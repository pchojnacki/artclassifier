package artclassifier.web;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import artclassifier.Article;
import artclassifier.ArticleClassifier;
import artclassifier.ArticleClassifier.ClassificationResult;
import artclassifier.ArticleClassifierService;
import artclassifier.wikia.WikiaArticlesExtractor;

@Controller
public class ClassifierController {

	private ArticleClassifier classifier;

	@PostConstruct
	public void init() throws Exception {
		this.classifier = ArticleClassifierService.getArticleClassifier(false);
	}

	@RequestMapping("/ping")
	@ResponseBody
	public String ping() {
		return "Ok";
	}

	private ReentrantLock lock = new ReentrantLock();

	@RequestMapping("/classify")
	@ResponseBody
	public String classify(@RequestParam String url) throws Exception {
		Article article = WikiaArticlesExtractor.getArticleByURL(url);

		List<ClassificationResult> result = null;
		try {
			this.lock.lock();
			result = this.classifier.classifyWithDistribution(article);
		} finally {
			this.lock.unlock();
		}

		StringBuilder sb = new StringBuilder();

		// TODO: use template or JSON response via Jackson

		sb.append("<a href=\"").append(url).append("\">").append(url).append("</a><br/><br/>");

		int i = 0;
		for (ClassificationResult entry : result) {
			if (i == 0) {
				sb.append("Article is about: <b>");
				sb.append(entry.label).append("</b><br/>");
				sb.append("</br>Other classes, sorted by relevance for this article<br/><br/>");
			} else {
				if ("other".equals(entry.label)) {
					sb.append("<hr/>");
				}
				sb.append(i).append(". ").append(entry.label).append("<br/>");
			}
			i++;
		}

		return sb.toString();
	}

}
