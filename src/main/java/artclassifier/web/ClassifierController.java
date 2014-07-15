package artclassifier.web;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import artclassifier.algorithm.Article;
import artclassifier.algorithm.ArticleClassifier;
import artclassifier.algorithm.ArticleClassifierService;
import artclassifier.algorithm.ClassificationResult;
import artclassifier.wikia.WikiaArticlesExtractor;

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

	@RequestMapping("/classify")
	@ResponseBody
	public String classify(@RequestParam String url) throws Exception {
		Article article = WikiaArticlesExtractor.getArticleByURL(url);

		List<ClassificationResult> result = null;
		try {
			this.classifierLock.lock();
			result = this.classifier.classifyWithDistribution(article);
		} finally {
			this.classifierLock.unlock();
		}

		StringBuilder sb = new StringBuilder();

		// TODO: use template or JSON response via Jackson

		sb.append("<a href=\"").append(url).append("\">").append(url).append("</a><br/><br/>");

		int i = 0;
		for (ClassificationResult entry : result) {
			if (i == 0) {
				sb.append("Article is about: <b>");
				sb.append(entry.getLabel()).append("</b><br/>");
				sb.append("</br>Other classes, sorted by relevance for this article<br/><br/>");
			} else {
				if ("other".equals(entry.getLabel())) {
					sb.append("<hr/>");
				}
				sb.append(i).append(". ").append(entry.getLabel()).append("<br/>");
			}
			i++;
		}

		return sb.toString();
	}

}
