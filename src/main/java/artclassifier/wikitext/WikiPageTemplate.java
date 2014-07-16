package artclassifier.wikitext;

import java.util.ArrayList;
import java.util.List;

/**
 * Taken from: https://github.com/Wikia/holmes/
 */
public class WikiPageTemplate {
	private final List<String> childNames;
	private String name;

	public WikiPageTemplate(String name, String[] childNames) {
		this.name = name;
		this.childNames = new ArrayList<>();
		for (String childName : childNames) {
			this.childNames.add(childName);
		}
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getChildNames() {
		return this.childNames;
	}
}
