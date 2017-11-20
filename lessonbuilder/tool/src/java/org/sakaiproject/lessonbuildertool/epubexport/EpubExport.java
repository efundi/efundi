package org.sakaiproject.lessonbuildertool.epubexport;

import coza.opencollab.epub.creator.model.Content;
import coza.opencollab.epub.creator.model.EpubBook;
import coza.opencollab.epub.creator.model.TocLink;
import java.io.ByteArrayInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentEntity;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.lessonbuildertool.ExportPageStructure;
import org.sakaiproject.lessonbuildertool.SimplePage;
import org.sakaiproject.lessonbuildertool.SimplePageItem;
import org.sakaiproject.lessonbuildertool.service.DataExportService;
import org.sakaiproject.lessonbuildertool.tool.view.ExportEpubViewParameters;
import org.sakaiproject.lessonbuildertool.util.ExportHelper;
import org.sakaiproject.lessonbuildertool.util.HtmlCleanerUtil;
import org.sakaiproject.portal.util.CSSUtils;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;
import org.springframework.util.FileCopyUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import uk.org.ponder.messageutil.MessageLocator;

/**
 * Export Lesson Content to EPUB format
 *
 * @author OpenCollab
 */
public class EpubExport {

    private final Log log = LogFactory.getLog(EpubExport.class);
    /**
     * property can be set via sakai.properties file.
     */
    private final boolean subPagesInline = Boolean.valueOf(ServerConfigurationService.getString("lessonbuilder.epub.inline.content", "false"));
    private final boolean addErrorToDoc = Boolean.valueOf(ServerConfigurationService.getString("lessonbuilder.epub.error.add.to.doc", "false"));
    private final boolean abortOnError = Boolean.valueOf(ServerConfigurationService.getString("lessonbuilder.epub.error.abort.doc.creation", "false"));
    private final boolean addErrorToSession = (abortOnError) ? true : Boolean.valueOf(ServerConfigurationService.getString("lessonbuilder.epub.error.add.to.session", "false"));
    private final String languageEncoding = ServerConfigurationService.getString("lessonbuilder.epub.languageEncoding", "en");
    private final int fileSizeInclusionLimit = Integer.parseInt(ServerConfigurationService.getString("lessonbuilder.epub.filesize.inclusion.limit", "300")) * 1024 * 1024;
    private final boolean showSubPageTitle = Boolean.valueOf(ServerConfigurationService.getString("lessonbuilder.export.show.subpage.title", "true"));
    private final boolean addExternalLinkToPDF = Boolean.valueOf(ServerConfigurationService.getString("lessonbuilder.epub.embed.pdf.include.external.link", "true"));
    private final boolean includeContentPage = Boolean.valueOf(ServerConfigurationService.getString("lessonbuilder.epub.include.content.page", "false"));
    private final String contentPlaceholder = ServerConfigurationService.getString("lessonbuilder.epub.content.heading.text", "Content");
    private final UrlValidator urlValidator = new UrlValidator();

    /**
     * ContentHostingService set by Spring configuration used for Resources work
     */
    private ContentHostingService contentHostingService;

    /**
     * DataExportService set by Spring configuration used for to export Lesson
     * data
     */
    private DataExportService dataExportService;

    // Utility class to clean html text
    private HtmlCleanerUtil htmlCleaner;

    // MessageLocator service required as per the Lessons tool exception
    private MessageLocator messageLocator;

    // Name of cover image, will look for .jpg, .png and .gif extensions
    private String coverImage = "epub_cover";

    // List of file extensions checked when searching for a cover image
    private List<String> imageExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");

    // List of file extensions checked when searching for video files
    private final List<String> videoExtensions = Arrays.asList("mp4", "flv", "avi", "mpeg", "webm", "3pg", "mkv");

    // HTML used to add links to EPUB
    private String linkHTML = "<p><a href=\"{0}\">{1}</a></p>";

    // Simple html link
    private final String plainLinkHTML = "<a href=\"{0}\">{1}</a>";

    // Simple html link
    private final String embeddedPlainLinkHTML = "<p><a href=\"{0}\" data-pre-embedded=\"true\">{1}</a></p>";

    private final String hiddenInput = "<input type=\"hidden\" id=\"{0}\" value=\"{1}\">";

    // HTML used to sized image tags to EPUB
    private String sizedImageHTML = "<p><img src=\"{0}\" {1} alt=\"\"/></p>";

    // HTML used to sized image tags to EPUB
    private String unsizedImageHTML = "<p><img src=\"{0}\" {1} alt=\"\" data-pre-embedded=\"true\"/></p>";

    /**
     * HTML template for displaying video
     */
    private String html5VideoTag = "<video width=\"320\" height=\"180\" controls=\"controls\">\n"
            + "<source src=\"{0}\" type=\"{1}\" />\n"
            + "<p>If your reading system does not support HTML5 video, \n"
            + "this video is available in\n"
            + "<a href=\"{2}\">{2}</a>.</p>\n"
            + "</video>";

    /**
     * HTML template for displaying audio
     */
    private String html5AudioTag = "<audio width=\"320\" height=\"180\" controls=\"controls\">\n"
            + "<source src=\"{0}\" type=\"{1}\"/>\n"
            + "<p>If your reading system does not support HTML5 audio, \n"
            + "this audio is available in\n"
            + "<a href=\"{2}\">{2}</a>.</p>\n"
            + "</audio>";

    /**
     * Add JavaScript to allow input for INPUT tags in EPUB reader, otherwise
     * reader Shortcut keys will take effect
     */
    private final String addContentToAllowInput
            = "function onkeydownFunc(e) {\n "
            + "  	var doStopPropagation;\n "
            + "		var targ;\n"
            + "     if (!e){ \n"
            + "			var e = window.event;\n"
            + "		}\n"
            + "	    if (e.target) { \n"
            + "       	targ = e.target;\n"
            + "     }else if (e.srcElement) { \n"
            + "			targ = e.srcElement;\n"
            + "		}\n"
            + "		if (targ.tagName.toUpperCase() == 'INPUT') {\n"
            + "			doStopPropagation = true;\n"
            + "		}else {\n"
            + "			doStopPropagation = false;\n"
            + "		}\n"
            + "		if (doStopPropagation) {\n"
            + "			e.stopPropagation();\n"
            + "		}\n"
            + "	}\n\n";

    private final String deleteResourceMessage = "<span style=\"color: #930;font-style: italic;padding-left: 1em;\">{0}</span>";

    // HTML used to display error messages for content that failed to be exported to EPUB
    private final String styledError = "<br/><div style=\"font-size;1.1em;border-radius:4px;border: 1px solid #cd0a0a;color:#cd0a0a;padding:4px;\">{0}</div><br/>";

    // This wrapper should not be settable
    private final String htmlWrapper = "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head ><title>{0}</title>{1}</head><body>{2}</body></html>";

    // JSON object wrapper
    private final String returnJSON = "\"docName\": \"{0}\", \"docURL\": \"{1}\"";

    // JSON error object wrapper
    private final String returnErrorJSON = "\"errorMessage\": \"{0}\", \"errorReason\": \"{1}\"";

    // Lessons Resource name
    private final String lessonResourceName = "/group/{0}/{1}/" + coverImage + ".";

    // Site Resource name
    private final String siteResourceName = "/group/{0}/" + coverImage + ".";

    // HTML used to add Sakai tool links to EPUB
    private final String sakaiToollinkHTML = "<a href=\"{0}\" target=\"_blank\">{1}</a></p>";

    private final String thisToolLink = "{0}/portal/site/{1}/page/{2}";

    private ExportHelper exportHelper;

    // We have to use UTF-8 for encoding
    private final Charset fixedCharSet = Charset.forName("UTF-8");

    private static final String EDGE_CHECK = "edge_includes";
    private static final String ADOBE_EDGE = "Edge";
    private static final String ARTICULATE = "Articulate";
    private static final String ARTICULATE_META_CHECK = "meta.xml";
    private static final String ARTICULATE_TAG_CHECK = "application";
    private static final String ARTICULATE_CHECK = "Articulate Storyline";

    private final String epubIframe = "<iframe\n"
            + "        src=\"{0}\"\n"
            + "        title=\"{1}\"\n"
            + "        class=\"{2}\">\n"
            + "   Your reading system does not support inline \n"
            + "   frames or support has been disabled. Please \n"
            + "   follow <a href=\"{3}\">this link</a> \n"
            + "   to open the associated content document.\n"
            + "</iframe>";

