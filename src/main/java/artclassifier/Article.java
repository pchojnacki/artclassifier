package artclassifier;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Article {

	private String wikiText;

	private String title;

	private long pageId;

	private long wikiId;

	private String type;

	public String getWikiText() {
		return this.wikiText;
	}

	public void setWikiText(String wikiText) {
		this.wikiText = wikiText;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getPageId() {
		return this.pageId;
	}

	public void setPageId(long pageId) {
		this.pageId = pageId;
	}

	public long getWikiId() {
		return this.wikiId;
	}

	public void setWikiId(long wikiId) {
		this.wikiId = wikiId;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
