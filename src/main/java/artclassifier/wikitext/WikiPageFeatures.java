package artclassifier.wikitext;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Taken from: https://github.com/Wikia/holmes/
 */
public class WikiPageFeatures {
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(WikiPageFeatures.class.toString());
	private String title = "";
	private String plain = "";
	private String summary = "";
	private final List<WikiPageCategory> categories = new ArrayList<>();
	private final List<WikiPageInternalLink> internalLinks = new ArrayList<>();
	private final List<WikiPageTemplateArgument> templateArguments = new ArrayList<>();
	private final List<WikiPageSection> sections = new ArrayList<>();
	private final List<WikiPageTemplate> templates = new ArrayList<>();

	public WikiPageFeatures(String title) {
		this.title = title;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPlain() {
		return this.plain;
	}

	public void setPlain(String plain) {
		this.plain = plain;
	}

	public List<WikiPageCategory> getCategories() {
		return this.categories;
	}

	public List<WikiPageInternalLink> getInternalLinks() {
		return this.internalLinks;
	}

	public List<WikiPageTemplateArgument> getTemplateArguments() {
		return this.templateArguments;
	}

	public List<WikiPageSection> getSections() {
		return this.sections;
	}

	public List<WikiPageTemplate> getTemplates() {
		return this.templates;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getSummary() {
		return this.summary;
	}
}