    /**
     * Creates an EPUB file for a specific Lesson
     *
     * The file is saved to the Site's Resources and the name of the file is
     * returned as a JSON object on the HttpServletResponse. This method should
     * only be called through the RSF :( framework
     *
     * @param sid
     * @param httpServletResponse
     * @param params
     */
    public void doExport(String sid, HttpServletResponse httpServletResponse,
            ExportEpubViewParameters params) {
        try {
            ContentResourceEdit contentResource = createEPUBBook(sid, params);
            httpServletResponse.setContentType("application/json");
            PrintWriter out = httpServletResponse.getWriter();
            out.println(createReturnJSON(contentResource));
            out.close();
        } catch (IdUnusedException iue) {
            handleRequiredResourceTool(httpServletResponse);
        } catch (Exception e) {
            handleError("ERROR in Epub Exporter! An exception occured while this Lesson was being exported.",
                    "simplepage.epub.error.creating.epub", e);
        }
    }

    /**
     * Creates the JSON object returned on the HttpServletResponse. We can add
     * error flags or messages here - will have to handle that in show-page.js
     *
     * @param fileName
     * @return
     */
    private String createReturnJSON(ContentResourceEdit contentResource) {
        String jsonValues = MessageFormat.format(returnJSON, getCleanResourceName(contentResource.getId()),
                contentResource.getUrl());
        return "{".concat(jsonValues).concat("}");
    }

    /**
     * Creates the JSON object returned on the HTTPServletResponse. The object
     * contains the error message and reason.
     *
     * @param errorMessage
     * @param errorCause
     * @return
     */
    private String createErrorJSON(String errorMessage, String errorCause) {
        String jsonValues = MessageFormat.format(returnErrorJSON, errorMessage, errorCause);
        return "{".concat(jsonValues).concat("}");
    }

    /**
     * Creates a JSON object containing the error message and reason. Returns
     * this data to the browser via httpServletResponse On exception logs the
     * error.
     *
     * @param httpServletResponse
     */
    private void handleRequiredResourceTool(HttpServletResponse httpServletResponse) {
        try {
            PrintWriter out = httpServletResponse.getWriter();
            out.println(createErrorJSON("Unable to create the EPUB file.", "Please request "
                    .concat("that the administrator add the ")
                    .concat("<strong>Resource</strong> tool to this site.<br/>")));
            out.close();
        } catch (Exception ex) {
            log.error("Error informing the user, that the resource tool is required for the EPUB export.", ex);
        }
    }

    /**
     * Gets the name of the ContentResource file
     *
     */
    private String getCleanResourceName(String id) {
        if (id.contains("/")) {
            return id.substring(id.lastIndexOf("/") + 1);
        } else {
            return id;
        }
    }

    /**
     * Creates the EPUB book, adds the lesson data and saves to resources
     *
     * @param siteId
     * @param params
     * @return name of the Resources object
     * @throws Exception
     */
    private ContentResourceEdit createEPUBBook(String siteId, ExportEpubViewParameters params) throws Exception {
        List<ExportPageStructure> pageIndex = new ArrayList<ExportPageStructure>();
        Site site = dataExportService.getSite(siteId);
        SimplePage simplePage = dataExportService.getLessonData(params.getPageId());
        params.setUrl(MessageFormat.format(thisToolLink, params.getUrl(), simplePage.getSiteId(), simplePage.getToolId()));
        StringBuilder content = new StringBuilder();
        StringBuilder jsContent = new StringBuilder();
        StringBuilder contentsPage = new StringBuilder();
        List<String> cssList = new ArrayList<String>();
        EpubBook book = new EpubBook(languageEncoding, simplePage.getTitle(),
                simplePage.getTitle(), site.getTitle());
        EpubBook shadowBook = new EpubBook(languageEncoding, simplePage.getTitle(),
                simplePage.getTitle(), site.getTitle());
        if (includeContentPage) { //There must be a better way, think on this
            try {
                shadowBook = createShadowBook(shadowBook, simplePage, content, site, pageIndex, params, cssList, jsContent);
            } catch (Exception e) {
                log.error(e);
            }
            content = jsContent = new StringBuilder();
            cssList = new ArrayList<String>();
            pageIndex = new ArrayList<ExportPageStructure>();
            populateContentsPage(contentsPage, shadowBook);
        }
        book.setAutoToc(false);     // Will only be one record if true. We manually build it.
        TocLink tocLink = new TocLink(getValidFilename(simplePage.getTitle()).concat(".xhtml"),
                simplePage.getTitle(), null);
        book.getTocLinks().add(tocLink);
        content.append(addTitleHeader(simplePage).concat("<br/>"));
        content.append(contentsPage.toString());
        buildTocStructure(site, simplePage, null, pageIndex);
        addLessonContent(book, site, params, null, content, jsContent, pageIndex, cssList, simplePage.getTitle());
        populateCssList(simplePage, cssList);
        outputSection(book, simplePage.getTitle(), content.toString(), site, jsContent.toString(), cssList);
        if (subPagesInline) {
            processInlineToc(book, pageIndex, simplePage);
        } else {
            processSubPages(book, site, params, pageIndex, content, cssList, simplePage.getTitle());
        }
        if (abortOnError && hasErrors()) {
            return null;
        } else {
            return saveEPUBFileInResources(book, siteId, getValidFilename(simplePage.getTitle()));
        }
    }

    /**
     * Creates a complete copy of the ebook, required when a content page is
     * required. Very heavy in terms of processing.
     *
     * @param book
     * @param simplePage
     * @param content
     * @param site
     * @param pageIndex
     * @param params
     * @param cssList
     * @param jsContent
     * @return
     * @throws Exception
     */
    private EpubBook createShadowBook(
            EpubBook book,
            SimplePage simplePage,
            StringBuilder content,
            Site site,
            List<ExportPageStructure> pageIndex,
            ExportEpubViewParameters params,
            List<String> cssList,
            StringBuilder jsContent
    ) throws Exception {
        book.setAutoToc(false);     // Will only be one record if true. We manually build it.
        TocLink tocLink = new TocLink(getValidFilename(simplePage.getTitle()).concat(".xhtml"),
                simplePage.getTitle(), null);
        book.getTocLinks().add(tocLink);
        content.append(addTitleHeader(simplePage));
        buildTocStructure(site, simplePage, null, pageIndex);
        addLessonContent(book, site, params, null, content, jsContent, pageIndex, cssList, simplePage.getTitle());
        populateCssList(simplePage, cssList);
        outputSection(book, simplePage.getTitle(), content.toString(), site, jsContent.toString(), cssList);
        if (subPagesInline) {
            processInlineToc(book, pageIndex, simplePage);
        } else {
            processSubPages(book, site, params, pageIndex, content, cssList, simplePage.getTitle());
        }
        return book;
    }

    /**
     * Creates a content page for insertion into the epub, prior to any other
     * content.
     *
     * @param contentsPage
     * @param book
     */
    private void populateContentsPage(StringBuilder contentsPage, EpubBook book) {
        contentsPage.append("<h2>");
        contentsPage.append(contentPlaceholder);
        contentsPage.append("</h2><br/>");
        if (!book.getTocLinks().isEmpty()) {
            contentsPage.append("<ul>");
            for (TocLink tl : book.getTocLinks()) {
                contentsPage.append("<li>");
                contentsPage.append(
                        MessageFormat.format(
                                plainLinkHTML, tl.getHref(), tl.getTitle().replaceAll("-", "&nbsp;")
                        )
                );
                contentsPage.append("</li>");
            }
            contentsPage.append("</ul>");
        }
        contentsPage.append("<br/>");
    }

    /**
     * Creates a structure of lesson pages to be used when creating the Table Of
     * Contents this avoids the Concurrent modification conditions and removes
     * complexity from the addLessonContent method
     *
     * @param site
     * @param simplePage
     * @param simplePageItemList
     * @param pageIndex
     */
    private void buildTocStructure(Site site, SimplePage simplePage, List<SimplePageItem> simplePageItemList, List<ExportPageStructure> pageIndex) {
        if (null == simplePageItemList) {
            simplePageItemList = dataExportService.getPageItems(simplePage.getPageId());
        }
        for (SimplePageItem spi : simplePageItemList) {
            if (spi.getType() == SimplePageItem.PAGE) {
                SimplePage subSimplePage = dataExportService.getLessonData(Long.parseLong(spi.getSakaiId()));
                List<SimplePageItem> subSimplePageItemList = dataExportService.getPageItems(subSimplePage.getPageId());
                ExportPageStructure eps = new ExportPageStructure();
                eps.setId(spi.getId());
                eps.setSpi(spi);
                eps.setParentId(subSimplePage.getPageId());
                eps.setHasBeenRendered(false);
                pageIndex.add(eps);
                buildTocStructure(site, subSimplePage, subSimplePageItemList, pageIndex);
            }
        }
    }

