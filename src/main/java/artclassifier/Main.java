package artclassifier;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import weka.classifiers.functions.SMO;

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

		new ArticleClassifier(trainingSet, validationSet, new SMO());
	}
}
