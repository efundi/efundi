package org.sakaiproject.lessonbuildertool.util;

import java.text.MessageFormat;
import org.apache.commons.lang.StringUtils;
import org.docx4j.convert.in.xhtml.XHTMLImageHandler;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.org.xhtmlrenderer.docx.Docx4JFSImage;
import org.docx4j.org.xhtmlrenderer.docx.Docx4jUserAgent;
import org.docx4j.wml.P;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.lessonbuildertool.service.DataExportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 * This is the Sakai implementation of the XHTMLImageHandler. This used to 
 * handle image URL's that will need authentication by getting the content 
 * through the DataExportService
 * 
 * TODO : review the copied XHTMLImageHandlerImpl code
 * 
 * @author OpenCollab
 */
public class XHTMLImageHandlerSakai implements XHTMLImageHandler {

    public static Logger log = LoggerFactory.getLogger(XHTMLImageHandlerSakai.class);
    
    private DataExportService dataExportService;
    
    /**
     * ServerConfigurationService to get url base paths
     */
    private ServerConfigurationService serverConfigurationService;

    @Override
    public void addImage(Docx4jUserAgent docx4jUserAgent, WordprocessingMLPackage wordMLPackage, P p, Element e, Long cx, Long cy) {
        boolean isError = false;
        try {   
            String url = e.getAttribute("src");
            // Get sakai resource
            byte[] imageBytes = getSakaiImage(url, docx4jUserAgent);        
            
            if (imageBytes == null) {
                isError = true;
            } else {
            	BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, imageBytes);
                Inline inline;
                if (cx == null && cy == null) {
                    inline = imagePart.createImageInline(null, e.getAttribute("alt"), 0, 1, false);
                } else {
                    if (cx == null) {
                        cx = imagePart.getImageInfo().getSize().getWidthPx()
                                * (cy / imagePart.getImageInfo().getSize().getHeightPx());

                    } else if (cy == null) {
                        cy = imagePart.getImageInfo().getSize().getHeightPx()
                                * (cx / imagePart.getImageInfo().getSize().getWidthPx());
                    }
                    inline = imagePart.createImageInline(null, e.getAttribute("alt"), 0, 1, cx, cy, false);
                }

                // Now add the inline in w:p/w:r/w:drawing
                org.docx4j.wml.R run = Context.getWmlObjectFactory().createR();
                p.getContent().add(run);
                org.docx4j.wml.Drawing drawing = Context.getWmlObjectFactory().createDrawing();
                run.getContent().add(drawing);
                drawing.getAnchorOrInline().add(inline);
            }
        } catch (Exception e1) {
            log.error(MessageFormat.format("Error during image processing: ''{0}'', insert default text.", new Object[]{e.getAttribute("alt")}), e1);
            isError = true;
        }

        if (isError) {
            org.docx4j.wml.R run = Context.getWmlObjectFactory().createR();
            p.getContent().add(run);

            org.docx4j.wml.Text text = Context.getWmlObjectFactory().createText();
            text.setValue("[MISSING IMAGE: " + e.getAttribute("alt") + ", " + e.getAttribute("alt") + " ]");

            run.getContent().add(text);
        }
    }
    
    /**
     * Retrieve the image byte[] 
     * 
     * Get the content from the DataExportService if it is a resource that 
     * would need authentication via the url. 
     * 
     * @param url
     * @param docx4jUserAgent
     * @return 
     */
    private byte[] getSakaiImage(String url, Docx4jUserAgent docx4jUserAgent) throws ServerOverloadException, Exception {
        String accessURL = serverConfigurationService.getAccessUrl();
        // Get resource files from the DataExportService
        if (url.contains(accessURL)) {
            String resourceId = java.net.URLDecoder.decode(StringUtils.remove(url, accessURL + "/content"), "UTF-8");
            ContentResource cr = dataExportService.getContentResourceData(resourceId);
            return cr.getContent();
        }else {
            Docx4JFSImage docx4JFSImage = docx4jUserAgent.getDocx4JImageResource(url);
            return docx4JFSImage==null?null:docx4JFSImage.getBytes();
        }
    }

    /**
     * @param serverConfigurationService the serverConfigurationService to set
     */
    public void setServerConfigurationService(ServerConfigurationService serverConfigurationService) {
        this.serverConfigurationService = serverConfigurationService;
    }

    /**
     * @param dataExportService the dataExportService to set
     */
    public void setDataExportService(DataExportService dataExportService) {
        this.dataExportService = dataExportService;
    }

}