    /**
     * Adds the reference to the element in the Table of Contents for in-line
     * content.
     *
     * @param book
     * @param pageIndex
     * @param simplePage
     */
    private void processInlineToc(EpubBook book, List<ExportPageStructure> pageIndex, SimplePage simplePage) {
        for (ExportPageStructure item : pageIndex) {
            SimplePage subSimplePage = dataExportService.getLessonData(Long.parseLong(item.getSpi().getSakaiId()));
            int indent = getIndentationLevel(subSimplePage, 0);
            String pageTitle = StringUtils.repeat(" - ", indent).concat(subSimplePage.getTitle());
            TocLink tocLink = new TocLink(getValidFilename(simplePage.getTitle()).concat(".xhtml#section_").concat(String.valueOf(item.getSpi().getId())),
                    pageTitle, null);
            book.getTocLinks().add(tocLink);
        }
    }

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

    /**
     * Adding the Title Header
     *
     * @param simplePage
     */
    private String addTitleHeader(SimplePage simplePage) {
        return "<h1>".concat(simplePage.getTitle()).concat("</h1>");
    }

    /**
     * Add a cover image to the EPUB book. First check for an image in the
     * lesson's resources folder then in the site folder
     *
     * @param book
     * @param simplePage
     */
    private void addCover(EpubBook book, SimplePage simplePage) {
        // First check in lesson resources then check site resources
        if (!searchAndAddCoverImages(MessageFormat.format(lessonResourceName, simplePage.getSiteId(), simplePage.getTitle()), book)) {
            // No cover found on Lesson level, now look for cover on site level
            searchAndAddCoverImages(MessageFormat.format(siteResourceName, simplePage.getSiteId()), book);
        }
    }

    /**
     * Search for image resources by iterating through all the known image types
     * and add to cover if found
     *
     * @param baseResourceId
     * @param book
     * @return
     */
    private boolean searchAndAddCoverImages(String baseResourceId, EpubBook book) {
        for (String ext : imageExtensions) {
            try {
                book.addCoverImage(dataExportService.getContentResourceData(baseResourceId + ext).getContent(), null, baseResourceId + ext);
                return true;
            } catch (Exception ex) {
                // This resourceId does not exist, just try the next one. NO need
                // to handle this exception - this is expected
            }
        }
        return false;
    }

    /**
     * Handles the logic to add all the types of SimplePageItems to the EPUB
     * document
     *
     * @param book
     * @param site
     * @param params
     * @param simplePageItemList
     * @param content
     * @param jsContent
     * @param pageIndex
     * @throws Exception
     */
    private void addLessonContent(EpubBook book, Site site, ExportEpubViewParameters params, List<SimplePageItem> simplePageItemList, StringBuilder content, StringBuilder jsContent, List<ExportPageStructure> pageIndex, List<String> cssList, String lessonName) throws Exception {
        SimplePage simplePage = dataExportService.getLessonData(params.getPageId());
        addCover(book, simplePage);
        if (null == simplePageItemList) {
            simplePageItemList = dataExportService.getPageItems(simplePage.getPageId());
        }
        boolean addQuestionJavascript = false;
        for (SimplePageItem spi : simplePageItemList) {
            switch (spi.getType()) {
                case SimplePageItem.PAGE:
                    SimplePage subSimplePage = dataExportService.getLessonData(Long.parseLong(spi.getSakaiId()));
                    populateCssList(subSimplePage, cssList);
                    if (subPagesInline) {
                        if (showSubPageTitle) {
                            content.append(addSimplePageNameHeader(spi));
                        }
                        List<SimplePageItem> subSimplePageItemList = dataExportService.getPageItems(subSimplePage.getPageId());
                        content.append("<div id=\"section_").append(spi.getId()).append("\">");
                        addLessonContent(book, site, params, subSimplePageItemList, content, jsContent, pageIndex, cssList, lessonName);
                        updateRenderedPages(pageIndex, spi.getId(), subSimplePage.getPageId());
                        content.append("</div><br/>");
                    } else {
                        content.append(addSimplePageReferrer(spi));
                    }
                    break;
                case SimplePageItem.ASSIGNMENT:
                    content.append(createAssignment(spi, site));
                    break;
                case SimplePageItem.ASSESSMENT:
                    content.append(createAssessment(spi, site));
                    break;
                case SimplePageItem.TEXT:
                    content.append(createText(book, spi, site, lessonName));
                    break;
                case SimplePageItem.RESOURCE:
                    content.append(createResource(book, spi, site, lessonName));
                    break;
                case SimplePageItem.MULTIMEDIA:
                    content.append(createMultimedia(book, spi, site, lessonName));
                    break;
                case SimplePageItem.FORUM:
                    content.append(createForumTopic(spi, site));
                    break;
                case SimplePageItem.QUESTION:
                    content.append(createQuestion(spi, params, site, jsContent));
                    addQuestionJavascript = true;
                    break;
                default:
                    break;
            }
        }
        if (addQuestionJavascript) {
            jsContent.append(addContentToAllowInput);
        }
    }

    /**
     * Adds a Name Header for Simple page
     *
     * @param content
     * @param spi
     */
    private String addSimplePageNameHeader(SimplePageItem spi) {
        return "<h2>".concat(spi.getName()).concat("</h2>");
    }

    /**
     * Update the pageIndex when a page has been rendered to prevent it from
     * being duplicated.
     *
     * @param pageIndex
     * @param SimplePageItemId
     * @param SimplePageId
     */
    private void updateRenderedPages(List<ExportPageStructure> pageIndex, Long SimplePageItemId, Long SimplePageId) {
        for (ExportPageStructure nses : pageIndex) {
            if (nses.getId() == SimplePageItemId && nses.getParentId() == SimplePageId) {
                nses.setHasBeenRendered(true);
                break;
            }
        }
    }

    /**
     * Adds a Name SubHeader for Simple page
     *
     * @param spi
     */
    private String addSimplePageReferrer(SimplePageItem spi) {
        return MessageFormat.format(linkHTML, "./" + getValidFilename(spi.getName().replace(" ", "_")) + ".xhtml", "<h4>".concat(spi.getName()).concat("</h4>"));
    }

    /**
     * Adds a section to the EPUB lib Book object, also adds the sites CSS file
     * to the EPUB file.
     *
     * @param book
     * @param sectionName
     * @param content
     * @throws IOException
     */
    private void outputSection(EpubBook book, String sectionName, String contentStr, Site site, String jsContent, List<String> cssList) throws IOException {
        Logger.getLogger(EpubExport.class.getName()).log(Level.INFO, "get the css" + site.getSkin());
        String skinFolder = CSSUtils.getSkinFromSite(site);
        Logger.getLogger(EpubExport.class.getName()).log(Level.INFO, "CSS Skin: " + CSSUtils.getSkinFromSite(site));
        Logger.getLogger(EpubExport.class.getName()).log(Level.INFO, "CSS Tool base Skin: " + CSSUtils.getCssToolBase());
        Logger.getLogger(EpubExport.class.getName()).log(Level.INFO, "CSS Portal Skin: " + CSSUtils.getCssPortalSkin(skinFolder));
        Logger.getLogger(EpubExport.class.getName()).log(Level.INFO, "CSS Tool base CDN: " + CSSUtils.getCssToolBaseCDN());
        String cssHref = CSSUtils.getCssToolBase().replaceFirst("/", "");
        String wrappedHTML = "";
        String siteCss = "<link href=\"".concat(cssHref).concat("\" type=\"text/css\" rel=\"stylesheet\" media=\"all\" />");
        URL siteUrl = new URL(site.getUrl());
        URL cssURL = new URL(siteUrl.getProtocol() + "://" + siteUrl.getHost() 
                + ( (siteUrl.getPort() != -1)? (":" + siteUrl.getPort()) : "")
                + CSSUtils.getCssToolBase());
        book.addContent(cssURL.openConnection().getInputStream(), "text/css", cssHref, false, false);
        if (!cssList.isEmpty()) {
            for (String css : cssList) {
                if (!"null".equals(css)) {
                    try {
                        ContentResource rs = contentHostingService.getResource(css);
                        book.addContent(rs.getContent(), "text/css", css, false, false);
                        siteCss = siteCss + "<link href=\"".concat(css.replaceFirst("/", "")).concat("\" type=\"text/css\" rel=\"stylesheet\" media=\"all\" />");
                    } catch (Exception e) {
                        contentStr = contentStr.concat(handleError(
                                "ERROR in Epub Exporter! An exception occured while appending the sites styling file (" + css + ") to the Epub on page:".concat(sectionName),
                                "simplepage.epub.error.attaching.css", e));
                        siteCss = "";
                    }
                }
            }
        }
        if (StringUtils.isNotEmpty(jsContent)) {
            String jsFilename = "library/js/".concat(getValidFilename(sectionName)).concat(".js");
            book.addContent(jsContent.getBytes(fixedCharSet), "text/javascript", jsFilename, false, false);
            siteCss = siteCss.concat("<script type=\"text/javascript\" src=\"".concat(jsFilename).concat("\"></script>"));
        }
        contentStr = "\n<br/>".concat(contentStr);
        wrappedHTML = wrappedHTML + MessageFormat.format(htmlWrapper, sectionName, siteCss, htmlCleaner.cleanHTMLString(contentStr));

        Content bookContent = book.addContent(wrappedHTML.getBytes(fixedCharSet), "application/xhtml+xml",
                getValidFilename(sectionName).concat(".xhtml"), true, true);
        if (StringUtils.isNotEmpty(jsContent)) {
            bookContent.setProperties("scripted");
        }
    }

