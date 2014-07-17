package artclassifier.algorithm;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import artclassifier.wikitext.WikiPageFeatures;
import artclassifier.wikitext.WikiTextFeaturesHelper;

@JsonIgnoreProperties(ignoreUnknown = true, value = { "wikiPageFeatures" })
public class Article {

	private String wikiText;

	private String title;

	private String type;

	private WikiPageFeatures wikiPageFeatures;

	public String getWikiText() {
		return this.wikiText;
	}

	public void setWikiText(String wikiText) {
		this.wikiText = wikiText;
		this.initializeWikiPageFeatures();
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
		this.initializeWikiPageFeatures();
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public WikiPageFeatures getWikiPageFeatures() {
		return this.wikiPageFeatures;
	}

	private void initializeWikiPageFeatures() {
		if ((this.title != null) && (this.wikiText != null)) {
			try {
				this.wikiPageFeatures = WikiTextFeaturesHelper.parse(this.title, this.wikiText);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
