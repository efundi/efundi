package org.sakaiproject.lessonbuildertool.docximport;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.wmf.tosvg.WMFTranscoder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.jsoup.nodes.Element;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.lessonbuildertool.SimplePage;
import org.sakaiproject.lessonbuildertool.SimplePageItem;
import org.sakaiproject.lessonbuildertool.model.SimplePageToolDao;
import org.sakaiproject.lessonbuildertool.tool.beans.SimplePageBean;
import static org.sakaiproject.lessonbuildertool.tool.beans.SimplePageBean.MAXIMUM_ATTEMPTS_FOR_UNIQUENESS;
import org.sakaiproject.lessonbuildertool.tool.view.ImportDocxViewParameters;
import org.sakaiproject.lessonbuildertool.util.ImportHelper;
import org.sakaiproject.lessonbuildertool.util.ImportTreeItem;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.util.Validator;
import uk.org.ponder.messageutil.MessageLocator;

/**
 * Creates a lesson with its content when given a DOCX file
 *
 * @author OpenCollab
 */
public class DocxImport {

    private final Log log = LogFactory.getLog(DocxImport.class);
    private final List<SimplePage> pages = new ArrayList<SimplePage>();
    private MessageLocator messageLocator;
//    private final String headingCss = ".sub-page-heading{font-size:large; font-weight: bold;}";
    private final String errorMessage = "<div style=\"font-size;1.1em;border-radius:4px;border: 1px solid #cd0a0a;color:#cd0a0a;padding:4px;\">{0}<br/>{1}</div><br/>";

    /**
     * ContentHostingService set by Spring configuration used for Resources work
     */
    private ContentHostingService contentHostingService;

    /**
     * performs the import of the document for conversion into a new lesson
     * create Resource Collection create CSS for document
     *
     * @param importFile
     * @param httpServletResponse
     * @param params
     * @param spb
     * @param simplePageToolDao
     * @throws org.docx4j.openpackaging.exceptions.Docx4JException
     * @throws java.io.FileNotFoundException
     */
    public void doImport(File importFile, HttpServletResponse httpServletResponse,
            ImportDocxViewParameters params, SimplePageBean spb, SimplePageToolDao simplePageToolDao) throws Docx4JException, FileNotFoundException {
        String siteId = ToolManager.getCurrentPlacement().getContext();
        ImportHelper ih = new ImportHelper();
        File html = ih.docxToHtml(siteId, importFile);
        String fullFilePath = html.getPath();
        try {
            String collectionId = createResourceCollection(siteId, params.getOriginalFileName());
            Document doc = Jsoup.parse(new File(fullFilePath), "UTF-8", "");
            String cssId = createCssResource(siteId, params, doc);
            Map<Long, ImportTreeItem> structure = ih.buildStructure(doc, params);
            parseStructure(structure, params, spb, ih, simplePageToolDao, siteId, importFile, collectionId, cssId);
        } catch (Exception ex) {
            handleError("Error importing the lesson from DOCX format. Could not Import the document.", "simplepage.docx.error.importing", ex);
        } finally {
            try {
                File f = new File(fullFilePath);
                f.delete();
                f = new File(fullFilePath.replace(".html", "").replace(" ", "") + "_files");
                FileUtils.deleteDirectory(f);
            } catch (Exception e) {
                log.error("Error deleting the tempory files.", e);
            }
        }
    }

    /**
     * Determines what content types are embedded within the HTML and performs
     * the required actions.
     *
     * @param it
     * @param siteId
     * @param importFile
     */
    private void getContentType(ImportTreeItem it, File importFile, String collectionId) {
        ImportHelper ih = new ImportHelper();
        HTMLSettings hs = ih.getDocxHtmlSettings(importFile.getPath());
        Document doc = Jsoup.parse(it.getHtmlContent());
        Elements images = doc.select("img[src]");
        for (Element img : images) {
            addImages(img, hs, collectionId, it);
        }
        Elements links = doc.select("a[href]");
        for (Element link : links) {
            if (!link.attr("href").startsWith("#")) {
                addLinks(link, collectionId, it);
            } else {
                it.setHtmlContent(it.getHtmlContent().replace(link.toString(), ""));
            }
        }
    }

