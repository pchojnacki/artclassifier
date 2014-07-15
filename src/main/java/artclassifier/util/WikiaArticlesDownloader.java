package artclassifier.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import artclassifier.algorithm.Article;

public class WikiaArticlesDownloader {

	private String wikiaUrl;

	public WikiaArticlesDownloader(String wikiaUrl) {
		this.wikiaUrl = wikiaUrl;
	}

	public List<String> getTopArticlesIds() throws Exception {
		List<String> ids = new ArrayList<>();
		try (InputStream in =
				new URL(this.wikiaUrl + "/api/v1/Articles/Top").openStream()) {

			JsonNode response = new ObjectMapper().readValue(in, JsonNode.class);
			JsonNode items = response.get("items");
			for (JsonNode item : items) {
				ids.add(item.get("id").asText());
			}
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

	public static Article getArticleByURL(String url) throws Exception {
		String articleId = null;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new URL(url).openStream()))) {
			StringBuilder pageBuilder = new StringBuilder();
			String s;
			while ((s = br.readLine()) != null) {
				pageBuilder.append(s);
			}
			String page = pageBuilder.toString();
			articleId = page.replaceAll("^.*wgArticleId\\s*=\\s*([^\\s,]+).*$", "$1");
		}
		String wikiaUrl = url.replaceAll("^(http://[^\\.]+\\.wikia\\.com).*", "$1");

		// log
		// TODO: remove
		System.out.println("wikia url is " + wikiaUrl);
		System.out.println("article id is " + articleId);
		System.out.println();

		if (articleId == null) {
			// TODO
			throw new RuntimeException();
		}

		return new WikiaArticlesDownloader(wikiaUrl).getArticle(articleId);
	}
}
