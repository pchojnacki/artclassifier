package artclassifier.feature;

import weka.core.Attribute;
import weka.core.Instance;
import weka.filters.Filter;
import artclassifier.Article;

public abstract class Feature {

	private Attribute attribute;

	public Feature(String featureName) {
		this.attribute = this.initializeAttribute(featureName);
	}

	public abstract Attribute initializeAttribute(String featureName);

	public abstract void addFeature(Article article, Instance instance);

	public abstract boolean hasFilter();

	public abstract Filter getFilter();

	public final Attribute getAttribute() {
		return this.attribute;
	}
}
