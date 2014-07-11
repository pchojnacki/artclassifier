package artclassifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.unsupervised.attribute.StringToWordVector;
import artclassifier.Feature.FeatureType;

public class ArticleClassifier {

	private static final Feature[] FEATURES = getFeatures();

	private static final Filter[] FILTERS = getFilters();

	private static final int ATTRIBUTES_COUNT = FEATURES.length + 1;

	private Attribute labelAttribute;

	private Classifier classifier;

	public ArticleClassifier(List<Article> trainingSetArticles,
			List<Article> validationSetArticles, Classifier classifier)
					throws Exception {

		this.labelAttribute = this.createLabelAttribute(trainingSetArticles);

		Instances trainingSet = this.createInstancesFromLabeledArticles(
				trainingSetArticles, "trainingSet");

		this.buildClassifier(trainingSet, classifier);

		System.out.println(this.classifier);

		// this.doCrossValidation(trainingSet, 10);

		this.evaluateOnValidationSet(validationSetArticles);
	}

	private void doCrossValidation(Instances trainingSet, int foldsNum) throws Exception {

		System.out.println();
		System.out.println("Cross validation on training set");
		System.out.println();

		Evaluation cvEaluation = new Evaluation(trainingSet);
		cvEaluation.crossValidateModel(this.classifier, trainingSet, foldsNum, new Random(1));
		System.out.println(cvEaluation.toSummaryString());
	}

	private void evaluateOnValidationSet(List<Article> validationSetArticles) throws Exception {

		System.out.println();
		System.out.println("Evaluating on validation set");
		System.out.println();

		Instances validationSet = this.createInstancesFromLabeledArticles(
				validationSetArticles, "validationSet");

		Evaluation eval = new Evaluation(validationSet);
		eval.evaluateModel(this.classifier, validationSet);
		System.out.println(eval.toSummaryString());
		System.out.println(eval.toMatrixString());

		System.out.println();
		System.out.println("FP\tFN\tF-score\tRecall\tPrecision\tLabel");
		for (int i = 0; i < this.labelAttribute.numValues(); i++) {
			String label = this.labelAttribute.value(i);

			System.out.printf("%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%s\n",
					eval.falsePositiveRate(i),
					eval.falseNegativeRate(i),
					eval.fMeasure(i),
					eval.recall(i),
					eval.precision(i),
					label);
		}
	}

	private void buildClassifier(Instances trainingSet, Classifier classifier)
			throws Exception {
		System.out.println();
		if (FILTERS != null) {
			MultiFilter multiFilter = new MultiFilter();
			multiFilter.setInputFormat(trainingSet);
			multiFilter.setFilters(FILTERS);
			FilteredClassifier filteredClassifier = new FilteredClassifier();
			filteredClassifier.setFilter(multiFilter);
			filteredClassifier.setClassifier(classifier);
			this.classifier = filteredClassifier;
		} else {
			this.classifier = classifier;
		}

		this.classifier.buildClassifier(trainingSet);
	}

	private Instances createInstancesFromLabeledArticles(
			List<Article> labeledArticles, String datasetName) {
		Instances instances = this.createEmptyInstancesSet(datasetName);
		for (Article article : labeledArticles) {
			Instance instance = this.articleToInstance(article);
			instance.setValue(this.labelAttribute, article.getType());
			instances.add(instance);
		}
		return instances;
	}

	private Instance articleToInstance(Article article) {
		Instance instance = new Instance(ATTRIBUTES_COUNT);
		for (Feature feature : FEATURES) {
			feature.addFeature(article, instance);
		}
		return instance;
	}

	private Instances createEmptyInstancesSet(String datasetName) {
		FastVector attributes = new FastVector(ATTRIBUTES_COUNT);
		for (Feature feature : FEATURES) {
			attributes.addElement(feature.getAttribute());
		}
		attributes.addElement(this.labelAttribute);
		Instances instances = new Instances(datasetName, attributes, 1);
		instances.setClass(this.labelAttribute);
		return instances;
	}

	private Attribute createLabelAttribute(List<Article> labeledArticles) {
		Set<String> labels = new HashSet<>();
		for (Article article : labeledArticles) {
			labels.add(article.getType());
		}

		FastVector labelsVector = new FastVector(labels.size());
		for (String label : labels) {
			labelsVector.addElement(label);
		}
		Attribute attr = new Attribute("tagAttribute", labelsVector);
		return attr;
	}

