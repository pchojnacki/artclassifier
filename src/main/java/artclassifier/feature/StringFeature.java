package artclassifier.feature;

import artclassifier.Article;
import artclassifier.util.SnowballStemmer;
import artclassifier.util.SpaceTokenizer;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.File;
import java.net.URISyntaxException;

public abstract class StringFeature extends Feature {

	public StringFeature(String featureName) {
		super(featureName);
	}

	@Override
	public Attribute initializeAttribute(String featureName) {
		return new Attribute(featureName, (FastVector) null);
	}

	@Override
	public void addFeature(Article article, Instance instance) {
		instance.setValue(this.getAttribute(), this.calculate(article));
	}

	protected abstract String calculate(Article article);

	@Override
	public boolean hasFilter() {
		return true;
	}

	protected String replaceYears(String s) {
		return s.replaceAll("(19|20)\\d{2}", " year ");
	}

	protected String replaceNumbers(String s) {
		return s.replaceAll("\\d+", " number ");
	}

	protected String removeNonCharacters(String s) {
		return s.replaceAll("\\P{L}+", " ");
	}

	@Override
	public StringToWordVector getFilter() {
		StringToWordVector filter = new StringToWordVector();

		filter.setAttributeIndices("1");

		filter.setStemmer(new SnowballStemmer());

		filter.setTokenizer(new SpaceTokenizer());

		filter.setLowerCaseTokens(true);

		filter.setUseStoplist(true);

		// TODO: ability to configure path
		String stopwordsFilePath = "/stop_words/stop_words.txt";
		try {
			filter.setStopwords(new File(this.getClass().getResource(stopwordsFilePath).toURI()));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return filter;
	}

}
