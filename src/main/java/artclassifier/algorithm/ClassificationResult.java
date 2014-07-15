package artclassifier.algorithm;

public class ClassificationResult implements Comparable<ClassificationResult> {

	String label;

	double relevance;

	public ClassificationResult(String label, double relevance) {
		this.label = label;
		this.relevance = relevance;
	}

	public String getLabel() {
		return this.label;
	}

	public double getRelevance() {
		return this.relevance;
	}

	@Override
	public int compareTo(ClassificationResult o) {
		return Double.compare(this.relevance, o.relevance);
	}
}