    /**
     * Create the resource collection for the lesson, all resources other than
     * the CSS will be stored in this collection
     *
     * @param siteId
     * @param originalFileName
     * @return
     * @throws Exception generic exception - we are only interested if it
     * succeeded
     */
    private String createResourceCollection(String siteId, String originalFileName)
            throws Exception {
        String collectionId = contentHostingService.getSiteCollection(siteId)
                + FilenameUtils.getBaseName(originalFileName) + "/";
        return checkCreateResourceCollection(collectionId, FilenameUtils.getBaseName(originalFileName));
    }

    /**
     * Checks and create resource collection if it does not exist
     *
     * @param siteId
     * @param originalFileName
     * @return
     * @throws Exception generic exception - we are only interested if it
     * succeeded
     */
    private String checkCreateResourceCollection(String collectionId, String displayName)
            throws Exception {
        try {
            contentHostingService.checkCollection(collectionId);
            return contentHostingService.getCollection(collectionId).getId();
        } catch (IdUnusedException ex) {
            // Resource does not exist, lets create it
            return createNewResourceCollection(collectionId, displayName);
        }
    }

    /**
     * Creates a new resource folder for the new lesson using the file name
     *
     * @param collectionId
     * @param originalFileName
     * @return
     * @throws Exception generic exception - we are only interested if it
     * succeeded
     */
    private String createNewResourceCollection(String collectionId, String displayName)
            throws Exception {
        ContentCollectionEdit edit = contentHostingService.addCollection(collectionId);
        edit.getPropertiesEdit().addProperty(ResourceProperties.PROP_DISPLAY_NAME, displayName);
        contentHostingService.commitCollection(edit);
        return contentHostingService.getCollection(collectionId).getId();
    }

    /**
     * Create the CSS Resource collection and store the style sheets retrieved
     * from the imported document.
     *
     * @param siteId
     * @param params
     * @param doc
     * @param simplePageToolDao
     * @return
     */
    private String createCssResource(String siteId, ImportDocxViewParameters params, Document doc) throws Exception {
        Element css = doc.select("style").first();
        String collectionId = contentHostingService.getSiteCollection(siteId) + "LB-CSS/";
        checkCreateResourceCollection(collectionId, "LB-CSS");

        ContentResourceEdit cre;
        try {
            cre = contentHostingService.addResource(collectionId, FilenameUtils.getBaseName(params.getOriginalFileName()), "css", 5);
            String cssString = css.html();
            cre.setContent(cssString.getBytes("UTF-8"));
            cre.setContentLength(cssString.length());
            contentHostingService.commitResource(cre);
            return cre.getId();
        } catch (Exception ex) {
            handleError("Error on Page: [" + FilenameUtils.getBaseName(params.getOriginalFileName()) + "]. Error creating the CSS resource for this Lesson.", "simplepage.docx.error.importing", ex);
        }
        return null;
    }

    /**
     * Add image to the Resource tool and update the html content url.
     *
     * @param img
     * @param hs
     * @param collectionId
     * @param it
     */
    private void addImages(Element img, HTMLSettings hs, String collectionId, ImportTreeItem it) {
        Path p = Paths.get(img.attr("src"));
        String imagePath = hs.getImageDirPath() + "/" + p.getFileName().toString();
        try {
            if (FilenameUtils.getExtension(imagePath).contains("wmf")) {
                //convert wmf to jpg
                imagePath = convertWMFToPNG(imagePath);
            }
            ContentCollection contentCollection = contentHostingService.getCollection(collectionId);
            ContentResourceEdit cre = contentHostingService.addResource(contentCollection.getId(), FilenameUtils.getBaseName(imagePath), FilenameUtils.getExtension(imagePath), 5);
            p = Paths.get(imagePath);
            cre.setContent(Files.readAllBytes(p));
            cre.setContentLength(Files.size(p));
            
            contentHostingService.commitResource(cre);
            it.setHtmlContent(it.getHtmlContent().replace(img.attr("src"), cre.getUrl()));
        } catch (Exception ex) {
            //"Error on Page: ["+it.getParentTitle()+"]. Error adding the image " + p.getFileName().toString() + " to this Lesson."
            try {
                ContentCollection contentCollection = contentHostingService.getCollection(collectionId);
                ContentResource cr = contentHostingService.getResource(contentCollection.getId() + FilenameUtils.getBaseName(imagePath) + "." + FilenameUtils.getExtension(imagePath));
                it.setHtmlContent(it.getHtmlContent().replace(img.attr("src"), cr.getUrl()));
            } catch (Exception exc) {
                handleError("Error on Page: [" + it.getParentTitle() + "]. Error adding the image " + p.getFileName().toString() + " to this Lesson.", "simplepage.docx.error.importing", ex);
                it.setHtmlContent(it.getHtmlContent().replace(img.toString(), img.toString().concat(MessageFormat.format(errorMessage, messageLocator.getMessage("simplepage.docx.error.importing"), "Error adding the image " + p.getFileName().toString() + " to this Lesson."))));
            }
        }
    }

