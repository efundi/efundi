package org.sakaiproject.lessonbuildertool.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.docx4j.Docx4J;
import org.docx4j.Docx4jProperties;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sakaiproject.lessonbuildertool.SimplePage;
import org.sakaiproject.lessonbuildertool.tool.view.ImportDocxViewParameters;

/**
 * Convenience methods and helpers for Import DOCX files and building a Lesson
 *
 * @author OpenCollab
 */
public class ImportHelper {

    /**
     * Create a .HTML file when given a .DOCX file
     *
     * @param siteId
     * @param file
     * @return
     * @throws org.docx4j.openpackaging.exceptions.Docx4JException
     * @throws java.io.FileNotFoundException
     */
    public File docxToHtml(String siteId, File file) throws Docx4JException, FileNotFoundException {
        WordprocessingMLPackage wordMLPackage;
        wordMLPackage = Docx4J.load(file);
        HTMLSettings htmlSettings = getDocxHtmlSettings(file.getPath());

        htmlSettings.setWmlPackage(wordMLPackage);
        OutputStream os;
        String fullFilePath = file.getPath() + ".html ";
        os = new FileOutputStream(fullFilePath);

        // If you want XHTML output
        Docx4jProperties.setProperty("docx4j.Convert.Out.HTML.OutputMethodXML", true);
        Docx4J.toHTML(htmlSettings, os, Docx4J.FLAG_EXPORT_PREFER_XSL);
        return new File(fullFilePath);
    }

    /**
     * Default HTMLSettings, convenience method
     *
     * @param filePath
     * @return
     */
    public HTMLSettings getDocxHtmlSettings(String filePath) {
        HTMLSettings htmlSettings = Docx4J.createHTMLSettings();
        htmlSettings.setImageDirPath(filePath + "_files");
        htmlSettings.setImageTargetUri(filePath.substring(filePath.lastIndexOf("/") + 1)
                + "_files");
        String userCSS = "html, body, div, span, h1, h2, h3, h4, h5, h6, p, a, img,  ol, ul, li, table, caption, tbody, tfoot, thead, tr, th, td "
                + "{ margin: 0; padding: 0; border: 0;}"
                + "body {line-height: 1;} ";
        htmlSettings.setUserCSS(userCSS);
        return htmlSettings;
    }

    /**
     * Parses through the HTML building a tree structure with simpler access to
     * the content
     *
     * Lots of rules violations in the documents provided to test. We will need
     * to got a formal complete rule sheet from NWU. before making further
     * sweeping changes.
     *
     * @param doc
     * @param params
     * @return
     */
    public Map<Long, ImportTreeItem> buildStructure(Document doc, ImportDocxViewParameters params) {
        Map<Long, ImportTreeItem> structure = new LinkedHashMap<Long, ImportTreeItem>();
        List<String> containerElements = new ArrayList<String>();
        containerElements.add("table");
        long levelParent = 0;
        String parentTitle = "";
        int sequence = 1;
        int whiteSpaceCounter = 0;
        ImportTreeItem iti = new ImportTreeItem();
        BreakOutObj boo = new BreakOutObj();
        Elements content = doc.select("p");
        int depthCount = 1;
        for (Element e : content) {
            boo = isAChildOf(doc, e, containerElements, boo, 0);
            if (boo.isChildOfBreakOutContainer()) {
                if (null != boo.getParentContainer() && depthCount != boo.getDepth()) {
                    e = boo.getParentContainer();
                    whiteSpaceCounter = addContentToIT(iti, e, structure, whiteSpaceCounter);
                    depthCount = boo.getDepth();
                }
            } else {
                if (e.attr("class").startsWith("Heading") && hasContent(e)) {
                    iti = addSubPageAndContent(e, levelParent, parentTitle, params, iti, structure, sequence, false);
                    levelParent = iti.getId();
                    parentTitle = iti.getTitle();
                    sequence = sequence + 1;
                    whiteSpaceCounter = 0;
                } else {
                    whiteSpaceCounter = addContentToIT(iti, e, structure, whiteSpaceCounter);
                }
                depthCount = 0;
            }
        }
        structure.put(iti.getId(), iti);
        return structure;
    }

    /**
     * adds content from many paragraph which do not require their own sub
     * pages.
     *
     * @param it
     * @param e
     * @param structure
     * @param isBreakOut
     */
    private int addContentToIT(ImportTreeItem iti, Element e, Map<Long, ImportTreeItem> structure, int whiteSpaceCounter) {
        if (hasContent(e)) {
            iti.setContent(iti.getContent().concat(e.text()));
            iti.setHtmlContent(iti.getHtmlContent().concat(e.toString()));
            whiteSpaceCounter = 0;
        } else {
            whiteSpaceCounter = whiteSpaceCounter + 1;
        }
        if (!hasContent(e) && whiteSpaceCounter <= 2) {
            iti.setContent(iti.getContent().concat(e.text()));
            iti.setHtmlContent(iti.getHtmlContent().concat(e.toString()));
        }
        return whiteSpaceCounter;
    }

