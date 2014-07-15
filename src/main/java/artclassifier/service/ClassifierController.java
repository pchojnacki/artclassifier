package artclassifier.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import artclassifier.algorithm.Article;
import artclassifier.algorithm.ArticleClassifier;
import artclassifier.algorithm.ArticleClassifierService;
import artclassifier.util.WikiaArticlesDownloader;

@Controller
public class ClassifierController {

	private ReentrantLock classifierLock = new ReentrantLock();

	private ArticleClassifier classifier;

	@PostConstruct
	public void init() throws Exception {
		this.classifier =
				ArticleClassifierService.getArticleClassifier(false);
	}

	@RequestMapping("/ping")
	@ResponseBody
	public String ping() {
		return "Ok";
	}

	@RequestMapping(value = "/classify", method = { RequestMethod.GET })
	@ResponseBody
	public Map<String, Object> classifyByUrl(@RequestParam String url) throws Exception {
		Article article = WikiaArticlesDownloader.getArticleByURL(url);

		Map<String, Object> response = this.classifyToResponse(article);

		return response;
	}

	@RequestMapping(value = "/classify", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> classifyArticle(@RequestBody Article article) throws Exception {

		Map<String, Object> response = this.classifyToResponse(article);

		return response;
	}

	private Map<String, Object> classifyToResponse(Article article) throws Exception {
		Map<String, Double> classificationResult = null;
		try {
			this.classifierLock.lock();
			classificationResult = this.classifier.classifyWithDistribution(article);
		} finally {
			this.classifierLock.unlock();
		}

		String label = null;
		double probability = Double.NEGATIVE_INFINITY;
		for (String currentLabel : classificationResult.keySet()) {
			Double currentProbability = classificationResult.get(currentLabel);
			if (currentProbability > probability) {
				label = currentLabel;
				probability = currentProbability;
			}
		}

		Map<String, Object> response = new HashMap<>();
		response.put("class", label);
		response.put("classes", classificationResult);
		return response;
	}

}