    /**
     * Creates the Assignment Resource file with a link
     *
     * @param book
     * @param spi
     * @param site
     * @param params
     */
    private String createAssignment(SimplePageItem spi, Site site) {
        try {
            return addSakaiToollinkToContent(dataExportService.getAssignmentURL(spi, site), dataExportService.getAssignmentTitle(spi));
        } catch (Exception e) {
            return handleError("ERROR in Epub Exporter! An exception occured while the Assignment for this Lesson was exported. ID : ".concat(spi.getSakaiId()),
                    "simplepage.epub.error.creating.assignment", e);
        }
    }

    /**
     * Adds a Sakai Tool link
     *
     * @param spi
     * @param site
     * @param url
     * @param title
     */
    private String addSakaiToollinkToContent(String url, String title) {
        return MessageFormat.format(sakaiToollinkHTML, url, title).concat("<br>");
    }

    /**
     * Creates the Assessment Resource file with a link
     *
     * @param book
     * @param spi
     * @param site
     * @param params
     */
    private String createAssessment(SimplePageItem spi, Site site) {
        try {
            return addSakaiToollinkToContent(dataExportService.getAssessmentURL(spi, site), dataExportService.getAssessmentTitle(spi));
        } catch (Exception e) {
            return handleError(
                    "ERROR in Epub Exporter! An exception occured while the Assessment for this Lesson was exported. ID : ".concat(spi.getSakaiId()),
                    "simplepage.epub.error.creating.assessment",
                    e);
        }
    }

    /**
     * Adds SimplePageItem content of text type to the EPUB document Handle the
     * image and object tags
     *
     * @param book
     * @param spi
     * @throws Exception
     */
    private String createText(EpubBook book, SimplePageItem spi, Site site, String lessonName) {
        spi = hasImagePdfOrVideo(book, spi, site, lessonName);
        return htmlCleaner.handleEPUBHtml(spi.getHtml(), book);
    }

    /**
     * Add the HTML links of linked content
     *
     * @param spi
     */
    private String createResource(EpubBook book, SimplePageItem spi, Site site, String lessonName) {
        if (spi.getName().contains(".pdf")
                || (imageExtensions.contains(FilenameUtils.getExtension(spi.getName())))
                || isAuthoringTool(spi, "'")) {
            return createMultimedia(book, spi, site, lessonName);
        }
        return MessageFormat.format(linkHTML, ServerConfigurationService.getAccessUrl().concat("/content").concat(spi.getSakaiId()), spi.getName());
    }

