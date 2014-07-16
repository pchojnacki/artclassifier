package artclassifier.wikitext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sweble.wikitext.engine.Page;
import org.sweble.wikitext.lazy.encval.IllegalCodePoint;
import org.sweble.wikitext.lazy.parser.Bold;
import org.sweble.wikitext.lazy.parser.ExternalLink;
import org.sweble.wikitext.lazy.parser.HorizontalRule;
import org.sweble.wikitext.lazy.parser.ImageLink;
import org.sweble.wikitext.lazy.parser.InternalLink;
import org.sweble.wikitext.lazy.parser.Italics;
import org.sweble.wikitext.lazy.parser.MagicWord;
import org.sweble.wikitext.lazy.parser.Paragraph;
import org.sweble.wikitext.lazy.parser.Section;
import org.sweble.wikitext.lazy.parser.Table;
import org.sweble.wikitext.lazy.parser.TableCell;
import org.sweble.wikitext.lazy.parser.TableHeader;
import org.sweble.wikitext.lazy.parser.TableRow;
import org.sweble.wikitext.lazy.parser.Whitespace;
import org.sweble.wikitext.lazy.parser.XmlElement;
import org.sweble.wikitext.lazy.preprocessor.TagExtension;
import org.sweble.wikitext.lazy.preprocessor.Template;
import org.sweble.wikitext.lazy.preprocessor.TemplateArgument;
import org.sweble.wikitext.lazy.preprocessor.TemplateParameter;
import org.sweble.wikitext.lazy.preprocessor.XmlComment;

import de.fau.cs.osr.ptk.common.AstVisitor;
import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.ptk.common.ast.ContentNode;
import de.fau.cs.osr.ptk.common.ast.NodeList;
import de.fau.cs.osr.ptk.common.ast.StringContentNode;
import de.fau.cs.osr.ptk.common.ast.Text;

/**
 * Taken from: https://github.com/Wikia/holmes/
 */
@SuppressWarnings("unused")
public class WikitextReaderVisitor extends AstVisitor {
	private static Logger logger = LoggerFactory.getLogger(WikitextReaderVisitor.class.toString());
	private final StringBuilder stringBuilder = new StringBuilder();
	private final StringBuilder abstractStringBuilder = new StringBuilder();
	private final WikiPageFeatures wikiPageFeatures;
	private long templateDepth = 0;

	public WikitextReaderVisitor(String title) {
		this.wikiPageFeatures = new WikiPageFeatures(title);
		this.stringBuilder.append(title).append("\n\n");
	}

	@Override
	protected boolean before(AstNode node) {
		return super.before(node);
	}

	@Override
	protected Object after(AstNode node, Object result) {
		return super.after(node, result);
	}

	private void emmitPlain(String plainText) {
		this.stringBuilder.append(plainText);
		if ((this.abstractStringBuilder.length() < 500) && (this.templateDepth == 0)) {
			this.abstractStringBuilder.append(plainText);
		}
	}

	public WikiPageFeatures getStructure() {
		this.wikiPageFeatures.setPlain(this.stringBuilder.toString());
		String summary = this.abstractStringBuilder.toString();
		if (summary.length() > 1000) {
			summary = summary.substring(0, 1000);
		}
		this.wikiPageFeatures.setSummary(summary);
		return this.wikiPageFeatures;
	}

	// / VISITORS

	public void visit(AstNode node) {
		// logger.info("Node: " + node.getNodeName());
	}

	public void visit(Section section) {
		// logger.info("Section: " + section.getTitle());

		this.wikiPageFeatures.getSections().add(new WikiPageSection(this.asPlain(section.getTitle()).trim()));
		this.emmitPlain("\n");
		this.iterate(section.getTitle());
		/*
		 * for(AstNode node: section.getTitle()) { if(node instanceof Text) {
		 * Text textNode = (Text) node; emmitPlain(textNode.getContent()); }
		 * else if( node instanceof Bold ) { } else if( node instanceof Italics
		 * ) { } else if( node instanceof InternalLink ) {
		 *
		 * } else { logger.info("Unexpected element in Section title: " +
		 * node.getNodeName()); } }
		 */
		this.emmitPlain("\n");

		this.iterate(section.getBody());
	}

	public void visit(Paragraph p)
	{
		// logger.debug("Paragraph: ");
		this.iterate(p.getContent());
		this.emmitPlain("\n");
	}

