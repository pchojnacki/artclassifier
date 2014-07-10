package artclassifier;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.Classifier;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.PolyKernel;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AttributeSelectedClassifier;
import weka.classifiers.trees.J48;
import weka.core.neighboursearch.KDTree;

public class Main {

	public static void main(String[] args) throws Exception {

		List<Article> articles = new ObjectMapper().readValue(
				new JsonFactory().createJsonParser(
						new File("/home/yurii/workspaces/holmes/training-data/2014.01.26.json")),
						new TypeReference<List<Article>>() {
				});

		Collections.shuffle(articles, new Random(10));

		int trainingSetSize = (articles.size() * 7) / 10;

		List<Article> trainingSet = articles.subList(0, trainingSetSize);
		List<Article> validationSet = articles.subList(trainingSetSize, articles.size());

		Classifier classifier = getAttributeSelectionClassifier(getSVM());

		new ArticleClassifier(trainingSet, validationSet, classifier);
	}

	private static Classifier getAttributeSelectionClassifier(Classifier c) {
		AttributeSelectedClassifier classifier = new AttributeSelectedClassifier();
		InfoGainAttributeEval infoGain = new InfoGainAttributeEval();
		classifier.setEvaluator(infoGain);
		Ranker ranker = new Ranker();
		// ranker.setNumToSelect(100);
		ranker.setThreshold(0.07);
		classifier.setSearch(ranker);
		classifier.setClassifier(c);
		return classifier;
	}

	private static Classifier getSVM() {
		SMO smo = new SMO();

		PolyKernel polyKernel = new PolyKernel();
		polyKernel.setExponent(1.5);
		// smo.setKernel(polyKernel);

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