    /**
     * Add Hyperlinks to the Lessons resources directory. Any A tag with an HREF
     * will have the HREF content stored as a resources in the resources tool
     *
     * @param link
     * @param collectionId
     */
    private void addLinks(Element link, String collectionId, ImportTreeItem it) {
        String name = link.attr("href");
        String base = link.attr("href");
        String extension = "";
        int i = link.attr("href").lastIndexOf("/");
        if (i < 0) {
            i = 0;
        }
        i = link.attr("href").lastIndexOf(".", i);
        if (i > 0) {
            extension = link.attr("href").substring(i);
            base = link.attr("href").substring(0, i);
        }
        try {
            ContentResourceEdit edit;
            edit = contentHostingService.addResource(collectionId,
                    Validator.escapeResourceName(base),
                    Validator.escapeResourceName(extension),
                    MAXIMUM_ATTEMPTS_FOR_UNIQUENESS);
            edit.setContentType("text/url");
            edit.setResourceType("org.sakaiproject.content.types.urlResource");
            edit.setContent(link.attr("href").getBytes("UTF-8"));
            edit.getPropertiesEdit().addProperty(ResourceProperties.PROP_DISPLAY_NAME,
                    Validator.escapeResourceName(link.attr("href")));
            contentHostingService.commitResource(edit);
        } catch (Exception ex) {
            handleError("Error adding the Hyperlink " + name + " to this Lesson.", "simplepage.docx.error.importing", ex);
            it.setHtmlContent(it.getHtmlContent().replace(link.toString(), link.toString().concat(MessageFormat.format(errorMessage, messageLocator.getMessage("simplepage.docx.error.importing"), "Error adding the image " + name + " to this Lesson's resources."))));
        }
    }

    /**
     * Parses over the streamlined structure and add content to the lesson
     *
     * @param structure
     * @param params
     * @param spb
     * @param ih
     * @param simplePageToolDao
     * @param siteId
     * @param importFile
     */
    private void parseStructure(Map<Long, ImportTreeItem> structure, ImportDocxViewParameters params,
            SimplePageBean spb, ImportHelper ih, SimplePageToolDao simplePageToolDao,
            String siteId, File importFile, String collectionId, String cssId) {
        SimplePage page;
        SimplePage rootPage = null;
        HashMap<Long, Long> id_map = new HashMap<Long, Long>();
        for (Map.Entry<Long, ImportTreeItem> entry : structure.entrySet()) {
            ImportTreeItem it = entry.getValue();
            String title = (it.getTitle().isEmpty()) ? FilenameUtils.getBaseName(params.getOriginalFileName()) : it.getTitle();
            if (pages.isEmpty()) {
                page = spb.addPage(FilenameUtils.getBaseName(params.getOriginalFileName()), false);  // add new top level page
                page.setCssSheet(cssId);
                rootPage = page;
                ih.updateIds(structure, page, it, id_map);
                SimplePageItem item = simplePageToolDao.makeItem(page.getPageId(), 1, SimplePageItem.TEXT, "", "");
                getContentType(it, importFile, collectionId);
                item.setHtml(it.getHtmlContent());
                spb.saveItem(item);
            } else {
                SimplePage parent = rootPage;
                if (it.getParentId() == 0) {
                    page = simplePageToolDao.makePage(String.valueOf(rootPage.getPageId()), siteId, title, rootPage.getPageId(), rootPage.getPageId());
                } else {
                    page = simplePageToolDao.makePage(String.valueOf(spb.getItemsOnPage(parent.getPageId()).size() + 1), siteId, title, rootPage.getPageId(), rootPage.getPageId());
                }
                page.setCssSheet(cssId);
                spb.saveItem(page);

                ih.updateIds(structure, page, it, id_map);
                if (it.getParentId() != 0) {
                    parent = spb.getPage(id_map.get(it.getParentId()));
                }
                // check for content of differing types e.g. Images, video
                getContentType(it, importFile, collectionId);
                int seq = spb.getItemsOnPage(page.getPageId()).size() + 1;
                SimplePageItem contentItem = simplePageToolDao.makeItem(page.getPageId(), seq, SimplePageItem.TEXT, "", "");
                contentItem.setHtml(it.getHtmlContent());
                spb.saveItem(contentItem);

                seq = spb.getItemsOnPage(parent.getPageId()).size() + 1;
                SimplePageItem item = simplePageToolDao.makeItem(parent.getPageId(), seq, SimplePageItem.PAGE,
                        Long.toString(page.getPageId()), it.getTitle());
                item.setHtml(Validator.escapeHtml(it.getContent()));
                spb.saveItem(item);

                ih.updateIds(structure, page, it, id_map);
            }
            pages.add(page);
        }
    }

