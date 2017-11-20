package org.sakaiproject.lessonbuildertool.util;

import coza.opencollab.epub.creator.model.EpubBook;

import java.net.URL;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.regex.Pattern;


import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.ContentNode;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.SimpleHtmlSerializer;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.lessonbuildertool.service.DataExportService;

/**
 * Utility class for using the HTMLCleaner API
 *
 * Sets the default properties and adds methods for commonly used operations
 *
 * @author OpenCollab
 */
public class HtmlCleanerUtil {

    private static final Logger LOG = Logger.getLogger(HtmlCleanerUtil.class);
    /**
     * The HTML cleaner
     */
    private final HtmlCleaner htmlSetup;
    /**
     * The properties for the cleaner.
     */
    private final CleanerProperties htmlProperties;
    /**
     * The HTML out
     */
    private final SimpleHtmlSerializer htmlSetdown;
    /**
     * The char set encoding
     */
    private final String CHARSET = "UTF-8";

    /**
     *
     */
    private DataExportService dataExportService;

    /**
     * ServerConfigurationService to get URL base paths
     */
    private ServerConfigurationService serverConfigurationService;

    /**
     * HTML template for displaying video
     */
    private String html5VideoTag = "<video controls=\"controls\">\n"
            + "<source src=\"{0}\" type=\"{1}\"/>\n"
            + "<p>If your reading system does not support HTML5 video, \n"
            + "this video is available in\n"
            + "<a href=\"{2}\">{2}</a>.</p>\n"
            + "</video>";
    
    private final String html5VideoTagWithDimensions = "<video width=\"{0}\" height=\"{1}\" controls=\"controls\">\n"
            + "<source src=\"{2}\" type=\"{3}\" />\n"
            + "<p>If your reading system does not support HTML5 video, \n"
            + "this video is available in\n"
            + "<a href=\"{4}\">{4}</a>.</p>\n"
            + "</video>";
    
    /**
     * HTML template for displaying audio
     */
    private String html5AudioTag = "<audio controls=\"controls\">\n"
            + "<source src=\"{0}\" type=\"{1}\"/>\n"
            + "<p>If your reading system does not support HTML5 audio, \n"
            + "this audio is available in\n"
            + "<a href=\"{2}\">{2}</a>.</p>\n"
            + "</audio>";
    
    /**
     * HTML template for displaying external video link
     */
    private String externalVideoTag = "<p>This video can be viewed online : \n"
            + "<a href=\"{0}\">{0}</a>.</p>";    
    
    // HTML used to display error messages for content that failed to be exported to EPUB
    private final String styledError = "<br/><div style=\"font-size;1.1em;border-radius:4px;border: 1px solid #cd0a0a;color:#cd0a0a;padding:4px;\">{0}</div><br/>";

    /**
     * Default constructor setting the HTML cleaner up.
     */
    public HtmlCleanerUtil() {
        htmlSetup = new HtmlCleaner();
        htmlProperties = htmlSetup.getProperties();
        htmlProperties.setAdvancedXmlEscape(true);
        htmlProperties.setUseEmptyElementTags(false);
        htmlProperties.setCharset(CHARSET);
        htmlProperties.setOmitDoctypeDeclaration(true);
        htmlProperties.setOmitHtmlEnvelope(true);
        htmlProperties.setOmitXmlDeclaration(true);
        htmlProperties.setRecognizeUnicodeChars(false);
        htmlSetdown = new SimpleHtmlSerializer(htmlProperties);
    }

    /**
     * Converts all iframe tags in the HTML doc to a tags
     *
     * @param htmlText
     * @return
     */
    public String convertIframeToLink(String htmlText) {
        TagNode node = getHtmlSetup().clean(htmlText);
        node.traverse(new IframeTagNodeVisitor());
        return htmlSetdown.getAsString(node);
    }

    /**
     * Handles images and embedded video for EPUB. The images will be added 
     * as resources and video will either embed as HTML5 tags or for external 
     * video a link will be added.
     *
     * @param htmlText
     * @param epubBook
     * @return
     */
    public String handleEPUBHtml(String htmlText, EpubBook epubBook) {
        TagNode node = getHtmlSetup().clean(htmlText);
        node.traverse(new EPUBTextTagNodeVisitor(epubBook));
        return htmlSetdown.getAsString(node);
    }

    /**
     * Returns cleaned HTML by reordering individual elements and producing 
     * well-formed XML
     *
     * @param dirtyHTML
     * @return
     */
    public String cleanHTMLString(String dirtyHTML) {
        TagNode node = getHtmlSetup().clean(dirtyHTML);
        return htmlSetdown.getAsString(node);
    }

    /**
     * @param dataExportService the dataExportService to set
     */
    public void setDataExportService(DataExportService dataExportService) {
        this.dataExportService = dataExportService;
    }

    /**
     * @param serverConfigurationService the serverConfigurationService to set
     */
    public void setServerConfigurationService(ServerConfigurationService serverConfigurationService) {
        this.serverConfigurationService = serverConfigurationService;
    }

