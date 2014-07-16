package artclassifier.wikitext;

/**
 * Taken from: https://github.com/Wikia/holmes/
 */
public class WikiPageTemplateArgument {
	private String name;
	private String stringValue;

	public WikiPageTemplateArgument(String name, String value) {
		this.name = name;
		this.stringValue = value;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStringValue() {
		return this.stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}
}
