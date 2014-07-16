package artclassifier.wikitext;

import org.apache.commons.lang.StringUtils;

/**
 * Taken from: https://github.com/Wikia/holmes/
 */
public class WikiPageInternalLink {
	private String title;
	private String to;

	public WikiPageInternalLink(String title, String to) {
		this.title = title;
		this.to = to;
		if (StringUtils.isEmpty(title)) {
			this.title = to;
		}
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTo() {
		return this.to;
	}

	public void setTo(String to) {
		this.to = to;
	}
}
