package artclassifier;

import weka.core.tokenizers.Tokenizer;

public class SpaceTokenizer extends Tokenizer {

	private static final long serialVersionUID = 1L;

	private String[] tokens;

	private int position;

	@Override
	public void tokenize(String s) {
		this.tokens = s.split("\\s+");
		this.position = 0;
	}

	@Override
	public Object nextElement() {
		return this.tokens[this.position++];
	}

	@Override
	public boolean hasMoreElements() {
		return this.position < this.tokens.length;
	}

	@Override
	public String getRevision() {
		return null;
	}

	@Override
	public String globalInfo() {
		return null;
	}
}
