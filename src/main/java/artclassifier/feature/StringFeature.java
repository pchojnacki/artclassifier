package artclassifier.feature;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.tokenizers.WordTokenizer;
import weka.filters.unsupervised.attribute.StringToWordVector;
import artclassifier.algorithm.Article;
import artclassifier.util.SnowballStemmer;
import de.abelssoft.wordtools.jwordsplitter.AbstractWordSplitter;
import de.abelssoft.wordtools.jwordsplitter.impl.GermanWordSplitter;

public abstract class StringFeature extends Feature {

	private AbstractWordSplitter splitter;

	public StringFeature(String featureName) {
		super(featureName);
		try {
			this.splitter = new GermanWordSplitter(true);
			this.splitter.setStrictMode(true);
		} catch (IOException e) {
			// TODO
			e.printStackTrace();
		}
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

	protected String prepare(String s) {
		s = this.replaceYears(s);
		s = this.replaceNumbers(s);
		s = this.removeNonCharacters(s);
		return s;
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

	protected String splitGermanWords(String s) {
		String[] words = s.split("\\s+");
		StringBuilder sb = new StringBuilder();
		for (String word : words) {
			sb.append(word).append(" ");
			for (String part : this.splitter.splitWord(word)) {
				if (!part.equals(word)) {
					sb.append(part).append(" ");
				}
			}
		}
		return sb.toString();
	}

	@Override
	public StringToWordVector getFilter() {
		StringToWordVector filter = new StringToWordVector();

		filter.setAttributeIndices("1");

		filter.setStemmer(new SnowballStemmer("english"));

		filter.setTokenizer(new WordTokenizer());

		filter.setLowerCaseTokens(true);

		filter.setUseStoplist(true);

		try {
			File temp = this.unpackStopwordsToTempFile();
			filter.setStopwords(temp);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return filter;
	}

	private File unpackStopwordsToTempFile() throws IOException, UnsupportedEncodingException, FileNotFoundException {
		// Extracting stop words to external file, because of Weka can't
		// read stop words from file, which included to jar archive
		File temp = File.createTempFile("stop_words_unpacked", ".tmp");

		// TODO: ability to configure path
		String stopwordsResource = "/lang/stop_words_en.txt";
		try (BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(stopwordsResource), "UTF-8"));
				PrintWriter pw = new PrintWriter(temp)) {
			String s;
			while ((s = br.readLine()) != null) {
				pw.println(s);
			}
		}
		return temp;
	}
}