	public void visit(Text text)
	{
		// logger.debug("Text: " + text.getContent().length());
		this.emmitPlain(text.getContent());
	}

	public void visit(HorizontalRule hr)
	{
		// logger.info("HorizontalRule: " + hr.getNodeTypeName());
	}

	public void visit(XmlElement e)
	{
		// logger.debug("XmlElement: " + e.getNodeTypeName());
		this.iterate(e.getBody());
	}

	public void visit(NodeList n)
	{
		this.iterate(n);
	}

	public void visit(Page p)
	{
		this.iterate(p.getContent());
	}

	public void visit(ImageLink imageLink)
	{
	}

	public void visit(IllegalCodePoint illegalCodePoint)
	{
	}

	public void visit(XmlComment xmlComment)
	{
	}

	public void visit(Template template)
	{
		try {
			this.templateDepth++;
			// logger.debug("Template" + template.getName() + " " +
			// template.getChildNames());
			this.wikiPageFeatures.getTemplates().add(new WikiPageTemplate(this.asPlain(template.getName()), template.getChildNames()));
			this.iterate(template.getArgs());
		} finally {
			this.templateDepth--;
		}
	}

	public void visit(TemplateArgument templateArgument)
	{
		String name = this.asPlain(templateArgument.getName());
		String value = this.asPlain(templateArgument.getValue());
		this.wikiPageFeatures.getTemplateArguments().add(new WikiPageTemplateArgument(name, value));
		// logger.debug("Template argument" + templateArgument.getName() + " = "
		// + templateArgument.getValue());
		this.iterate(templateArgument.getName());
		this.emmitPlain(":\n");
		this.iterate(templateArgument.getValue());
		this.emmitPlain("\n");
	}

	public void visit(TemplateParameter templateParameter)
	{
		// logger.info("Template parameter" + templateParameter.getName() +
		// " = " + templateParameter.getDefaultValue());
	}

	public void visit(TagExtension n)
	{
	}

	public void visit(Table table) {
		this.emmitPlain("\n");
		this.iterate(table.getBody());
		this.emmitPlain("\n");
	}

	public void visit(TableHeader tableHeader) {
		this.iterate(tableHeader.getBody());
	}

	public void visit(TableRow tableRow) {
		this.iterate(tableRow.getBody());
	}

	public void visit(TableCell tableCell) {
		this.iterate(tableCell.getBody());
	}

	public void visit(MagicWord magicWord)
	{
		// logger.info("magic word" + magicWord.getWord());
	}

	public void visit(InternalLink link) {
		if (link.getTarget().startsWith("Category:")) {
			this.visitCategory(link);
		} else {
			// logger.debug("Internal Link: " + link.getTitle().getNodeName() +
			// " " + link.getTarget());
			this.wikiPageFeatures.getInternalLinks().add(
					new WikiPageInternalLink(this.asPlain(link.getTitle().getContent()), link.getTarget()));
			this.iterate(link.getTitle().getContent());
			this.emmitPlain(" " + link.getTarget());
		}
	}

	public void visitCategory(InternalLink categoryLink) {
		String title = this.asPlain(categoryLink.getTitle().getContent());
		String target = categoryLink.getTarget().replaceAll("Category:", "");
		this.emmitPlain(target + " " + title);
		WikiPageCategory category = new WikiPageCategory(target);
		this.wikiPageFeatures.getCategories().add(category);
	}

	public void visit(ExternalLink link) {
		// logger.debug("External Link: " + link.getTitle().getNodeName() + " "
		// + link.getTarget());
		// logger.debug("             : " + link.getTitle());
		this.iterate(link.getTitle());
	}

	public void visit(Bold b)
	{
		this.iterate(b.getContent());
	}

	public void visit(Italics i)
	{
		this.iterate(i.getContent());
	}

	public void visit(Whitespace w)
	{
		this.emmitPlain(" ");
	}

	private String asPlain(NodeList content) {
		StringBuilder sb = new StringBuilder();
		for (AstNode node : content) {
			if (node instanceof StringContentNode) {
				sb.append(((StringContentNode) node).getContent());
			} else if (node instanceof Whitespace) {
				sb.append(" ");
			} else if (node instanceof InternalLink) {
				sb.append(this.asPlain(((InternalLink) node).getTitle().getContent()));
			} else if (node instanceof ContentNode) {
				sb.append(this.asPlain(((ContentNode) node).getContent()));
			}
		}
		return sb.toString();
	}
}
