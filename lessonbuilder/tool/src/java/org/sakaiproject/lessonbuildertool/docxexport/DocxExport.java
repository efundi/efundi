package org.sakaiproject.lessonbuildertool.docxexport;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.docx4j.XmlUtils;
import org.docx4j.convert.in.xhtml.XHTMLImageHandler;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.contenttype.ContentType;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.exceptions.InvalidOperationException;
import org.docx4j.openpackaging.io.SaveToZipFile;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.AlternativeFormatInputPart;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart;
import org.docx4j.openpackaging.parts.relationships.Namespaces;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.CTAltChunk;
import org.docx4j.wml.CTBookmark;
import org.docx4j.wml.CTMarkupRange;
import org.docx4j.wml.Color;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.P.Hyperlink;
import org.docx4j.wml.R;
import org.docx4j.wml.RFonts;
import org.docx4j.wml.RPr;
import org.docx4j.wml.Style;
import org.docx4j.wml.Style.BasedOn;
import org.docx4j.wml.Styles;
import org.docx4j.wml.U;
import org.docx4j.wml.UnderlineEnumeration;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.CleanerTransformations;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagTransformation;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.lessonbuildertool.SimplePage;
import org.sakaiproject.lessonbuildertool.SimplePageItem;
import org.sakaiproject.lessonbuildertool.service.DataExportService;
import org.sakaiproject.lessonbuildertool.tool.view.ExportDocxViewParameters;
import org.sakaiproject.lessonbuildertool.util.ExportImportConstants;
import org.sakaiproject.lessonbuildertool.util.ExportHelper;
import org.sakaiproject.lessonbuildertool.util.HtmlCleanerUtil;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;
import org.springframework.web.util.UriUtils;

import uk.org.ponder.messageutil.MessageLocator;

/**
 * Export Lesson Content to docx format
 *
 * @author OpenCollab
 */
public class DocxExport {

    private final Log log = LogFactory.getLog(DocxExport.class);

    private String abortOnErrorStyledMessage = "<div style=\"font-size;1.1em;border-radius:4px;border: 1px solid #cd0a0a;color:#cd0a0a;padding:4px;\">{0}</div><br/>";

    // Export service initialised, spring injected instance.
    private DataExportService dataExportService;

    // MessageLocator service required as per the Lessons tool exception
    private MessageLocator messageLocator;

    // Utility class to clean html text
    private HtmlCleanerUtil htmlCleaner;

    // XHTMLImageHandler instance for handling images in html to docx conversions
    private XHTMLImageHandler xHTMLImageHandler;

    private ExportHelper exportHelper;

    /**
     * performs the
     *
     * @param sid
     * @param httpServletResponse
     * @param params
     */
    public void doExport(String sid, HttpServletResponse httpServletResponse,
            ExportDocxViewParameters params) {
        StringBuilder lessonName = new StringBuilder();
        setHTMLCleanerTransformations();
        
        try {
            Site site = dataExportService.getSite(sid);
            lessonName.append(site.getTitle());
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
            
            //*************************
            alterStyleSheet(wordMLPackage);
            //*************************
            SimplePage simplePage = dataExportService.getLessonData(params.getPageId());

            if (null != simplePage) {
                lessonName.append(simplePage.getTitle());
                wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Title", simplePage.getTitle());

                List<SimplePageItem> spItemList = dataExportService.getPageItems(simplePage.getPageId());
                processPageItems(wordMLPackage, spItemList, params, site, 0);
            }

            String fileName = lessonName.toString() + "_" + String.valueOf(new Date().getTime()) + ".docx";
            fileName = fileName.replace(" ", "_");
            SaveToZipFile saver = new SaveToZipFile(wordMLPackage);
            download(httpServletResponse, fileName, saver);
        } catch (NumberFormatException nfEx) {
            handleError(null, "Error exporting the lesson to DOCX format. Could not parse page id.", "simplepage.docx.error.exporting", nfEx);
        } catch (Docx4JException docxEx) {
            handleError(null, "Error exporting the lesson to DOCX format. Could not process page items.", "simplepage.docx.error.exporting", docxEx);
        } catch (IdUnusedException idEx) {
            handleError(null, "Error exporting the lesson to DOCX format. Could not find site ofr id : " + sid, "simplepage.docx.error.exporting", idEx);
        }
    }
    
