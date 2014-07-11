package artclassifier;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.Classifier;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.PolyKernel;
import weka.classifiers.functions.supportVector.RBFKernel;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AttributeSelectedClassifier;
import weka.classifiers.trees.J48;
import weka.core.neighboursearch.KDTree;

@SuppressWarnings("unused")
public class Main {

	private static final String LABELED_ARTICLES_JSON_FILE = "/Users/yura/workspaces/holmes/training-data/2014.01.26.json";

	public static void main(String[] args) throws Exception {

		List<Article> labeledArticles = readLabeledArticles();

		Collections.shuffle(labeledArticles, new Random(10));

		int trainingSetSize = (labeledArticles.size() * 7) / 10;

		List<Article> trainingSet = labeledArticles.subList(0, trainingSetSize);

		List<Article> validationSet = labeledArticles.subList(trainingSetSize, labeledArticles.size());

		Classifier classifier = null;

		// see:
		// http://stackoverflow.com/questions/11482108/wekas-pca-is-taking-too-long-to-run/11793003#11793003
		// classifier = getAttributeSelectionClassifier(
		// getAttributeSelectionClassifier(getSVM(), new PrincipalComponents(),
		// 30),
		// new InfoGainAttributeEval(), 300);

		classifier = getAttributeSelectionClassifier(getSVM(), new InfoGainAttributeEval(), 300);

		boolean performCrossValidation = false;

		ArticleClassifier articleClassifier =
				new ArticleClassifier(trainingSet, validationSet, classifier, performCrossValidation);

		for (int i = 0; i < 10; i++) {
			Article article = validationSet.get(i);
			articleClassifier.calssify(article);
			Map<String, Double> result = articleClassifier.calssify(article);

			System.out.println(article.getTitle());
			System.out.println(result);
			System.out.println();
		}
	}

	private static List<Article> readLabeledArticles() throws IOException, JsonParseException, JsonMappingException {
		List<Article> articles = new ObjectMapper().readValue(
				new JsonFactory().createJsonParser(
						new File(LABELED_ARTICLES_JSON_FILE)),
						new TypeReference<List<Article>>() {
				});
		return articles;
	}

	private static Classifier getAttributeSelectionClassifier(Classifier c, ASEvaluation eval, int numToSelect) {
		AttributeSelectedClassifier classifier = new AttributeSelectedClassifier();
		classifier.setEvaluator(eval);

		Ranker ranker = new Ranker();
		ranker.setNumToSelect(numToSelect);
		classifier.setSearch(ranker);

		classifier.setClassifier(c);
		return classifier;
	}

	private static Classifier getSVM() {
		SMO smo = new SMO();

		PolyKernel polyKernel = new PolyKernel();
		polyKernel.setExponent(1.6);
		smo.setKernel(polyKernel);

		RBFKernel rbfKernel = new RBFKernel();
		rbfKernel.setGamma(0.03);
		// smo.setKernel(rbfKernel);

		// smo.setBuildLogisticModels(true);
		return smo;
	}

	private static Classifier getDecisionTree() {
		J48 j48 = new J48();
		j48.setUnpruned(false);
		j48.setMinNumObj(5);
		return j48;
	}

	private static Classifier getKnn() throws Exception {
		IBk knn = new IBk(7);
		KDTree kdTree = new KDTree();
		knn.setNearestNeighbourSearchAlgorithm(kdTree);
		knn.setOptions(new String[] { "-I" });
		return knn;
	}
}
