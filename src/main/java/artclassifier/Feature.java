package artclassifier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.filters.Filter;

public abstract class Feature {

	private Attribute attribute;

	private FeatureType type;

	public Feature(String featureName, FeatureType type) {
		this.type = type;
		switch (type) {
		case NUMERIC:
			this.attribute = new Attribute(featureName);
			break;

		case STRING:
			this.attribute = new Attribute(featureName, (FastVector) null);
			break;
		}
	}

	public final void addFeature(Article article, Instance instance) {
		switch (this.type) {
		case NUMERIC:
			instance.setValue(this.attribute, this.getNumericFeature(article));
			break;

		case STRING:
			instance.setValue(this.attribute, this.getStringFeature(article));
			break;
		}
	}

	protected double getNumericFeature(Article article) {
		throw new UnsupportedOperationException();
	}

	protected String getStringFeature(Article article) {
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

	public boolean hasFilter() {
		return false;
	}

	public Filter getFilter() {
		throw new UnsupportedOperationException();
	}

	public final Attribute getAttribute() {
		return this.attribute;
	}

	public enum FeatureType {
		NUMERIC,
		STRING
	}

}