    /**
     * Creates a subpage and inserts content into the subpage.
     *
     * @param e
     * @param levelParent
     * @param parentTitle
     * @param params
     * @param it
     * @param structure
     * @param sequence
     * @param breakOut
     * @return
     */
    private ImportTreeItem addSubPageAndContent(Element e, long levelParent, String parentTitle,
            ImportDocxViewParameters params, ImportTreeItem it, Map<Long, ImportTreeItem> structure,
            int sequence, boolean breakOut) {
        int level = (breakOut) ? 0 : Integer.parseInt(e.attr("class").substring("Heading".length(), "Heading".length() + 2).trim());
        if (level == 1) {
            levelParent = 0;
            parentTitle = FilenameUtils.getBaseName(params.getOriginalFileName());
        }
        it = determineAncestory(it, structure, levelParent);
        String title;
        if (e.text().length() > 50) {
            title = e.text().substring(0, 50) + " ...";
        } else {
            title = e.text();
        }
        if (!title.equals(it.getTitle())) {
            structure.put(it.getId(), it);
            it = new ImportTreeItem();
        }
        it.setLevel(level);
        it.setTitle(title);
//        if(includeSubPageHeadingInContent){
//            it.setHtmlContent(MessageFormat.format(heading, title));
//        }
        it.setParentId(levelParent);
        it.setParentTitle(parentTitle);
        it.setSequence(sequence);
        it.setType(1);
        it.setId(sequence);
        it = determineAncestory(it, structure, levelParent);
        return it;
    }

    /**
     * Update the ids of the tree items to the newly created pages.
     *
     * @param structure
     * @param page
     * @param it
     * @param id_map
     */
    public void updateIds(Map<Long, ImportTreeItem> structure, SimplePage page, ImportTreeItem it, HashMap<Long, Long> id_map) {
        for (Map.Entry<Long, ImportTreeItem> entry : structure.entrySet()) {
            ImportTreeItem iTree = entry.getValue();
            long oldId = it.getId();
            if (iTree.getId() == oldId) {
                iTree.setId(page.getPageId());
            }
            if (iTree.getParentId() == oldId) {
                iTree.setParentId(page.getPageId());
            }
            id_map.put(oldId, page.getPageId());
        }
    }

    /**
     * The isAChildOf determines whether the current paragraph is a child of a
     * container which cannot be broken up into smaller chunks even if it is a
     * paragraph of heading type. E.G. Tables It also gets the full object and
     * inserts it whole into the currently processed Page.
     *
     * @param doc
     * @param element
     * @param containerElements
     * @param boo
     * @param depth
     * @return
     */
    public BreakOutObj isAChildOf(Document doc, Element element, List<String> containerElements, BreakOutObj boo, int depth) {
        boo.setIsChildOfBreakOutContainer(false);
        if (null == element.parent()) {
            boo.setIsChildOfBreakOutContainer(false);
            return boo;
        }
        if (containerElements.contains(element.parent().tagName())) {
            boo.setIsChildOfBreakOutContainer(true);
            boo.setParentContainer(element.parent());
            boo.setDepth(depth + 1);
            return boo;
        } else {
            element = element.parent();
            return isAChildOf(doc, element, containerElements, boo, depth + 1);
        }
    }

    /**
     * Check if any of the paragraphs contain content, in word an empty line can
     * still have heading styling. Which creates empty none sense sub pages.
     *
     * @param e
     * @return boolean
     */
    private boolean hasContent(Element e) {
        if (e.text() == null || e.html().equals("&nbsp;") || e.text().equals(" ")) {
            return false;
        }
        if (StringUtils.isEmpty(e.text()) && e.children().isEmpty()) {
            return false;
        }
        if (!e.children().isEmpty()) {
            return true;
        }
        return !e.text().trim().isEmpty();
    }

    /**
     *
     * @param iti
     * @param structure
     * @param previousElementId
     * @return
     */
    private ImportTreeItem determineAncestory(ImportTreeItem iti, Map<Long, ImportTreeItem> structure, Long previousElementId) {
        ImportTreeItem previousItem = structure.get(iti.getId() - 1);
        if (null != previousItem) {
            if (previousItem.getLevel() == iti.getLevel()) {
                iti.setParentId(previousItem.getParentId());
            }
            iti.setParentId(locateLevelParent(structure, iti));
        }
        return iti;
    }

    /**
     * Locate the actual parent for the item. This decision is based on the item
     * level. retrieve the parent of the iti, if the parent is at a higher level
     * confirm parent. if not higher level, get parents parent, check if it is a
     * higher level if so set this parent as iti's parentId.
     *
     * @param structure
     * @param iti
     * @return
     */
    private long locateLevelParent(Map<Long, ImportTreeItem> structure, ImportTreeItem iti) {
        //check if the iti level is less than its parent items
        if (null != iti && null != structure) {
            if (iti.getParentId() != 0) {
                ImportTreeItem parentITI = structure.get(iti.getParentId());
                if (parentITI.getLevel() < iti.getLevel()) {
                    return iti.getParentId();
                } else {
                    return locateLevelParent(structure, parentITI);
                }
            }
        }
        return 0;
    }
}