	private static Filter[] getFilters() {
		List<Filter> filters = new ArrayList<>();
		for (Feature feature : FEATURES) {
			if (feature.hasFilter()) {
				filters.add(feature.getFilter());
			}
		}

		if (filters.isEmpty()) {
			return null;
		}

		return filters.toArray(new Filter[] {});
	}

	private static Feature[] getFeatures() {

		return new Feature[] {

				new Feature("wikiTextCleaned", FeatureType.STRING) {

					@Override
					protected String getStringFeature(Article article) {
						String wikiText = article.getWikiText();
						wikiText = wikiText.replaceAll("(19|20)\\d{2}", " year ");
						wikiText = wikiText.replaceAll("\\d+", " number ");
						wikiText = wikiText.replaceAll("\\P{L}+", " ");
						return wikiText;
					}

					@Override
					public boolean hasFilter() {
						return true;
					}

					@Override
					public Filter getFilter() {
						StringToWordVector filter = new StringToWordVector();
						filter.setAttributeIndices("1");
						filter.setAttributeNamePrefix("wikiTextCleaned_");
						filter.setTFTransform(true);
						filter.setIDFTransform(true);
						filter.setStemmer(new SnowballStemmer());
						filter.setTokenizer(new SpaceTokenizer());
						filter.setLowerCaseTokens(true);
						filter.setUseStoplist(true);
						filter.setMinTermFreq(5);
						filter.setStopwords(new File(
								"/home/yurii/sandbox/artclassifier/src/main/resources/stop_words/stop_words.txt"));
						return filter;
					}
				},

				new Feature("titleTextCleaned", FeatureType.STRING) {

					@Override
					protected String getStringFeature(Article article) {
						String title = article.getTitle();
						title = title.replaceAll("(19|20)\\d{2}", " year ");
						title = title.replaceAll("\\d+", " number ");
						title = title.replaceAll("\\P{L}+", " ");
						return title;
					}

					@Override
					public boolean hasFilter() {
						return true;
					}

					@Override
					public Filter getFilter() {
						StringToWordVector filter = new StringToWordVector();
						filter.setAttributeIndices("1");
						filter.setAttributeNamePrefix("titleTextCleaned_");
						filter.setStemmer(new SnowballStemmer());
						filter.setTokenizer(new SpaceTokenizer());
						filter.setLowerCaseTokens(true);
						filter.setUseStoplist(true);
						filter.setStopwords(new File(
								"/home/yurii/sandbox/artclassifier/src/main/resources/stop_words/stop_words.txt"));
						return filter;
					}
				},

				new Feature("numberOfWordsInTitle", FeatureType.NUMERIC) {
					@Override
					protected double getNumericFeature(Article article) {
						String title = article.getTitle();
						int occurences = this.countRegrexp(title, "[^s]{3,}");
						return occurences;
					}
				},

				new Feature("numberOfYearsInWikiText", FeatureType.NUMERIC) {
					@Override
					protected double getNumericFeature(Article article) {
						String wikiText = article.getWikiText();
						int occurences = this.countRegrexp(wikiText, "(19|20)\\d{2}");
						return occurences;
					}
				},

				new Feature("numberOfNamesInTitle", FeatureType.NUMERIC) {
					private Set<String> names = new HashSet<>();
					{
						try (BufferedReader br = new BufferedReader(new FileReader(
								"/home/yurii/sandbox/artclassifier/src/main/resources/names/names.txt"))) {
							String s;
							while ((s = br.readLine()) != null) {
								this.names.add(s);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					protected double getNumericFeature(Article article) {
						String title = article.getTitle();
						String[] parts = title.split("\\P{L}+");
						int count = 0;
						for (String p : parts) {
							if (this.names.contains(p)) {
								count++;
							}
						}
						return count;
					}
				},

				new Feature("numberOfNamesInWikiText", FeatureType.NUMERIC) {
					private Set<String> names = new HashSet<>();
					{
						try (BufferedReader br = new BufferedReader(new FileReader(
								"/home/yurii/sandbox/artclassifier/src/main/resources/names/names.txt"))) {
							String s;
							while ((s = br.readLine()) != null) {
								this.names.add(s);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					protected double getNumericFeature(Article article) {
						String wikiText = article.getWikiText();
						String[] parts = wikiText.split("\\P{L}+");
						int count = 0;
						for (String p : parts) {
							if (this.names.contains(p)) {
								count++;
							}
						}
						return count;
					}
				},
		};
	}
}
