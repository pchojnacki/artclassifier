package artclassifier;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import artclassifier.algorithm.Article;
import artclassifier.util.WikiaArticlesDownloader;

// TODO: remove
public class TrainingSetCreator {

	public static void main(String[] args) throws Exception {
		// script1();
		script2();
	}

	private static void script2() throws IOException, JsonParseException, JsonMappingException, FileNotFoundException, JsonGenerationException {
		List<Article> articles = new ObjectMapper().readValue(new JsonFactory().createJsonParser(
				new FileInputStream("/home/yurii/de.json")),
				new TypeReference<List<Article>>() {
				});

		Map<String, String> labelsMap = new HashMap<>();
		labelsMap.put("book", "book");
		labelsMap.put("Book", "book");

		labelsMap.put("character", "character");
		labelsMap.put("Character", "character");

		labelsMap.put("other", "other");
		labelsMap.put("Other", "other");
		labelsMap.put("item", "other");
		labelsMap.put("Item", "other");
		labelsMap.put("Monster", "other");

		labelsMap.put("movie", "movie");
		labelsMap.put("Movie", "movie");

		labelsMap.put("person", "person");
		labelsMap.put("Person", "person");

		labelsMap.put("tv_episode", "tv_episode");
		labelsMap.put("Tv Episode", "tv_episode");

		labelsMap.put("tv_series", "tv_series");

		labelsMap.put("tv_season", "tv_season");
		labelsMap.put("tv_seasons", "tv_season");
		labelsMap.put("Tv Seasons", "tv_season");
		labelsMap.put("Tv Season", "tv_season");

		labelsMap.put("video_game", "video_game");

		for (Article a : articles) {
			String type = a.getType();
			type = labelsMap.get(type);
			a.setType(type);
		}

		ObjectMapper objectMapper = new ObjectMapper();
		FileOutputStream fos = new FileOutputStream("/home/yurii/de_training_set.json");
		objectMapper.writeValue(fos, articles);
		fos.flush();
		fos.close();
	}

	private static void script1() throws IOException, JsonParseException, JsonMappingException, Exception, FileNotFoundException, JsonGenerationException {
		Map<String, Integer> labelsCount = new HashMap<>();
		List<Article> articles = readLabeledArticles();

		articles.addAll(readFromTSV());

		for (Article a : articles) {
			Integer count = labelsCount.get(a.getType());
			if (count == null) {
				count = 0;
			}
			labelsCount.put(a.getType(), count + 1);
		}
		for (String s : labelsCount.keySet()) {
			System.out.println(s + "\t" + labelsCount.get(s));
		}

		ObjectMapper objectMapper = new ObjectMapper();
		FileOutputStream fos = new FileOutputStream("/home/yurii/de.json");
		objectMapper.writeValue(fos, articles);
		fos.flush();
		fos.close();
	}

	private static List<Article> readLabeledArticles() throws IOException, JsonParseException, JsonMappingException {
		List<Article> articles1 = new ObjectMapper().readValue(new JsonFactory().createJsonParser(
				new FileInputStream("/home/yurii/de_test.json")),
				new TypeReference<List<Article>>() {
				});

		List<Article> articles2 = new ObjectMapper().readValue(new JsonFactory().createJsonParser(
				new FileInputStream("/home/yurii/de_test3_Entertainment.json")),
				new TypeReference<List<Article>>() {
				});

		List<Article> articles = new ArrayList<>();
		articles.addAll(articles1);
		articles.addAll(articles2);
		return articles;
	}

	private static List<Article> readFromTSV() throws Exception {
		ExecutorService executor = Executors.newFixedThreadPool(10);
		final List<Article> articles = new ArrayList<>();
		List<Callable<Article>> tasks = new ArrayList<>();
		final AtomicInteger i = new AtomicInteger(0);
		try (BufferedReader br = new BufferedReader(new FileReader("/home/yurii/DE learning set - all-grouped.tsv"))) {
			String s;
			while ((s = br.readLine()) != null) {
				if (s.contains("http")) {
					final String s1 = s;
					tasks.add(new Callable<Article>() {
						@Override
						public Article call() throws Exception {
							String[] parts = s1.split("\t");
							Article a = WikiaArticlesDownloader.getArticleByURL(parts[0]);
							a.setType(parts[1]);
							articles.add(a);

							System.out.println(i.incrementAndGet());
							System.out.println(s1);
							return a;
						}
					});
				}
			}
		}
		List<Future<Article>> futures = executor.invokeAll(tasks);
		for (Future<Article> f : futures) {
			try {
				articles.add(f.get());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return articles;
	}

}
