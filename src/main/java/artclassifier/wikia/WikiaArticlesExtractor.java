package artclassifier.wikia;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import artclassifier.Article;

public class WikiaArticlesExtractor {

	private String wikiaUrl;

	public WikiaArticlesExtractor(String wikiaUrl) {
		this.wikiaUrl = wikiaUrl;
	}

	public List<String> getTopArticlesIds() throws Exception {
		List<String> ids = new ArrayList<>();
		try (InputStream in =
				new URL(this.wikiaUrl + "/api/v1/Articles/Top").openStream()) {

			JsonNode response = new ObjectMapper().readValue(in, JsonNode.class);
			JsonNode items = response.get("items");
			items.forEach(item -> ids.add(item.get("id").asText()));
		}
		return ids;
	}

	public Article getArticle(String id) throws Exception {
		Article article = new Article();
		try (InputStream in =
				new URL(this.wikiaUrl + "/api.php?action=parse&format=json&prop=wikitext&pageid=" + id).openStream()) {

			JsonNode response = new ObjectMapper().readValue(in, JsonNode.class);
			JsonNode content = response.get("parse");

			String title = content.get("title").asText();
			String wikiText = content.get("wikitext").get("*").asText();
			article.setTitle(title);
			article.setWikiText(wikiText);
		}
		return article;
	}

}
