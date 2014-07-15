package artclassifier.feature;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.filters.unsupervised.attribute.StringToWordVector;
import artclassifier.algorithm.Article;
import artclassifier.util.SnowballStemmer;
import artclassifier.util.SpaceTokenizer;

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
		String stopwordsFilePath = "/stop_words.txt";
		try {
			// Extracting stop words to external file, because of Weka can't
			// read stop words from file, which included to jar archive
			File temp = File.createTempFile("stop_words_unpacked", ".tmp");

			try (BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(stopwordsFilePath), "UTF-8"));
					PrintWriter pw = new PrintWriter(temp)) {
				String s;
				while ((s = br.readLine()) != null) {
					pw.println(s);
				}
			}

			filter.setStopwords(new File(temp.getAbsolutePath()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return filter;
	}
}
