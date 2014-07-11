package artclassifier.feature;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import weka.core.Attribute;
import weka.core.Instance;
import weka.filters.Filter;
import artclassifier.Article;

public abstract class NumericFeature extends Feature {

	public NumericFeature(String featureName) {
		super(featureName);
	}

	@Override
	public Attribute initializeAttribute(String featureName) {
		return new Attribute(featureName);
	}

	@Override
	public void addFeature(Article article, Instance instance) {
		instance.setValue(this.getAttribute(), this.calculate(article));
	}

	protected abstract double calculate(Article article);

	@Override
	public boolean hasFilter() {
		return false;
	}

	@Override
	public Filter getFilter() {
		throw new UnsupportedOperationException();
	}

	protected int countRegrexp(String s, String regexp) {
		int count = 0;
		Pattern pattern = Pattern.compile(regexp);
		Matcher matcher = pattern.matcher(s);
		while (matcher.find()) {
			count++;
		}
		return count;
	}
}