    /**
     * Add multimedia content to the EPUB HTML content
     *
     * determine whether this links to a articulate/Adobe Edge include all
     * E-learning tool data
     *
     * @param book
     * @param spi
     * @param site
     */
    private String createMultimedia(EpubBook book, SimplePageItem spi, Site site, String lessonName) {
        String type = "";
        if (isAuthoringTool(spi, type)) {
            return addElearningContent(spi, book, type);
        }
        String multimediaDisplayType = spi.getAttribute("multimediaDisplayType");
        if ("3".equals(multimediaDisplayType)) {
            String url = spi.getAttribute("multimediaUrl");
            return MessageFormat.format(linkHTML, url, url);
        } else if ("2".equals(multimediaDisplayType)) {
            String url = spi.getName();
            return MessageFormat.format(linkHTML, url, url);
        } else if ("1".equals(multimediaDisplayType)) {
            return htmlCleaner.convertIframeToLink(spi.getAttribute("multimediaEmbedCode"));
        } else {
            if (spi.getName().contains(".pdf")) {
                try {
                    String fullLink = "";
                    String linkHref;
                    boolean isLocal = false;
                    if (spi.getType() == 1 && !spi.getHtml().equals("text/html")) {
                        linkHref = spi.getSakaiId();
                        fullLink = ServerConfigurationService.getAccessUrl().concat("/content").concat(spi.getSakaiId());
                        isLocal = true;
                    } else {
                        fullLink = new String(contentHostingService.getResource(spi.getSakaiId()).getContent(), StandardCharsets.UTF_8);
                        linkHref = java.net.URLDecoder.decode(fullLink.substring(fullLink.lastIndexOf("/group"), fullLink.length()), "UTF-8");
                        isLocal = new URL(site.getUrl()).getHost().equals(new URL(fullLink).getHost());
                    }
                    if (isLocal) {
                        return embedContentResourceGivenURL(linkHref, book, spi, "pdf", fullLink);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(EpubExport.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (imageExtensions.contains(FilenameUtils.getExtension(spi.getName())) && spi.getType() != 7) {
                try {
                    String fullLink;
                    String linkHref;
                    boolean isLocal = false;
                    if (spi.getType() == 1 && spi.getHtml().contains("image/")) {
                        linkHref = spi.getSakaiId();
                        fullLink = ServerConfigurationService.getAccessUrl().concat("/content").concat(spi.getSakaiId());
                        isLocal = true;
                    } else {
                        fullLink = new String(contentHostingService.getResource(spi.getSakaiId()).getContent(), StandardCharsets.UTF_8);
                        linkHref = java.net.URLDecoder.decode(fullLink.substring(fullLink.lastIndexOf("/group"), fullLink.length()), "UTF-8");
                        isLocal = new URL(site.getUrl()).getHost().equals(new URL(fullLink).getHost());
                    }
                    if (isLocal) {
                        return embedContentResourceGivenURL(linkHref, book, spi, "image", fullLink);
                    }
                } catch (Exception e) {
                    return handleError(
                            "ERROR in Epub Exporter! Error adding embedded resource to EPUB file. ID : ".concat(spi.getSakaiId()),
                            "simplepage.epub.error.creating.resource",
                            e);
                }
            }
            return handleEmbeddedResources(book, spi, site);
        }
    }

    /**
     * When given a Content Link, to a resource which contains the URL to the
     * desired Resource. Get the Resource URL and embed the actual resource into
     * the Ebook and modify the URL.
     *
     * @param linkHref
     * @param book
     * @param spi
     * @return
     * @throws UnsupportedEncodingException
     * @throws PermissionException
     * @throws IdUnusedException
     * @throws TypeException
     * @throws ServerOverloadException
     */
    private String embedContentResourceGivenURL(String linkHref, EpubBook book, SimplePageItem spi, String resourceType, String fullLink) throws UnsupportedEncodingException, PermissionException, IdUnusedException, TypeException, ServerOverloadException {
        ContentResource cr = getContentResourceFromURL(linkHref, false);
        String linkText = spi.getName();
        SimplePageItem spItem = spi;
        spItem.setSakaiId(cr.getReference());
        String embedRes = embedResourceFile(book, cr, spItem, linkText);
        if (resourceType.equals("pdf")) {
            if (addExternalLinkToPDF) {
                embedRes = embedRes.replace("</p>", "") + MessageFormat.format(plainLinkHTML, fullLink, "&nbsp;(View Online)</p>");
            }
        }
        spi.setHtml(spi.getHtml().replace(spi.getName(), embedRes));
        return embedRes;
    }

    /**
     * Handles the text,image and video types of multimedia
     *
     * @param book
     * @param spi
     * @param site
     */
    private String handleEmbeddedResources(EpubBook book, SimplePageItem spi, Site site) {
        try {
            ContentResource cr = dataExportService.getContentResourceData(spi.getSakaiId());
            if (cr == null) {
                return "<br/>" + spi.getName() + ": [*Deleted*] ";
            }
            String resourceLink = ServerConfigurationService.getAccessUrl().concat("/content").concat(spi.getSakaiId());
            if (fileSizeInclusionLimit <= cr.getContentLength()) {
                return MessageFormat.format(linkHTML, site.getUrl(), "View :" + spi.getName()
                        + MessageFormat.format(linkHTML, resourceLink, " Download :" + spi.getName() + " ( " + FileUtils.byteCountToDisplaySize(cr.getContentLength()) + " )"));
            }
            if ("text/url".equals(cr.getContentType())) {
                return MessageFormat.format(linkHTML, spi.getName(), spi.getName());
                // Embed Images
            } else if (cr.getContentType().contains("image/")) {
                return createContentResourceImageHTML(book, cr, spi);
                // Embed video    
            } else if (cr.getContentType().contains("video/")) {
                return embedMultiMediaContentResource(book, cr, spi, "video");
                // Embedded audio
            } else if (cr.getContentType().contains("audio/")) {
                return embedMultiMediaContentResource(book, cr, spi, "audio");
                // Only embed .PDF for now, behaviour in E-Readers unknown for .DOCX etc.
            } else if (cr.getContentType().contains("pdf")) {
                //Fallback method + regular link to external pdf
                String pdfLink = embedPDFResourceFile(book, cr, spi);
                if (addExternalLinkToPDF) {
                    pdfLink = pdfLink.replace("</p>", "") + MessageFormat.format(plainLinkHTML, resourceLink, "&nbsp;(View Online)</p>");
                }
                return pdfLink;

                // Any other embedded file will just be a link to the Sakai hosted resource    
            } else {
                return MessageFormat.format(linkHTML, ServerConfigurationService.getAccessUrl().concat("/content").concat(spi.getSakaiId()),
                        getCleanResourceName(spi.getSakaiId()));
            }
        } catch (IdUnusedException iue) {
            handleError(
                    "ERROR in Epub Exporter! The resource ID : ".concat(spi.getSakaiId()).concat(", was deleted from the Resources section. It has not been deleted from the Lesson. Please update the Lesson."),
                    "simplepage.epub.error.creating.resource",
                    iue);
            return MessageFormat.format(deleteResourceMessage, spi.getName() + ": [*Deleted*] ");

        } catch (Exception ex) {
            return handleError(
                    "ERROR in Epub Exporter! Error adding embedded resource to EPUB file. ID : ".concat(spi.getSakaiId()),
                    "simplepage.epub.error.creating.resource",
                    ex);
        }
    }

    /**
     * Embeds a resource file and adds a fall-back link to the Sakai resource.
     *
     * @param book
     * @param cr
     * @param spi
     * @return String HTML tag with a link to resource
     * @throws ServerOverloadException
     */
    private String embedPDFResourceFile(EpubBook book, ContentResource cr, SimplePageItem spi) throws ServerOverloadException {
        String cleanId = spi.getSakaiId().replaceFirst("/", "").replace(" ", "_");
        Content content = book.addContent(cr.getContent(), null, cleanId, false, true);
        content.setLinear(false);
        Content fallback = book.addTextContent(cleanId, "fallback/" + cleanId + ".xhtml",
                MessageFormat.format(linkHTML, ServerConfigurationService.getAccessUrl().concat("/content").concat(getValidFilename(cleanId)),
                        getValidFilename(cleanId)));
        fallback.setToc(false);
        content.setFallBack(fallback);
        return MessageFormat.format(linkHTML, cleanId, getCleanResourceName(cleanId));
    }

    /**
     * Embeds a resource file and adds a fall-back link to the Sakai resource.
     *
     * @param book
     * @param cr
     * @param spi
     * @return String HTML tag with a link to resource
     * @throws ServerOverloadException
     */
    private String embedResourceFile(EpubBook book, ContentResource cr, SimplePageItem spi, String linkText) throws ServerOverloadException {
        String cleanId = spi.getSakaiId().replaceFirst("/", "").replace(" ", "_");
        Content content = book.addContent(cr.getContent(), null, cleanId, false, true);
        content.setLinear(false);
        Content fallback = book.addTextContent(cleanId, "fallback/" + cleanId + ".xhtml",
                MessageFormat.format(linkHTML, getValidFilename(cleanId), linkText));
        fallback.setToc(false);
        content.setFallBack(fallback);
        return MessageFormat.format(embeddedPlainLinkHTML, cleanId, linkText);
    }

    /**
     * Embeds a PDF OutputStream and adds a fall-back link to the Sakai
     * resource.
     *
     * @param book
     * @param ByteArrayOutputStream
     * @param spi
     * @return String HTML tag with a link to resource
     * @throws ServerOverloadException
     */
    private String embedPDFOutputStreamContent(EpubBook book, ByteArrayOutputStream os, SimplePageItem spi) throws ServerOverloadException {
        String cleanId = spi.getSakaiId().replaceFirst("/", "").replace(" ", "_");
        try {
            Content content = book.addContent(os.toByteArray(), null, cleanId, false, true);
            content.setLinear(false);
            Content fallback = book.addTextContent(cleanId, "fallback/" + cleanId + ".xhtml",
                    MessageFormat.format(linkHTML, ServerConfigurationService.getAccessUrl().concat("/content").concat(getValidFilename(cleanId)),
                            getValidFilename(cleanId)));
            fallback.setToc(false);
            content.setFallBack(fallback);
        } catch (Exception e) {
            handleError(
                    "ERROR in Epub Exporter! The PDF could not be embedded ".concat(spi.getSakaiId()).concat("."),
                    "simplepage.epub.error.creating.resource",
                    e);
        }
        return MessageFormat.format(linkHTML, cleanId, getCleanResourceName(cleanId));
    }

    /**
     * Adds an ContentResource image to the EPUB book
     *
     * @param book
     * @param cr
     * @param spi
     * @throws ServerOverloadException
     */
    private String createContentResourceImageHTML(EpubBook book, ContentResource cr, SimplePageItem spi) throws ServerOverloadException {
        String href = getValidFilename(spi.getSakaiId().replaceFirst("/", ""));
        book.addContent(cr.getContent(), null, href, false, false);
        return MessageFormat.format(sizedImageHTML, href, getDimensionAttribute(spi));
    }

    /**
     * Adds an ContentResource image to the EPUB book
     *
     * @param book
     * @param cr
     * @param spi
     * @throws ServerOverloadException
     */
    private String createContentResourceHTML(EpubBook book, ContentResource cr, String filename, String linkText) throws ServerOverloadException {
        String href = getValidFilename(filename.replaceFirst("/", ""));
        book.addContent(cr.getContent(), null, href, false, false);
        return MessageFormat.format(plainLinkHTML, href, linkText);
    }

    /**
     * Builds the HTML attribute for height and width from the SimplePageItem
     * height and width properties
     *
     * @param spi
     * @return
     */
    private String getDimensionAttribute(SimplePageItem spi) {
        String attr = "";
        if (!StringUtils.isEmpty(spi.getHeight())) {
            attr = attr.concat("height='" + spi.getHeight() + "'");
        } else if (!StringUtils.isEmpty(spi.getWidth())) {
            attr = attr.concat("width='" + spi.getWidth() + "'");
        }
        return attr;
    }

    /**
     * Adds an ContentResource video/audio to the EPUB book
     *
     * @param book
     * @param cr
     * @param spi
     * @param string
     * @throws ServerOverloadException
     */
    private String embedMultiMediaContentResource(EpubBook book, ContentResource cr, SimplePageItem spi, String type) throws ServerOverloadException {
        String resourceLink = ServerConfigurationService.getAccessUrl().concat("/content").concat(spi.getSakaiId());
        book.addContent(cr.getContent(), null, spi.getSakaiId().replaceFirst("/", ""), false, false);
        String contentType = cr.getContentType();
        return MessageFormat.format(((type.equals("video")) ? html5VideoTag : html5AudioTag), spi.getSakaiId().replaceFirst("/", ""),
                contentType, resourceLink, resourceLink);
    }

    /**
     * Adds an ContentResource video/audio to the EPUB book
     *
     * @param book
     * @param cr
     * @param filename
     * @param string
     * @throws ServerOverloadException
     */
    private String embedMultiMediaContentResource(EpubBook book, ContentResource cr, String filename, String type) throws ServerOverloadException {
        String resourceLink = ServerConfigurationService.getAccessUrl().concat("/content").concat(filename);
        book.addContent(cr.getContent(), null, filename, false, false);
        String contentType = cr.getContentType();
        return MessageFormat.format(((type.equals("video")) ? html5VideoTag : html5AudioTag), filename,
                contentType, resourceLink, resourceLink);
    }

    /**
     * Creates the Forum topic Resource file with a link
     *
     * @param spi
     * @param site
     */
    private String createForumTopic(SimplePageItem spi, Site site) {
        return addSakaiToollinkToContent(dataExportService.getForumTopicURL(spi, site), dataExportService.getForumTopicTitle(spi));
    }

    /**
     * Adds SimplePageItem content of Question type to the EPUB document
     *
     * @param book
     * @param spi
     * @param params
     * @param site
     */
    private String createQuestion(SimplePageItem spi, ExportEpubViewParameters params, Site site, StringBuilder jsContent) {
        if (null != spi.getGradebookId()) {
            Collection<ToolConfiguration> tools = site.getTools("sakai.lessonbuildertool");
            String toolId = "";
            SimplePage simplePage = dataExportService.getLessonData(params.getPageId());
            for (ToolConfiguration tool : tools) {
                if (simplePage.getTitle().equals(tool.getTitle())) {
                    toolId = tool.getId();
                    break;
                }
            }
            if (StringUtils.isEmpty(toolId)) {
                return "";
            }
            String url = ServerConfigurationService.getToolUrl() + "/" + toolId
                    + "/ShowPage?returnView=#itemContainer::itemTable::item:" + (spi.getSequence() - 1) + ":";
            return MessageFormat.format(linkHTML, url, "Question : " + spi.getAttribute("questionText"));
        } else {
            return getQuestionHTML(spi, jsContent);
        }
    }

    /**
     * Creates a byte[] of the Question type
     *
     * @param spi
     * @return
     */
    private String getQuestionHTML(SimplePageItem spi, StringBuilder jsContent) {
        // Question Text
        String questionHtml = "<h3 style=\"color:grey\">" + spi.getAttribute("questionText") + "</h3><br>";
        String questionType = spi.getAttribute("questionType");
        if (StringUtils.equals(questionType, "shortanswer")) {
            questionHtml = questionHtml.concat(buildShortAnswerQuestion(spi, jsContent));
        } else if (StringUtils.equals(questionType, "multipleChoice")) {
            questionHtml = questionHtml.concat(buildMultipleChoiceQuestion(spi, jsContent));
        }
        return questionHtml;
    }

    /**
     * Appends HTML for Short Answer Questions
     *
     * @param spi
     */
    private String buildShortAnswerQuestion(SimplePageItem spi, StringBuilder jsContent) {
        StringBuilder questionHtml = new StringBuilder();

        String shortAnswerInputId = "itemContainer::itemTable::item:" + spi.getId() + ":shortanswerInput";
        String answerOutputId = "itemContainer::itemTable::item:" + spi.getId() + ":answerOutput";
        addShortQuestionHiddenContent(spi, questionHtml, shortAnswerInputId, answerOutputId, jsContent);

        questionHtml.append("Answer : ");
        //Construct answer input tag 
        questionHtml.append("<input type=\"text\" value=\"\" id=\"").append(shortAnswerInputId).append("\" onkeydown=\"onkeydownFunc(event)\">\n");
        //Add Answer Text Label
        questionHtml.append("<p><label id=\"").append(answerOutputId).append("\"></label></p>\n");
        //Construct Submit input tag 
        questionHtml.append("<input type=\"button\" value=\"Check Answer\" onclick=\"checkShortAnswer").append(spi.getId()).append("()\"><br><br>");
        //addGradingPaneLink(spi, html);
        return questionHtml.toString();
    }

    /**
     * Adds the hidden fields for short questions
     *
     * @param spi
     * @param html
     * @param questionAnswerId
     * @param questionCorrectTextId
     * @param questionIncorrectTextId
     * @param shortAnswerInputId
     * @param answerOutputId
     */
    private void addShortQuestionHiddenContent(SimplePageItem spi, StringBuilder html, String shortAnswerInputId, String answerOutputId, StringBuilder jsContent) {
        String questionAnswerId = "itemContainer::itemTable::item:" + spi.getId() + ":questionAnswer";
        String questionCorrectTextId = "itemContainer::itemTable::item:" + spi.getId() + ":questionCorrectText";
        String questionIncorrectTextId = "itemContainer::itemTable::item:" + spi.getId() + ":questionIncorrectText";
        addJavascriptShortAnswerFunction(spi.getId(), questionAnswerId, questionCorrectTextId, questionIncorrectTextId, shortAnswerInputId, answerOutputId, jsContent);
        String answersArrayStr = getAnswersArrayStr(spi);
        //Add questionAnswer hidden input
        html.append(MessageFormat.format(hiddenInput, questionAnswerId, answersArrayStr)).append("\n");
        //Add questionCorrectText hidden input
        html.append(MessageFormat.format(hiddenInput, questionCorrectTextId, spi.getAttribute("questionCorrectText"))).append("\n");
        //Add questionIncorrectText hidden input
        html.append(MessageFormat.format(hiddenInput, questionIncorrectTextId, spi.getAttribute("questionIncorrectText"))).append("\n");
    }

    /**
     * Adds short question JavaScript to the jsContent
     *
     * @param html
     * @param id
     * @param questionAnswerId
     * @param questionCorrectTextId
     * @param questionIncorrectTextId
     * @param shortAnswerInputId
     * @param answerOutputId
     */
    private void addJavascriptShortAnswerFunction(long id, String questionAnswerId,
            String questionCorrectTextId, String questionIncorrectTextId, String shortAnswerInputId, String answerOutputId, StringBuilder jsContent) {
        jsContent.append("	function checkShortAnswer").append(id).append("() {\n");
        jsContent.append("		document.getElementById(\"").append(answerOutputId).append("\").innerHTML = \"\";\n");
        jsContent.append("		var inputAnswer = document.getElementById(\"").append(shortAnswerInputId).append("\").value;\n");
        jsContent.append("		if(inputAnswer == \"\" || inputAnswer == null) {\n");
        jsContent.append("			return;\n");
        jsContent.append("		}\n");
        jsContent.append("		var answers = document.getElementById(\"").append(questionAnswerId).append("\").value;\n");
        jsContent.append("		var answersArr = answers.split(',');\n");
        jsContent.append("		var answersArray = JSON.parse(JSON.stringify(answersArr));\n");
        jsContent.append("		var count = 0;\n");
        jsContent.append("		while (count != answersArray.length) {\n");
        jsContent.append("			if(answersArray[count] == (inputAnswer)) {\n");
        jsContent.append("				if( document.getElementById(\"").append(questionCorrectTextId).append("\").value == \"\" ) {\n");
        jsContent.append("					document.getElementById(\"").append(answerOutputId).append("\").innerHTML = \"Correct\";\n");
        jsContent.append("					return;\n");
        jsContent.append("				}else{ \n");
        jsContent.append("					document.getElementById(\"").append(answerOutputId).append("\").innerHTML = document.getElementById(\"").append(questionCorrectTextId).append("\").value;\n");
        jsContent.append("					return;\n");
        jsContent.append("				}\n");
        jsContent.append("			}\n");
        jsContent.append("			count = count + 1;\n");
        jsContent.append("		}\n");
        jsContent.append("		if(document.getElementById(\"").append(questionIncorrectTextId).append("\").value == \"\"){\n");
        jsContent.append("			document.getElementById(\"").append(answerOutputId).append("\").innerHTML = \"Incorrect\";\n");
        jsContent.append("		}else{\n");
        jsContent.append("			document.getElementById(\"").append(answerOutputId).append("\").innerHTML = document.getElementById(\"").append(questionIncorrectTextId).append("\").value;\n");
        jsContent.append("		}\n");
        jsContent.append("	}\n");
        jsContent.append("\n");
    }

    /**
     * Return an Array in String format
     *
     * @param spi
     * @return
     */
    private String getAnswersArrayStr(SimplePageItem spi) {
        StringBuilder answersArrayStr = new StringBuilder();
        String questionAnswer = spi.getAttribute("questionAnswer");
        String[] answers = questionAnswer.split("\\n");
        for (String answer : answers) {
            answersArrayStr.append(answer);
            answersArrayStr.append(",");
        }
        return answersArrayStr.toString().substring(0, answersArrayStr.toString().length() - 1);
    }

    /**
     * Appends HTML for Multiple Choice Questions
     *
     * @param spi
     */
    private String buildMultipleChoiceQuestion(SimplePageItem spi, StringBuilder jsContent) {
        StringBuilder questionHtml = new StringBuilder();
        String answerOutputId = "itemContainer::itemTable::item:" + spi.getId() + ":answerOutput";
        addMultiQuestionHiddenContent(spi, questionHtml, answerOutputId, jsContent);
        List<Map> answers = (List<Map>) spi.getJsonAttribute("answers");
        int count = 0;
        for (Map answerMap : answers) {
            Long id = (Long) answerMap.get("id");
            String text = (String) answerMap.get("text");
            //Construct answer input tag 
            String inputId = "itemContainer::itemTable::item:" + spi.getId() + ":multipleChoiceAnswer:" + count + ":multipleChoiceAnswerRadio";
            questionHtml.append("<input id=\"").append(inputId).append("\"  type=\"radio\" value=\"").append(id).append("\" name=\"");
            //Construct name value
            questionHtml.append("itemContainer::itemTable::item:").append(spi.getId()).append(":multipleChoiceSelect:-selection").append("\">\n");
            //Construct answer input label tag
            questionHtml.append("<label for=\"").append(inputId).append("\">").append(text).append("</label>\n");
            count += 1;
            if (count != answers.size()) {
                questionHtml.append("<br>");
            }
        }

        //Add Answer Text Label
        questionHtml.append("<p><label id=\"").append(answerOutputId).append("\"></label></p>");
        //Construct Submit input tag 
        questionHtml.append("<br><input type=\"button\" value=\"Check Answer\" onclick=\"checkMultipleAnswer").append(spi.getId()).append("()\"><br><br>");
        //addGradingPaneLink(spi, html);		
        return questionHtml.toString();
    }

    /**
     * Adds the hodden content for multi choice questions
     *
     * @param spi
     * @param html
     */
    private void addMultiQuestionHiddenContent(SimplePageItem spi, StringBuilder html, String answerOutputId, StringBuilder jsContent) {
        String questionAnswerId = "itemContainer::itemTable::item:" + spi.getId() + ":questionAnswer";
        String questionCorrectTextId = "itemContainer::itemTable::item:" + spi.getId() + ":questionCorrectText";
        String shortAnswerInputId = "itemContainer::itemTable::item:" + spi.getId() + ":shortanswerInput";
        String questionIncorrectTextId = "itemContainer::itemTable::item:" + spi.getId() + ":questionIncorrectText";
        addJavascriptMultiAnswerFunction(jsContent, spi.getId(), questionAnswerId, questionCorrectTextId, questionIncorrectTextId, shortAnswerInputId, answerOutputId);
        String correctAnswerIds = getMultiAnswersCorrectAnswerIds(spi);
        //Add questionAnswer hidden input
        html.append(MessageFormat.format(hiddenInput, questionAnswerId, correctAnswerIds)).append("\n");
        //Add questionCorrectText hidden input
        html.append(MessageFormat.format(hiddenInput, questionCorrectTextId, spi.getAttribute("questionCorrectText"))).append("\n");
        //Add questionIncorrectText hidden input
        html.append(MessageFormat.format(hiddenInput, questionIncorrectTextId, spi.getAttribute("questionIncorrectText"))).append("\n");
    }

    /**
     * Appends the JavaScript for multi choice questions
     *
     * @param html
     * @param id
     * @param questionAnswerId
     * @param questionCorrectTextId
     * @param questionIncorrectTextId
     * @param shortAnswerInputId
     * @param answerOutputId
     */
    private void addJavascriptMultiAnswerFunction(StringBuilder html, long id, String questionAnswerId,
            String questionCorrectTextId, String questionIncorrectTextId, String shortAnswerInputId, String answerOutputId) {
        html.append("	function checkMultipleAnswer").append(id).append("() {\n");
        html.append("		document.getElementById(\"").append(answerOutputId).append("\").innerHTML = \"\"; \n");
        html.append("		var radioAnswer = document.getElementById(\"").append(questionAnswerId).append("\").value;\n");
        html.append("		var radioAnswerIds = radioAnswer.split(\",\");\n");
        html.append("		var count = 0;\n");
        html.append("		while (count != radioAnswerIds.length) { \n");
        html.append("			if (document.getElementById(radioAnswerIds[count]).checked) {\n");
        html.append("			   if(document.getElementById(\"").append(questionCorrectTextId).append("\").value == \"\"){ \n");
        html.append("			   		document.getElementById(\"").append(answerOutputId).append("\").innerHTML = \"Correct. \"; \n");
        html.append("			   }else{ \n");
        html.append("			  document.getElementById(\"").append(answerOutputId).append("\").innerHTML = \"Correct. \" + document.getElementById(\"").append(questionCorrectTextId).append("\").value;\n");
        html.append("			  break;\n");
        html.append("			  }\n");
        html.append("			} else {\n");
        html.append("				document.getElementById(\"").append(answerOutputId).append("\").innerHTML = \"Incorrect. \" + document.getElementById(\"").append(questionIncorrectTextId).append("\").value;\n");
        html.append("			}\n");
        html.append("			count = count + 1;\n");
        html.append("		}\n");
        html.append("	}\n");
        html.append("\n");
    }

    /**
     * Return all the correct Multi Answers
     *
     * @param spi
     * @return
     */
    private String getMultiAnswersCorrectAnswerIds(SimplePageItem spi) {
        StringBuilder answerIds = new StringBuilder();
        List<Map> answers = (List<Map>) spi.getJsonAttribute("answers");
        boolean oneCorrect = false;
        for (Map answersMap : answers) {
            Long id = (Long) answersMap.get("id");
            Boolean isCorrect = (Boolean) answersMap.get("correct");
            if (isCorrect && oneCorrect) {
                answerIds.append(",itemContainer::itemTable::item:").append(spi.getId()).append(":multipleChoiceAnswer:").append((id - 1)).append(":multipleChoiceAnswerRadio");
            } else if (isCorrect && !oneCorrect) {
                answerIds.append("itemContainer::itemTable::item:").append(spi.getId()).append(":multipleChoiceAnswer:").append((id - 1)).append(":multipleChoiceAnswerRadio");
                oneCorrect = true;
            }
        }
        return answerIds.toString();
    }

    /**
     * Save the EPUB document to Resources
     *
     * @param book
     * @param siteId
     * @return the name of the saved resource
     * @throws Exception
     */
    private ContentResourceEdit saveEPUBFileInResources(EpubBook book, String siteId, String lessonName) throws Exception {
        // We do not want to save to file first so we use a ByteArrayOutputStream
        ByteArrayOutputStream bookByteOutStream = new ByteArrayOutputStream();
        book.writeToStream(bookByteOutStream);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String fileName = lessonName.concat("_").concat(sdf.format(new Date()));
        byte[] fileData = bookByteOutStream.toByteArray();
        String collectionId = contentHostingService.getSiteCollection(siteId);
        ContentCollection contentCollection = contentHostingService.getCollection(collectionId);
        ContentResourceEdit cre = contentHostingService.addResource(contentCollection.getId(), fileName, ".epub", 5);
        cre.setContent(fileData);
        cre.setContentLength(fileData.length);
        contentHostingService.commitResource(cre);
        return cre;
    }

    /**
     * Checks the toolSession to determine whether there are errors
     *
     * @return
     */
    private boolean hasErrors() {
        ToolSession toolSession = SessionManager.getCurrentToolSession();
        if (toolSession == null) {
            return false;
        }
        List<String> errors = (List<String>) toolSession.getAttribute("lessonbuilder.errors");
        if (null == errors) {
            return false;
        }
        return !errors.isEmpty();
    }

    /**
     * Standard method of handling exceptions within the EPUB tool
     *
     * @param errorMessage - the message which will be displayed to the user
     * @param e - The exception object which will be logged and used for the
     * session errors
     */
    private String handleError(String errorMessage, String errorKey, Exception e) {
        String errorString = "";
        log.error(errorMessage, e);
        if (addErrorToDoc) {
            errorString = MessageFormat.format(styledError, errorMessage);
        }
        if (addErrorToSession) {
            if (errorMessage == null) {
                errorKey = messageLocator.getMessage(errorKey);
            } else {
                errorKey = messageLocator.getMessage(errorKey).concat(" : ").concat(errorMessage);
            }
            exportHelper.setErrMessage(errorKey, true, "simplepage.epub.error.header");
        }
        return errorString;
    }

    /**
     * Process the sub pages linked on a lesson
     *
     * @param book
     * @param site
     * @param params
     * @param pageIndex
     * @param content
     * @param cssList
     * @throws Exception
     */
    public void processSubPages(EpubBook book, Site site, ExportEpubViewParameters params, List<ExportPageStructure> pageIndex, StringBuilder content, List<String> cssList, String lessonName) throws Exception {
        StringBuilder jsContent = new StringBuilder();
        for (ExportPageStructure nses : pageIndex) {
            content = new StringBuilder();
            SimplePageItem spi = nses.getSpi();
            SimplePage subPageData = dataExportService.getLessonData(Long.parseLong(spi.getSakaiId()));
            List<SimplePageItem> simplePageItemList = dataExportService.getPageItems(subPageData.getPageId());
            if (!nses.isHasBeenRendered()) {
                //content.append(addSimplePageNameHeader(spi));
                addLessonContent(book, site, params, simplePageItemList, content, jsContent, pageIndex, cssList, lessonName);
                TocLink tocLink = new TocLink(getValidFilename(subPageData.getTitle()).concat(".xhtml"),
                        StringUtils.repeat(" - ", getIndentationLevel(subPageData, 0)).concat(subPageData.getTitle()), null);
                book.getTocLinks().add(tocLink);
                populateCssList(subPageData, cssList);
                outputSection(book, subPageData.getTitle(), content.toString(), site, jsContent.toString(), cssList);
                updateRenderedPages(pageIndex, spi.getId(), subPageData.getPageId());
            }
        }
    }

    private void populateCssList(SimplePage sp, List<String> cssList) {
        if (sp != null) {
            if (null != sp.getCssSheet() && !"null".equals(sp.getCssSheet())) {
                cssList.add(sp.getCssSheet());
            }
        }
    }

    /**
     * When given a SimplePageItem determine whether this resource is a
     * e-learning tool or a content authoring tool
     *
     * @param SimplePageItem
     */
    private boolean isAuthoringTool(SimplePageItem spi, String type) {
        try {
            ContentResource rs = contentHostingService.getResource(spi.getSakaiId());
            type = rs.getContentType();
            ContentCollection cc = rs.getContainingCollection();
            for (ContentEntity ce : cc.getMemberResources()) {
                if (ce.getId().contains(EDGE_CHECK)) {
                    type = ADOBE_EDGE;
                    return true;
                }
                if (ce.getId().contains(ARTICULATE_META_CHECK)) {
                    ContentResource cr = contentHostingService.getResource(ce.getId());
                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                    Document doc = dBuilder.parse(new ByteArrayInputStream(cr.getContent()));
                    NodeList nodes = doc.getElementsByTagName(ARTICULATE_TAG_CHECK);
                    Element element = (Element) nodes.item(0);
                    if (element.hasAttribute("name")) {
                        String val = element.getAttribute("name");
                        if (ARTICULATE_CHECK.equals(val)) {
                            type = ARTICULATE;
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e);
        }
        return false;
    }

    /**
     * Add E-Learning content to the EPUB file This method simple sets the
     * correct folder depth. When an E-Learning package is imported into Sakai.
     * It is a zip file which is uploaded into the resources tool. In the lesson
     * tool a user must now choose the embed content on page option > Click the
     * "Or select an existing resource" link > Go into the Instructor_s Info
     * folder > Go two folders down (Edge) one folder (Articulate) then select
     * the the html file > Click Continue Adobe_Edge has a data structure of zip
     * file name for folder name, then repeats this in the child level. This
     * child folder then contains all the resources Articulate has a data
     * structure of zip file name for folder name This child folder then
     * contains all the resources
     *
     * @param spi
     * @param book
     * @param type
     * @return
     */
    private String addElearningContent(SimplePageItem spi, EpubBook book, String type) {
        try {
            ContentResource rs = contentHostingService.getResource(spi.getSakaiId());
            type = rs.getContentType();
            ContentCollection cc = rs.getContainingCollection();
            if (ADOBE_EDGE.equals(type)) {
                cc = cc.getContainingCollection();
            }
            addAllContentOfCollection(book, cc, cc.isCollection());
        } catch (Exception ex) {
            log.error(ex);
        }
        String fileLocation = spi.getSakaiId().replaceFirst("/", "");
        return MessageFormat.format(epubIframe, fileLocation, spi.getName(), "", spi.getName());
    }

    /**
     *
     * @param book
     * @param resource
     * @param isColletion
     */
    private void addAllContentOfCollection(EpubBook book, Object resource, Boolean isColletion) {
        if (isColletion) {
            ContentCollection cc = (ContentCollection) resource;
            for (ContentEntity ce : cc.getMemberResources()) {
                addAllContentOfCollection(book, ce, ce.isCollection());
            }
        } else {
            ContentResource rs = (ContentResource) resource;
            try {
                book.addContent(rs.getContent(), rs.getContentType(), rs.getId(), false, false);
            } catch (Exception e) {
                log.error(e);
            }
        }
    }

    /**
     * Given a filename, replaces illegal XML and File system characters with an
     * underscore
     *
     * @param currentName
     * @return
     */
    private String getValidFilename(String currentName) {
        return currentName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
    }

    /**
     * Sets the DataExportService instance
     *
     * @param dataExportService
     */
    public void setDataExportService(DataExportService dataExportService) {
        this.dataExportService = dataExportService;
    }

    /**
     * @param contentHostingService the contentHostingService to set
     */
    public void setContentHostingService(ContentHostingService contentHostingService) {
        this.contentHostingService = contentHostingService;
    }

    /**
     * @param htmlCleaner the htmlCleaner to set
     */
    public void setHtmlCleaner(HtmlCleanerUtil htmlCleaner) {
        this.htmlCleaner = htmlCleaner;
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
     * @param coverImage the coverImage to set
     */
    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    /**
     * @param imageExtensions the imageExtensions to set
     */
    public void setImageExtensions(List<String> imageExtensions) {
        this.imageExtensions = imageExtensions;
    }

    /**
     * @param linkHTML the linkHTML to set
     */
    public void setLinkHTML(String linkHTML) {
        this.linkHTML = linkHTML;
    }

    /**
     * @param html5VideoTag the html5VideoTag to set
     */
    public void setHtml5VideoTag(String html5VideoTag) {
        this.html5VideoTag = html5VideoTag;
    }

    /**
     * @param sizedImageHTML the sizedImageHTML to set
     */
    public void setSizedImageHTML(String sizedImageHTML) {
        this.sizedImageHTML = sizedImageHTML;
    }

    /**
     * @param html5AudioTag the HTML5AudioTag to set
     */
    public void setHtml5AudioTag(String html5AudioTag) {
        this.html5AudioTag = html5AudioTag;
    }

    /**
     * @param exportHelper the exportHelper to set
     */
    public void setExportHelper(ExportHelper exportHelper) {
        this.exportHelper = exportHelper;
    }

    /**
     * Checks whether a simple page item of type text, has links to pdf or
     * videos inside of it. E.G a video link pointing to a video in the
     * resources tool of the site, within a html text editor It will add the
     * resource to the epub and update the link to point to the correct source
     * in the epub
     *
     * @param book
     * @param spi
     * @param site
     * @param lessonName
     * @return
     */
    private SimplePageItem hasImagePdfOrVideo(EpubBook book, SimplePageItem spi, Site site, String lessonName) {
        org.jsoup.nodes.Document doc = Jsoup.parse(spi.getHtml(), "UTF-8");
        Elements links = doc.select("a[href]");
        for (org.jsoup.nodes.Element link : links) {
            if (link.attr("href").contains(".pdf")) {
                String linkHref = link.attr("href");
                try {
                    if (isLinkToLocalResource(site, link)) {
                        ContentResource cr = getContentResourceFromURL(linkHref, false);
                        String linkText = link.text();
                        String fullLink = linkHref;
                        linkHref = java.net.URLDecoder.decode(linkHref.substring(linkHref.lastIndexOf("/group"), linkHref.length()), "UTF-8");
                        linkHref = linkHref.replaceFirst("/", "");
                        SimplePageItem spItem = spi;
                        spItem.setSakaiId(cr.getReference());
                        String embedRes = embedResourceFile(book, cr, spItem, linkText);
                        if (addExternalLinkToPDF) {
                            embedRes = embedRes.replace("</p>", "") + MessageFormat.format(plainLinkHTML, fullLink, "&nbsp;(View Online)</p>");
                        }
                        spi.setHtml(spi.getHtml().replace(link.toString(), embedRes));
                    }
                } catch (Exception e) {
                    log.error("unable to embed PDF", e);
                }
            }
            if (imageExtensions.contains(FilenameUtils.getExtension(link.attr("href"))) && link.attr("data-pre-embedded").isEmpty()) {
                String linkHref = link.attr("href");
                try {
                    if (isLinkToLocalResource(site, link) && link.attr("data-pre-embedded").isEmpty()) {
                        ContentResource cr = getContentResourceFromURL(linkHref, false);
                        String linkText = link.text();
                        linkHref = java.net.URLDecoder.decode(linkHref.substring(linkHref.lastIndexOf("/group"), linkHref.length()), "UTF-8");
                        linkHref = linkHref.replaceFirst("/", "");
                        SimplePageItem spItem = spi;
                        spItem.setSakaiId(cr.getReference());
                        String embedRes = embedResourceFile(book, cr, spItem, linkText);
                        spi.setHtml(spi.getHtml().replace(link.toString(), embedRes));
                    }
                } catch (Exception e) {
                    log.error("unable to embed Image", e);
                }
            }
        }
        return spi;
    }

    /**
     * Determine if a url link is pointing to a local Resource
     *
     * @param site
     * @param link
     * @return boolean
     * @throws MalformedURLException
     */
    private boolean isLinkToLocalResource(Site site, org.jsoup.nodes.Element link) throws MalformedURLException {
        if (!link.attr("data-pre-embedded").isEmpty()) {
            return true;
        }
        return new URL(site.getUrl()).getHost().equals(new URL(link.attr("href")).getHost());
    }

    /**
     * Given a URL to a local resource. Determine the proper SakaiId and
     * retrieve the ContentResource passing it by reference returning the
     * changed URL, which now points to the file location inside the epub
     *
     * @param linkHref
     * @param cr
     * @return
     * @throws UnsupportedEncodingException
     * @throws PermissionException
     * @throws IdUnusedException
     * @throws TypeException
     */
    private ContentResource getContentResourceFromURL(String linkHref, boolean isVideo) throws UnsupportedEncodingException, PermissionException, IdUnusedException, TypeException {
        linkHref = java.net.URLDecoder.decode(linkHref.substring(linkHref.lastIndexOf("/group"), linkHref.length()), "UTF-8");
        if (isVideo) {
            linkHref = linkHref.replaceFirst("/", "");
        }
        ContentResource cr = contentHostingService.getResource(linkHref);
        return cr;
    }
}