    /**
     * Add transformations to the HTML cleaner to convert the CKEditor header 
     * tags (h1,h2,h3,h4) to span tags with in line styling. We need to do this 
     * to preserve the style without using the Header word styles. The Header word
     * styles is used to create sub pages when importing.
     */
    private void setHTMLCleanerTransformations() {
        HtmlCleaner htmlSetup = htmlCleaner.getHtmlSetup();
        CleanerProperties props = htmlSetup.getProperties();
        CleanerTransformations transformations = new CleanerTransformations();
        int counter = 1;
        for (String headerStyle : ExportImportConstants.CK_HEADER_LIST) {
            TagTransformation h1TT = new TagTransformation("h" + counter++, "span", false);
            h1TT.addAttributeTransformation("style", headerStyle);
            transformations.addTransformation(h1TT);       
        }
        
        props.setCleanerTransformations(transformations);
    }

    /**
     * Deliver the file to the user.
     *
     * @param fileName
     * @param filePath
     * @return
     */
    private void download(HttpServletResponse response, String fileName, SaveToZipFile saver) {
        OutputStream out = null;
        try {
            if (ExportImportConstants.ABORT_ON_ERROR && hasErrors()) {
                out = response.getOutputStream();
                response.setContentType("text/html; charset=".concat(ExportImportConstants.CHARSET));
                PrintStream printStream = new PrintStream(out);
                printStream.print(MessageFormat.format(abortOnErrorStyledMessage, "File aborted as a result of errors"));
                printStream.print("<script type=\"text/javascript\">setTimeout(parent.window.location.reload(true),10000);</script>");
                printStream.close();
                out.flush();
            } else {
                response.setHeader("Content-disposition", "inline; filename="
                        + fileName);
                // Add cookie so that we can check for that cookie and close the 
                // UI dialog
                Cookie cookie = new Cookie("fileDownloadToken", "closeDocXDialog");
                cookie.setPath("/");
                response.addCookie(cookie);
                response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                out = response.getOutputStream();
                saver.save(out);
                out.flush();
            }
        } catch (IOException ioe) {
            setErrKey("simplepage.docx.error.exporting", ioe.getMessage(), false);
        } catch (Docx4JException docxEx) {
            setErrKey("simplepage.docx.error.exporting", docxEx.getMessage(), false);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    log.error("Could not close output stream in DocX file download.", ex);
                }
            }
        }
    }

    /**
     * Process the page items for a SimplePage. It routes items to the methods
     * which can process this item and return a WordprocessingMLPackage object
     * with content
     *
     * @param wordMLPackage
     * @param spItemList
     * @return
     * @throws Docx4JException
     */
    private void processPageItems(WordprocessingMLPackage wordMLPackage, List<SimplePageItem> spItemList, ExportDocxViewParameters params, Site site, int currentDepth) throws Docx4JException {

        for (SimplePageItem spi : spItemList) {
            log.debug(spi.getType() + " - " + spi.getSakaiId() + " - " + spi.getName());
            switch (spi.getType()) {
                case SimplePageItem.PAGE:
                    SimplePage simplePage = dataExportService.getLessonData(Long.parseLong(spi.getSakaiId()));
                    int indent = getIndentationLevel(simplePage, currentDepth);
                    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading" + indent, spi.getName());                    
                    List<SimplePageItem> simplePageItemList = dataExportService.getPageItems(simplePage.getPageId());
                    processPageItems(wordMLPackage, simplePageItemList, params, site, indent);
                    break;
                case SimplePageItem.ASSIGNMENT:
                    log.debug("ASSIGNMENT :" + spi.getSakaiId() + " - " + spi.getName());
                    addAssignment(wordMLPackage, spi, site);
                    break;
                case SimplePageItem.ASSESSMENT:
                    log.debug("ASSESSMENT :" + spi.getSakaiId() + " - " + spi.getName());
                    addAssessment(wordMLPackage, spi, site);
                    break;
                case SimplePageItem.TEXT:
                    log.debug("TEXT :" + spi.getSakaiId() + " - " + spi.getName());
                    processText(wordMLPackage, spi);
                    break;
                case SimplePageItem.URL:
                    log.debug("URL :" + spi.getSakaiId() + " - " + spi.getName());
                    wordMLPackage.getMainDocumentPart().addParagraphOfText(spi.getName());
                    break;
                case SimplePageItem.RESOURCE:
                    log.debug("RESOURCE :" + spi.getSakaiId() + " - " + spi.getName());
                    addLink(wordMLPackage, spi, params);
                    break;
                case SimplePageItem.MULTIMEDIA:
                    log.debug("MULTIMEDIA :" + spi.getSakaiId() + " - " + spi.getName());
                    processMultiMedia(wordMLPackage, spi, params);
                    break;
                case SimplePageItem.FORUM:
                    log.debug("FORUM :" + spi.getSakaiId() + " - " + spi.getName());
                    addForumTopic(wordMLPackage, spi, site);
                    break;
                case SimplePageItem.COMMENTS:
                    log.debug("COMMENTS :" + spi.getSakaiId() + " - " + spi.getName());
                    wordMLPackage.getMainDocumentPart().addParagraphOfText(spi.getName());
                    break;
                case SimplePageItem.STUDENT_CONTENT:
                    log.debug("STUDENT_CONTENT :" + spi.getSakaiId() + " - " + spi.getName());
                    wordMLPackage.getMainDocumentPart().addParagraphOfText(spi.getName());
                    break;
                case SimplePageItem.QUESTION:
                    log.debug("QUESTION :" + spi.getSakaiId() + " - " + spi.getName());
                    addQuestion(wordMLPackage, spi);
                    break;
                case SimplePageItem.BLTI:
                    log.debug("BLTI :" + spi.getSakaiId() + " - " + spi.getName());
                    wordMLPackage.getMainDocumentPart().addParagraphOfText(spi.getName());
                    break;
                case SimplePageItem.PEEREVAL:
                    log.debug("PEEREVAL :" + spi.getSakaiId() + " - " + spi.getName());
                    wordMLPackage.getMainDocumentPart().addParagraphOfText(spi.getName());
                    break;
            }
        }
    }

    /**
     * Processes any SimplePageItem of type Text
     *
     * @param wordMLPackage
     * @param spi
     * @return
     * @throws Docx4JException
     */
    private void processText(WordprocessingMLPackage wordMLPackage, SimplePageItem spi) {
        XHTMLImporterImpl imp = new XHTMLImporterImpl(wordMLPackage);
        imp.setXHTMLImageHandler(xHTMLImageHandler);
        String cleanHTML = "<body>" + htmlCleaner.cleanHTMLString(spi.getHtml()) + "</body>";
        try {
            List<Object> convertedList = imp.convert(cleanHTML, "");
            for (Object part : convertedList) {
                wordMLPackage.getMainDocumentPart().addObject(part);
            }
        } catch (Docx4JException ex) {
            handleError(wordMLPackage, "Error importing textual content HTML into Docx.", "simplepage.docx.error.processing.text", ex);
        }
    }

    /**
     * Create an HTML file in the docx zip structure and add a reference to that
     *
     * @param wordMLPackage
     * @param spi
     * @param htmlText
     */
    private void addHtmlToDoc(WordprocessingMLPackage wordMLPackage, SimplePageItem spi, String htmlText) {
        try {
            AlternativeFormatInputPart afiPart = new AlternativeFormatInputPart(new PartName("/textContent_" + spi.getId() + ".html"));
            afiPart.setBinaryData(("<body>" + htmlText + "</body>").getBytes());
            afiPart.setContentType(new ContentType("text/html"));

            Relationship altChunkRel = wordMLPackage.getMainDocumentPart().addTargetPart(afiPart);
            CTAltChunk ac = Context.getWmlObjectFactory().createCTAltChunk();
            ac.setId(altChunkRel.getId());

            wordMLPackage.getMainDocumentPart().getContent().add(ac);
        } catch (InvalidFormatException ex) {
            wordMLPackage.getMainDocumentPart().addParagraphOfText("ERROR in docx Exporter! This text contains content that can not be handled by the exporter!");
            log.error(ex);
        }
    }

    /**
     * Add the link of the assignment to the doc
     *
     * @param wordMLPackage
     * @param spi
     * @param site
     */
    private void addAssignment(WordprocessingMLPackage wordMLPackage, SimplePageItem spi, Site site) {
        try {
            addLink(wordMLPackage, dataExportService.getAssignmentURL(spi, site),
                    dataExportService.getAssignmentTitle(spi));
        } catch (IdUnusedException e) {
            handleError(wordMLPackage, "ERROR in docx Exporter! This Assignment id could not be located by the exporter! ID : " + spi.getSakaiId(), "simplepage.docx.error.creating.assignment", e);
        } catch (PermissionException e) {
            handleError(wordMLPackage, "ERROR in docx Exporter! The exporter does not have permission to access this Assignment! ID : " + spi.getSakaiId(), "simplepage.docx.error.creating.assignment", e);
        }
    }

    /**
     * Add the link of the Assessment to the doc
     *
     * @param wordMLPackage
     * @param spi
     * @param site
     */
    private void addAssessment(WordprocessingMLPackage wordMLPackage, SimplePageItem spi, Site site) {
        addLink(wordMLPackage, dataExportService.getAssessmentURL(spi, site),
                dataExportService.getAssessmentTitle(spi));
    }

    /**
     * Add the link of the Forum topic to the doc
     *
     * @param wordMLPackage
     * @param spi
     * @param site
     */
    private void addForumTopic(WordprocessingMLPackage wordMLPackage, SimplePageItem spi, Site site) {
        addLink(wordMLPackage, dataExportService.getForumTopicURL(spi, site),
                dataExportService.getForumTopicTitle(spi));
    }

    /**
     * Add an external link
     *
     * @param wordMLPackage
     * @param spi
     * @return
     */
    private void addExternalWebLink(WordprocessingMLPackage wordMLPackage, SimplePageItem spi) {
        String linkUrl = spi.getName();
        addLink(wordMLPackage, linkUrl, linkUrl);
    }

    /**
     * Add a Question
     *
     * @param wordMLPackage
     * @param spi
     * @return
     */
    private void addQuestion(WordprocessingMLPackage wordMLPackage, SimplePageItem spi) {
        wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading3", spi.getName());
        wordMLPackage.getMainDocumentPart().addParagraphOfText("QuestionText : " + spi.getAttribute("questionText"));
        wordMLPackage.getMainDocumentPart().addParagraphOfText("Answers : " + spi.getJsonAttribute("answers"));
        wordMLPackage.getMainDocumentPart().addParagraphOfText("QuestionIncorrectText : " + spi.getAttribute("questionIncorrectText"));
        wordMLPackage.getMainDocumentPart().addParagraphOfText("QuestionCorrectText : " + spi.getAttribute("questionCorrectText"));
    }

    /**
     * Processes any SimplePageItem of type Multimedia
     *
     * @param wordMLPackage
     * @param spi
     * @return
     */
    private void processMultiMedia(WordprocessingMLPackage wordMLPackage, SimplePageItem spi, ExportDocxViewParameters params) {
        String multimediaDisplayType = spi.getAttribute("multimediaDisplayType");
        // 	multimediaDisplayType : 1 -- embed code, 2 -- av type, 3 -- oembed, 4 -- iframe
        if ("3".equals(multimediaDisplayType)) {
            String url = spi.getAttribute("multimediaUrl");
            addLink(wordMLPackage, url, url);
        } else if ("2".equals(multimediaDisplayType)) {
            handleAVType(wordMLPackage, spi);
        } else if ("1".equals(multimediaDisplayType)) {
            String html = htmlCleaner.convertIframeToLink(spi.getAttribute("multimediaEmbedCode"));
            addHtmlToDoc(wordMLPackage, spi, html);
        } else {
            handleOtherMultimediaDisplayTypes(wordMLPackage, spi, params);
        }
    }

    /**
     * Handle AV types that was embedded by URL
     *
     * @param wordMLPackage
     * @param spi
     * @throws Exception
     */
    private void handleAVType(WordprocessingMLPackage wordMLPackage, SimplePageItem spi) {
        String url = spi.getName();
        try {
            String ext = FilenameUtils.getExtension(url);
            if (ExportImportConstants.IMAGE_EXTENSIONS.contains(ext)) {
                BufferedImage image = ImageIO.read(new URL(url));
                // write image to outputstream
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, ext, baos);
                baos.flush();
                // get bytes
                byte[] imageBytes = baos.toByteArray();
                addImage(wordMLPackage, spi, imageBytes);
                baos.close();
                return;
            }
        } catch (Exception ex) {
            handleError(wordMLPackage, "Error downloading image : " + url, "simplepage.docx.error.creating.assignment", ex);
        }
        addLink(wordMLPackage, url, url);
    }

    /**
     * Handles the text and image types of multimedia
     *
     * @param wordMLPackage
     * @param spi
     * @param params
     */
    private void handleOtherMultimediaDisplayTypes(WordprocessingMLPackage wordMLPackage, SimplePageItem spi, ExportDocxViewParameters params) {
        try {
            ContentResource cr = dataExportService.getContentResourceData(spi.getSakaiId());
            if ("text/url".equals(cr.getContentType())) {
                addExternalWebLink(wordMLPackage, spi);
                // Embed Images
            } else if (ExportImportConstants.EMBED_RESOURCES && cr.getContentType().contains("image/")) {
                addImage(wordMLPackage, spi, cr.getContent());
                // Add link if not handled already    
            } else {
                addLink(wordMLPackage, spi, params);
            }
        } catch (Exception ex) {
            handleError(wordMLPackage, "Error adding embedded resource to Docx file.", "simplepage.docx.error.creating.new.image", ex);
        }
    }

    /**
     * Embeds an image in the doc
     *
     * @param wordMLPackage
     * @param spi
     * @param content
     * @throws ServerOverloadException
     */
    private void addImage(WordprocessingMLPackage wordMLPackage, SimplePageItem spi, byte[] content) throws ServerOverloadException {
        org.docx4j.wml.P p = newImage(
                wordMLPackage,
                content,
                spi.getDescription(), spi.getDescription(), 0, 1, 6000
        );
        wordMLPackage.getMainDocumentPart().addObject(p);
    }

    /**
     * Processes any SimplePageItem of type link
     *
     * @param wordMLPackage
     * @param spi
     * @return
     */
    private void addLink(WordprocessingMLPackage wordMLPackage, SimplePageItem spi, ExportDocxViewParameters params) {
        String linkUrl = params.getUrl() + "/access/content" + spi.getSakaiId();
        try {
            linkUrl = params.getUrl() + "/access/content" + UriUtils.encodeQuery(spi.getSakaiId(), ExportImportConstants.CHARSET);
        } catch (UnsupportedEncodingException e) {
            handleError(wordMLPackage, "Error adding an external link to the Docx file.", "simplepage.docx.error.url", e);
        }
        addLink(wordMLPackage, linkUrl, spi.getName());
    }

    /**
     * Adds a link with a specific url and label
     *
     * @param wordMLPackage
     * @param url
     * @param label
     */
    private void addLink(WordprocessingMLPackage wordMLPackage, String url, String label) {
        Hyperlink link = createHyperlink(wordMLPackage, url, label);

        org.docx4j.wml.ObjectFactory wmlFactory = new org.docx4j.wml.ObjectFactory();
        org.docx4j.wml.P paragraph = wmlFactory.createP();

        paragraph.getParagraphContent().add(link);
        wordMLPackage.getMainDocumentPart().addObject(paragraph);
    }

    /**
     * Create an docx4j image object from an byte array
     *
     * @param wordMLPackage
     * @param bytes
     * @param filenameHint
     * @param altText
     * @param id1
     * @param id2
     * @param cx
     * @return
     */
    private org.docx4j.wml.P newImage(
            WordprocessingMLPackage wordMLPackage, byte[] bytes,
            String filenameHint, String altText, int id1, int id2, long cx) {
        try {
            BinaryPartAbstractImage imagePart = BinaryPartAbstractImage
                    .createImagePart(wordMLPackage, bytes);

            Inline inline = imagePart.createImageInline(filenameHint, altText,
                    id1, id2, cx, false);

            // Now add the inline in w:p/w:r/w:drawing
            org.docx4j.wml.ObjectFactory factory = new org.docx4j.wml.ObjectFactory();
            org.docx4j.wml.P p = factory.createP();
            org.docx4j.wml.R run = factory.createR();
            p.getContent().add(run);
            org.docx4j.wml.Drawing drawing = factory.createDrawing();
            run.getContent().add(drawing);
            drawing.getAnchorOrInline().add(inline);

            return p;
        } catch (Exception e) {
            handleError(wordMLPackage, "Error creating a new image the Docx file.", "simplepage.docx.error.creating.new.image", e);
        }
        return null;
    }

    /**
     * Creates an external hyperlink with a set url and label
     *
     * @param wordMLPackage
     * @param url
     * @return
     */
    private Hyperlink createHyperlink(WordprocessingMLPackage wordMLPackage,
            String url, String label) {
        try {
            org.docx4j.relationships.ObjectFactory factory
                    = new org.docx4j.relationships.ObjectFactory();

            org.docx4j.relationships.Relationship rel = factory.createRelationship();
            rel.setType(Namespaces.HYPERLINK);
            rel.setTarget(url);
            rel.setTargetMode("External");

            wordMLPackage.getMainDocumentPart().getRelationshipsPart().addRelationship(rel);
            String hpl = "<w:hyperlink r:id=\"" + rel.getId() + "\" xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" "
                    + "xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\" >"
                    + "<w:r>"
                    + "<w:rPr>"
                    + "<w:rStyle w:val=\"Hyperlink\" />"
                    + "</w:rPr>"
                    + "<w:t>" + label + "</w:t>"
                    + "</w:r>"
                    + "</w:hyperlink>";

            return (Hyperlink) XmlUtils.unmarshalString(hpl);
        } catch (JAXBException e) {
            handleError(wordMLPackage, "Error creating hyperlink.", "simplepage.docx.error.hyperlink", e);
            return null;
        } catch (InvalidOperationException e) {
            handleError(wordMLPackage, "Error creating hyperlink.", "simplepage.docx.error.hyperlink", e);
            return null;
        }
    }

    /**
     * Surround the specified r in the specified p
     *
     * with a bookmark (with specified name and id)
     *
     * @param p
     * @param r
     * @param name
     * @param id
     */
    public void bookmarkRun(P p, R r, String name, int id) {

        // Find the index
        int index = p.getContent().indexOf(r);

        if (index < 0) {
            return;
        }

        ObjectFactory factory = Context.getWmlObjectFactory();
        BigInteger ID = BigInteger.valueOf(id);

        // Add bookmark end first
        CTMarkupRange mr = factory.createCTMarkupRange();
        mr.setId(ID);
        JAXBElement<CTMarkupRange> bmEnd = factory.createBodyBookmarkEnd(mr);
        p.getContent().add(index + 1, bmEnd);

        // Next, bookmark start
        CTBookmark bm = factory.createCTBookmark();
        bm.setId(ID);
        bm.setName(name);
        JAXBElement<CTBookmark> bmStart = factory.createBodyBookmarkStart(bm);
        p.getContent().add(index, bmStart);
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
        if (ExportImportConstants.ADD_ERROR_TO_SESSION) {
            if (text == null) {
                key = messageLocator.getMessage(key);
            } else {
                key = messageLocator.getMessage(key) + " : " + text;
            }
            exportHelper.setErrMessage(key, showHeaderMessage, "simplepage.docx.error.header");
        }
    }

    /**
     * Wraps the errors in a div and adds styling
     *
     * @param content
     * @param errorMessage
     */
    private void styledError(WordprocessingMLPackage wordMLPackage, String errorMessage) {
        XHTMLImporterImpl imp = new XHTMLImporterImpl(wordMLPackage);
        imp.setXHTMLImageHandler(xHTMLImageHandler);
        String cleanHTML = "<body>"
                + htmlCleaner.cleanHTMLString("<br/><div style='font-size;1.1em;border-radius:4px;border: 1px solid #cd0a0a;color:#cd0a0a;padding:4px;'>" + errorMessage + "</div><br/>") + "</body>";
        try {
            List<Object> convertedList = imp.convert(cleanHTML, "");
            for (Object part : convertedList) {
                wordMLPackage.getMainDocumentPart().addObject(part);
            }
        } catch (Docx4JException ex) {
            log.error("Error importing Text content HTML into Docx.", ex);
        }
    }

    /**
     * Standard method of handling exceptions within the Epub tool
     *
     * @param wordMLPackage - the document which is used to create the epub file
     * @param errorMessage - the message which will be displayed to the user
     * @param e - The exception object which will be logged and used for the
     * session errors
     */
    private void handleError(WordprocessingMLPackage wordMLPackage, String errorMessage, String errorKey, Exception e) {
        log.error(errorMessage, e);
        setErrKey(errorKey, errorMessage, true);
        if (null != wordMLPackage) {
            if (ExportImportConstants.ADD_ERROR_TO_DOC) {
                styledError(wordMLPackage, errorMessage);
            }
        }
    }

    /**
     * Checks the toolSession to determine whether there are errors
     *
     * @return
     */
    private Boolean hasErrors() {
        ToolSession toolSession = SessionManager.getCurrentToolSession();
        if (toolSession == null) {
            return false;
        }
        List<String> errors = (List<String>) toolSession.getAttribute("lessonbuilder.errors");
        return !CollectionUtils.isEmpty(errors);
    }

    /**
     * Setter to inject DataExportService
     *
     * @param dataExportService
     */
    public void setDataExportService(DataExportService dataExportService) {
        this.dataExportService = dataExportService;
    }

    /**
     * Setter to inject MessageLocator
     *
     * @param messageLocator
     */
    public void setMessageLocator(MessageLocator messageLocator) {
        this.messageLocator = messageLocator;
    }

    /**
     * @param xHTMLImageHandler the xHTMLImageHandler to set
     */
    public void setxHTMLImageHandler(XHTMLImageHandler xHTMLImageHandler) {
        this.xHTMLImageHandler = xHTMLImageHandler;
    }

    /**
     * @param htmlCleaner the htmlCleaner to set
     */
    public void setHtmlCleaner(HtmlCleanerUtil htmlCleaner) {
        this.htmlCleaner = htmlCleaner;
    }

    /**
     * Setter to inject the styled string
     *
     * @param abortOnErrorStyledMessage
     */
    public void setAbortOnErrorStyledMessage(String abortOnErrorStyledMessage) {
        this.abortOnErrorStyledMessage = abortOnErrorStyledMessage;
    }

    /**
     * @param exportHelper the exportHelper to set
     */
    public void setExportHelper(ExportHelper exportHelper) {
        this.exportHelper = exportHelper;
    }

    //****************************************
    /**
     * This method alters the default style sheet that is part of each document.
     *
     * To do this, we first retrieve the style sheet from the package and then
     * get the Styles object from it. From this object, we get the list of
     * actual styles and iterate over them. We check against all styles we want
     * to alter and apply the alterations if applicable.
     *
     * @param wordMLPackage
     */
    public void alterStyleSheet(WordprocessingMLPackage wordMLPackage) {
        StyleDefinitionsPart styleDefinitionsPart
                = wordMLPackage.getMainDocumentPart().getStyleDefinitionsPart();
        Styles styles = styleDefinitionsPart.getJaxbElement();

        List<Style> stylesList = styles.getStyle();
        for (String styleName : ExportImportConstants.STYLE_LIST) {
            Style style = getStyleFromList(stylesList, styleName);
            if (style != null) {
                setStyle(style);
            } else {
                style = createNewStyle(styleName);                
                setStyle(style);  
                stylesList.add(style);
            }
        }
    }
    
    /**
     * Get a style from the Style list with a specific name or return null if
     * not present
     * 
     * @param stylesList
     * @param styleName
     * @return the Style if present or null
     */
    private Style getStyleFromList(List<Style> stylesList, String styleName) {
        for (Style style : stylesList) {
            if (style.getStyleId().equals(styleName)) {
                return style;
            }
        }
        return null;
    }

    /**
     * Create a new Style object and initialize it
     * 
     * @param styleName
     * @return new Style object
     */
    private Style createNewStyle(String styleName) {
        Style newStyle = Context.getWmlObjectFactory().createStyle();
        newStyle.setType("paragraph");
        newStyle.setStyleId("myNewStyle");

        org.docx4j.wml.Style.Name n = Context.getWmlObjectFactory().createStyleName();
        n.setVal(styleName);
        newStyle.setStyleId(styleName);         
        
        BasedOn based = Context.getWmlObjectFactory().createStyleBasedOn();
        based.setVal("Normal");      
        newStyle.setBasedOn(based);  
        newStyle.setRPr(new RPr());
        
        return newStyle;
    }

    /**
     * For this style, we get the existing run properties from the style and
     * remove the theme font information from them. Then we also remove the bold
     * styling, change the font size (half-points) and add an underline.
     */
    private void setStyle(Style style) {
        String font = ServerConfigurationService.getString("lessonbuilder.docx.style."+style.getStyleId()+".font", "");
        int size = Integer.valueOf(ServerConfigurationService.getString("lessonbuilder.docx.style."+style.getStyleId()+".size", "0"));
        String colour = ServerConfigurationService.getString("lessonbuilder.docx.style."+style.getStyleId()+".color", "");
        boolean underline = Boolean.valueOf(ServerConfigurationService.getString("lessonbuilder.docx.style."+style.getStyleId()+".underline", "null"));
        boolean bold = Boolean.valueOf(ServerConfigurationService.getString("lessonbuilder.docx.style."+style.getStyleId()+".bold", "null"));
        boolean italic = Boolean.valueOf(ServerConfigurationService.getString("lessonbuilder.docx.style."+style.getStyleId()+".italics", "null"));
        
        RPr rpr = getRunPropertiesAndRemoveThemeInfo(style);
        if (StringUtils.isNotEmpty(font)) {
            changeFont(rpr, font);
        }
        if (size> 0 ) {
            changeFontSize(rpr, size);
        }
        if (StringUtils.isNotEmpty(colour)) {
            changeTextColor(rpr, colour);
        }
        if (bold) {
            addBoldStyle(rpr);
        } else {
            removeBoldStyle(rpr);
        }
        if (underline) {
            addUnderline(rpr);
        } else {
            removeUnderline(rpr);
        }
        if(italic){
            addItalicStyle(rpr);
        }else{
            removeItalicStyle(rpr);
        }
    }

    private RPr getRunPropertiesAndRemoveThemeInfo(Style style) {
        // We only want to change some settings, so we get the existing run
        // properties from the style.
        RPr rpr = style.getRPr();
        removeThemeFontInformation(rpr);
        return rpr;
    }

    /**
     * Change the font of the given run properties to Arial.
     *
     * A run font specifies the fonts which shall be used to display the
     * contents of the run. Of the four possible types of content, we change the
     * styling of two of them: ASCII and High ANSI. Finally we add the run font
     * to the run properties.
     *
     * @param runProperties
     */
    private void changeFont(RPr runProperties, String font) {
        RFonts runFont = new RFonts();
        runFont.setAscii(font);
        runFont.setHAnsi(font);
        runProperties.setRFonts(runFont);
    }

    /**
     * Change the font size of the given run properties to the given value.
     *
     * @param runProperties
     * @param fontSize Twice the size needed, as it is specified as half-point
     * value
     */
    private void changeFontSize(RPr runProperties, int fontSize) {
        HpsMeasure size = new HpsMeasure();
        size.setVal(BigInteger.valueOf(fontSize * 2));
        runProperties.setSz(size);
    }

    /**
     * Change the font size of the given run properties to the given value.
     *
     * @param runProperties
     * @param fontSize Twice the size needed, as it is specified as half-point
     * value
     */
    private void changeTextColor(RPr runProperties, String colour) {
        Color c = new Color();
        c.setVal(colour);
        runProperties.setColor(c);
    }

    /**
     * Removes the theme font information from the run properties. If this is
     * not removed then the styles based on the normal style won't inherit the
     * font from the normal style.
     *
     * @param runProperties
     */
    private void removeThemeFontInformation(RPr runProperties) {
        if (runProperties.getRFonts() != null) {
            runProperties.getRFonts().setAsciiTheme(null);
            runProperties.getRFonts().setHAnsiTheme(null);
        }
    }

    /**
     * Removes the Bold styling from the run properties.
     *
     * @param runProperties
     */
    private void removeBoldStyle(RPr runProperties) {
        if(runProperties.getB() != null){
            try{
                runProperties.getB().setVal(false);
            }catch(Exception e){
                log.error("Unable to UnBold", e);
            }
        }
    }

    /**
     * Adds the Bold styling from the run properties.
     *
     * @param runProperties
     */
    private void addBoldStyle(RPr runProperties) {
        try{
            runProperties.setB(new BooleanDefaultTrue());
            runProperties.getB().setVal(true);
        }catch(Exception e){            
            log.error("Unable to Bold", e);
        }
    }

        /**
     * Removes the Bold styling from the run properties.
     *
     * @param runProperties
     */
    private void removeItalicStyle(RPr runProperties) {
        if(runProperties.getI()!= null){
            try{
                runProperties.getI().setVal(false);
            }catch(Exception e){
                log.error("Unable to UnItalic", e);
            }
        }
    }

    /**
     * Adds the Bold styling from the run properties.
     *
     * @param runProperties
     */
    private void addItalicStyle(RPr runProperties) {
        try{
            runProperties.setI(new BooleanDefaultTrue() );
        }catch(Exception e){
            log.error("Unable to Italisize", e);
        }
    }
    
    
    /**
     * Adds a single underline to the run properties.
     *
     * @param runProperties
     */
    private void addUnderline(RPr runProperties) {
        U underline = new U();
        underline.setVal(UnderlineEnumeration.SINGLE);
        runProperties.setU(underline);
    }

    /**
     * Removes a single underline to the run properties.
     *
     * @param runProperties
     */
    private void removeUnderline(RPr runProperties) {
        U underline = new U();
        underline.setVal(UnderlineEnumeration.NONE);
        runProperties.setU(underline);
    }

    //****************************************
    /**
     * determine the level of indentation for the TOC
     *
     * @param simplePage
     * @param level
     * @return
     */
    private int getIndentationLevel(SimplePage simplePage, int level) {
        if (simplePage != null && simplePage.getParent() != null) {
            level = getIndentationLevel(dataExportService.getLessonData(simplePage.getParent()), level + 1);
        }
        return level;
    }
}
