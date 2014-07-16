package artclassifier.wikitext;

/**
 * Taken from: https://github.com/Wikia/holmes/
 */
public class WikiPageSection {
	private String title;

	public WikiPageSection(String title) {
		this.title = title;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