    /**
     * Set the contentHostingService for this class.
     *
     * @param contentHostingService
     */
    public void setContentHostingService(ContentHostingService contentHostingService) {
        this.contentHostingService = contentHostingService;
    }

    /**
     * Convert Word clip art file of type WMF to PNG WMF to SVG then SVG to PNG
     *
     * @param imagePath
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     * @throws TranscoderException
     */
    private String convertWMFToPNG(String imagePath) throws FileNotFoundException, IOException, TranscoderException {
        File wmf = new File(imagePath);
        WMFTranscoder transcoder = new WMFTranscoder();
        String svgFile = StringUtils.replace(imagePath, "wmf", "svg");
        FileInputStream is = new FileInputStream(wmf);
        TranscoderInput wmfti = new TranscoderInput(is);
        FileOutputStream fos = new FileOutputStream(svgFile);
        TranscoderOutput svg = new TranscoderOutput(new OutputStreamWriter(fos, "UTF-8"));
        transcoder.transcode(wmfti, svg);
        return trancodeSVGToPNG(svgFile);
    }

    /**
     * Convert an SVG to PNG file.
     *
     * @param svgFile
     * @return
     * @throws FileNotFoundException
     * @throws TranscoderException
     * @throws IOException
     */
    private String trancodeSVGToPNG(String svgFile) {
        String pngFile = StringUtils.replace(svgFile, "svg", "png");
        try {
            PNGTranscoder t = new PNGTranscoder();
            TranscoderInput inputSVG = new TranscoderInput(new FileInputStream(svgFile));
            OutputStream pngOutStream = new FileOutputStream(pngFile);
            TranscoderOutput outputPNG = new TranscoderOutput(pngOutStream);
            t.transcode(inputSVG, outputPNG);
            pngOutStream.flush();
            pngOutStream.close();
        } catch (Exception e) {
            handleError("Error transcode the Word Clipart file to this Lesson.", "simplepage.docx.error.importing", e);
        }
        return pngFile;
    }

    /**
     * Standard method of handling exceptions within the DocX import
     *
     * @param errorMessage - the message which will be displayed to the user
     * @param e - The exception object which will be logged and used for the
     * session errors
     */
    private void handleError(String errorMessage, String errorKey, Exception e) {
        log.error(errorMessage, e);
        setErrKey(errorKey, errorMessage, true);
    }

    /**
     * Sets the error handling message.
     *
     * @param key - message key
     * @param text - exception message
     * @param showHeaderMessage - show header message if docx file created
     * anyway
     */
    private void setErrKey(String key, String text, boolean showHeaderMessage) {
        if (text == null) {
            key = messageLocator.getMessage(key);
        } else {
            key = messageLocator.getMessage(key) + " : " + text;
        }
        setErrMessage(key, showHeaderMessage, "simplepage.docx.error.header");
    }

    /**
     * Add errors to the tool session so that we display it to user when
     * refreshing the lesson page.
     *
     * @param errorMessage
     * @param showHeaderMessage
     * @param headerErrorMsg
     */
    public void setErrMessage(String errorMessage, boolean showHeaderMessage, String headerErrorMsg) {
        ToolSession toolSession = SessionManager.getCurrentToolSession();
        if (toolSession == null) {
            log.error("Lesson Builder error not in tool: " + errorMessage);
            return;
        }
        List<String> errors = (List<String>) toolSession.getAttribute("lessonbuilder.errors");
        if (errors == null) {
            errors = new ArrayList<String>();
            if (showHeaderMessage) {
                errors.add(messageLocator.getMessage(headerErrorMsg));
            }
            toolSession.setAttribute("lessonbuilder.errors", errors);
        }
        errors.add(errorMessage);
    }

    /**
     * Setter to inject MessageLocator
     *
     * @param messageLocator
     */
    public void setMessageLocator(MessageLocator messageLocator) {
        this.messageLocator = messageLocator;
    }
}
