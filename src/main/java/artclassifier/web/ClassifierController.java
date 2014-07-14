package artclassifier.web;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import artclassifier.ArticleClassifier;
import artclassifier.ArticleClassifierService;

@Controller
public class ClassifierController {

	private ArticleClassifier classifier;

	@PostConstruct
	public void init() throws Exception {
		this.classifier = ArticleClassifierService.getArticleClassifier();
	}

	@RequestMapping("/ping")
	@ResponseBody
	public String ping() {
		return "Ok";
	}

}