    /**
     * @param html5VideoTag the html5VideoTag to set
     */
    public void setHtml5VideoTag(String html5VideoTag) {
        this.html5VideoTag = html5VideoTag;
    }

    /**
     * @param html5AudioTag the html5AudioTag to set
     */
    public void setHtml5AudioTag(String html5AudioTag) {
        this.html5AudioTag = html5AudioTag;
    }

    /**
     * @param externalVideoTag the externalVideoTag to set
     */
    public void setExternalVideoTag(String externalVideoTag) {
        this.externalVideoTag = externalVideoTag;
    }

    /**
     * @return the htmlSetup
     */
    public HtmlCleaner getHtmlSetup() {
        return htmlSetup;
    }

    /**
     * TagNodeVisitor implementation that replaces iframes with links to the
     * iframe src
     */
    class IframeTagNodeVisitor implements TagNodeVisitor {

        @Override
        public boolean visit(TagNode tagNode, HtmlNode htmlNode) {
            if (htmlNode instanceof TagNode) {
                TagNode tag = (TagNode) htmlNode;
                String tagName = tag.getName();
                String src = tag.getAttributeByName("src");
                if ("iframe".equals(tagName) && src != null) {
                    String cleanedSource = cleanIframeSource(src);
                    TagNode parent = tag.getParent();
                    int index = parent.getChildIndex(tag);
                    // Create a link tag and insert at current position
                    TagNode newTag = new TagNode("a");
                    ContentNode content = new ContentNode(cleanedSource);
                    newTag.addChild(content);
                    newTag.addAttribute("href", cleanedSource);
                    parent.insertChild(index, newTag);
                    // remove old/current iframe tag
                    parent.removeChild(tag);
                }
            }
            // tells visitor to continue traversing the DOM tree
            return true;
        }

        /**
         * Add protocol to the source if needed. We will have to use HTTP as we
         * do not know the protocol and DOCX needs a protocol to open link in
         * browser. This was tested for all known embed links and HTTP did work.
         *
         * @param src
         * @return
         */
        private String cleanIframeSource(String src) {
            if (src.indexOf("//") == 0) {
                return src.replaceFirst("//", "http://");
            } else {
                return src;
            }

        }
    }

    /**
     * TagNodeVisitor implementation that handles HTML added to the EPUB book.
     * Image and Object(video) TagNodes are handled
     */
    class EPUBTextTagNodeVisitor implements TagNodeVisitor {

        private final EpubBook epubBook;

        public EPUBTextTagNodeVisitor(EpubBook epubBook) {
            this.epubBook = epubBook;
        }

        @Override
        public boolean visit(TagNode tagNode, HtmlNode htmlNode) {
            if (htmlNode instanceof TagNode) {
                TagNode tag = (TagNode) htmlNode;
                String tagName = tag.getName();
                String src = tag.getAttributeByName("src");
                String data = tag.getAttributeByName("data");
                if ("img".equals(tagName) && src != null && tag.getAttributeByName("data-mathml") != null) {
                	addEPUBImageResource(src, tag);
                	removeInvalidMathMlAttributes(tag);
                } else if ("img".equals(tagName) && src != null ) {
                    if(tag.getAttributeByName("data-pre-embedded") == null){
                	addEPUBImageResource(src, tag);
                    }
                } else if ("object".equals(tagName) && data != null) {
                    addMultiMedia(tag);
                } else if("audio".equals(tagName) && tag.hasChildren()){
                	addAudio(tag);
                }
            }
            // tells visitor to continue traversing the DOM tree
            return true;
        }

        /**
         * Gets the image file and adds it as a Resource to the EPUB Book,
         * changes the src attribute to reference the EPUB resource
         *
         * @param url
         * @throws ServerOverloadException
         * @throws Exception
         */
        private void addEPUBImageResource(String url, TagNode tag) {
            try {
                // Check if it is a Sakai Resource
                url = URLDecoder.decode(url, "UTF-8");
                if (!isExternalSource(url)) {
                    String resourceID = addEPUBResource(url,null);
                    tag.addAttribute("src", resourceID.replaceFirst("/", ""));
                } else {
                    URL imageURL = new URL(url);
                    String fileName = "externalImages" + imageURL.getPath();
                    epubBook.addContent(imageURL.openStream(), null, fileName, false, false);
                    tag.addAttribute("src", fileName);
                }
            } catch (Exception ex) {
                LOG.error("Could not save image as EPUB resource - this image will link to external URL : " + url, ex);
                replaceTagWithNewHTML(tag, MessageFormat.format(styledError, url + ", no longer exists"));
            }
        }
        
