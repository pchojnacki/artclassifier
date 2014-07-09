package artclassifier;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

public class App
{
	public static void main(String[] args) throws Exception {

		List<Article> articles = new ObjectMapper().readValue(
				new JsonFactory().createJsonParser(
						new File("/home/yurii/workspaces/holmes/training-data/2014.01.26.json")),
				new TypeReference<List<Article>>() {
				});

		Map<String, Integer> typeCount = new HashMap<>();

		for (Article a : articles) {
			System.out.println(a.getTitle());
			String type = a.getType();
			System.out.println(type);

			Integer count = typeCount.get(type);
			if (count == null) {
				typeCount.put(type, 1);
			} else {
				typeCount.put(type, count + 1);
			}

			System.out.println();
		}

		System.out.println(articles.size());

		for (String type : typeCount.keySet()) {
			System.out.println(type + "\t" + typeCount.get(type));
		}
	}
}
