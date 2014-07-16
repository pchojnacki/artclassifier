package artclassifier.wikitext;

import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

import org.sweble.wikitext.engine.CompiledPage;
import org.sweble.wikitext.engine.Compiler;
import org.sweble.wikitext.engine.CompilerException;
import org.sweble.wikitext.engine.PageId;
import org.sweble.wikitext.engine.PageTitle;
import org.sweble.wikitext.engine.config.WikiConfigurationInterface;
import org.sweble.wikitext.engine.utils.SimpleWikiConfiguration;
import org.sweble.wikitext.lazy.LinkTargetException;

/**
 * Taken from: https://github.com/Wikia/holmes/
 */
public class WikiTextFeaturesHelper {
	// configuration is not thread safe but can be cached, so we're using thread
	// local.
	private static ThreadLocal<WikiConfigurationInterface> threadLocalConfig = new ThreadLocal<WikiConfigurationInterface>() {
		@Override
		protected WikiConfigurationInterface initialValue() {
			try {
				return new SimpleWikiConfiguration("classpath:/org/sweble/wikitext/engine/SimpleWikiConfiguration.xml");
			} catch (FileNotFoundException | JAXBException e) {
				throw new RuntimeException(e);
			}
		}
	};

	public static WikiPageFeatures parse(String title, String wikiText) throws FileNotFoundException, JAXBException, LinkTargetException, CompilerException {
		WikiConfigurationInterface configuration = threadLocalConfig.get();
		if (configuration == null) {
			throw new IllegalStateException("Cannot parse wikitext. WikiConfiguration not loaded.");
		}
		Compiler compiler = new Compiler(configuration);

		PageTitle pageTitle = PageTitle.make(configuration, title);
		PageId pageId = new PageId(pageTitle, -1);
		CompiledPage cp = compiler.postprocess(pageId, wikiText, null);
		WikitextReaderVisitor p = new WikitextReaderVisitor(title);
		p.go(cp.getPage());
		return p.getStructure();
	}
}