        /**
         * Gets a Sakai resource from the resource URL and add it to the EPUB
         * book
         * 
         * @param url
         * @throws Exception 
         */
        private String addEPUBResource(String url, String mimeType) throws Exception {
            url = java.net.URLDecoder.decode(url);
            String accessURL = serverConfigurationService.getAccessUrl();
            String resourceId = StringUtils.remove(url, accessURL + "/content");
            ContentResource cr = dataExportService.getContentResourceData(resourceId);
            epubBook.addContent(cr.getContent(), mimeType, resourceId, false, false);
            return cr.getId();
        }

        /**
         * Handles the <object> tags. Checks if it is a Sakai or external source
         * and then adds the content to the EPUB book
         *
         * @param tag
         */
        private void addMultiMedia(TagNode tag) {
        	//TODO: rework this method to include audio to the EPUB as well (NSU-23 & NSU-44)
            for (TagNode child : tag.getChildTagList()) {
                if ("param".equals(child.getName())
                        && "FlashVars".equals(child.getAttributeByName("name"))) {
                    String src = getMultiMediaSource(child.getAttributeByName("value"));
                    if (isExternalSource(src)) {
                        replaceTagWithNewHTML(tag, MessageFormat.format(externalVideoTag, src));
                    } else {
                        addSakaiMultiMedia(tag, src);
                    }
                }
            }
        }
        
        /**
         * Handles the <source> tag used by the text tool when embedding audio.
         * @param tag
         */
        private void addAudio(TagNode tag){
        	for (TagNode child : tag.getChildTagList()) {
        		if ("source".equals(child.getName())){
        			alterHtmlAudioToHtml5AudioTag(tag, child.getAttributeByName("src"));
        		}
        	}
        }

        /**
         * the HtmlAudio tag to HTML5 Audio
         * @param tag
         * @param srcValue
         */
        private void alterHtmlAudioToHtml5AudioTag(TagNode tag, String srcValue){
        	Pattern p = Pattern.compile("content");
        	java.util.regex.Matcher m = p.matcher(srcValue);
        	if(m.find()){
        		srcValue = srcValue.substring(m.end()).trim();
        	}
        	try{
        		String type = "audio/" + FilenameUtils.getExtension(srcValue);
	    		String contentResourceId = addEPUBResource(srcValue, type);
	        	String newNode = MessageFormat.format(html5AudioTag, contentResourceId.replaceFirst("/", ""), type, srcValue);
	            replaceTagWithNewHTML(tag.getParent(), newNode);
        	}catch(Exception e){
        		LOG.error("Could not alter the HtmlAudio tag to HTML5 Audio URL : " + srcValue , e);
        	}
        }
        
        /**
         * Add HTML5 video tag and add the multimedia as 'n resource in the EPUB
         * book
         *
         * @param tag
         * @param src
         * @throws Exception
         */
        private void addSakaiMultiMedia(TagNode tag, String src) {
            try {
                String type = "video/" + FilenameUtils.getExtension(src);  
                String resourceId = addEPUBResource(src, null);       
                String newNode = MessageFormat.format(html5VideoTag, resourceId.replaceFirst("/", ""), type, src);
                if(null != tag.getAttributeByName("width") && null != tag.getAttributeByName("height"))
                {
                    newNode = MessageFormat.format(html5VideoTagWithDimensions, tag.getAttributeByName("width"),  tag.getAttributeByName("height"), resourceId.replaceFirst("/", ""), type, src);   
                }
                replaceTagWithNewHTML(tag, newNode);
            } catch (Exception ex) {
                LOG.error("Could not save video as EPUB resource - this video will link to external URL : " + src, ex);
                replaceTagWithNewHTML(tag, MessageFormat.format(externalVideoTag, src));
            }
        }

        /**
         * Replaces the current tag with a new tag created from the HTML text
         *
         * @param tag old Tag
         * @param newTagHtml new Tag HTML
         */
        private void replaceTagWithNewHTML(TagNode tag, String newTagHtml) {
            TagNode parent = tag.getParent();
            int index = parent.getChildIndex(tag);
            TagNode node = getHtmlSetup().clean(newTagHtml);
            parent.insertChild(index, node);
            // remove old/current iframe tag
            parent.removeChild(tag);
        }

        /**
         * Get the source URL of the multimedia flash object. Returns null if
         * not found
         *
         * @param flashVars
         * @return
         */
        private String getMultiMediaSource(String flashVars) {
            // split the params
            String[] vars = flashVars.split("&");
            // Get the src param and return the value
            for (String var : vars) {
                if (var.startsWith("src=")) {
                    return var.substring(4);
                }
            }
            return null;
        }

        /**
         * Check if the source is external or an Sakai resource link
         *
         * @param src
         * @return
         */
        private boolean isExternalSource(String src) {
            String accessURL = serverConfigurationService.getAccessUrl();
            return !src.startsWith(accessURL);
        }
        
        /**
         * Remove invalid attributes for MathMl Data
         * @param tag
         */
        private void removeInvalidMathMlAttributes(TagNode tag) {
			tag.removeAttribute("border");
			tag.removeAttribute("data-mathml");
			tag.removeAttribute("id");
		}
    }

}
