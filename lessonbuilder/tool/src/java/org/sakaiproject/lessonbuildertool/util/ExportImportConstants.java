package org.sakaiproject.lessonbuildertool.util;

import java.util.Arrays;
import java.util.List;
import org.sakaiproject.component.cover.ServerConfigurationService;

/**
 * 
 * @author OpenCollab
 */
public class ExportImportConstants {
    
    public static final boolean EMBED_RESOURCES = Boolean.valueOf(ServerConfigurationService.getString("lessonbuilder.docx.embed.resources", "true"));
    public static final boolean SUB_PAGES_INLINE = Boolean.valueOf(ServerConfigurationService.getString("lessonbuilder.docx.subpages.inline", "true"));
    public static final String CK_HEADER1_STYLE = ServerConfigurationService.getString("lessonbuilder.export.ckheader1.style", "font-weight: bold;font-size: 22.3740986738pt;font-family: Calibri;");
    public static final String CK_HEADER2_STYLE = ServerConfigurationService.getString("lessonbuilder.export.ckheader2.style", "font-weight: bold;font-size: 14.54316413797pt;color: #555;font-family: Calibri;");
    public static final String CK_HEADER3_STYLE = ServerConfigurationService.getString("lessonbuilder.export.ckheader3.style", "font-weight: bold;font-size: 12.30575427059pt;color: #555;font-family: Calibri;");
    public static final String CK_HEADER4_STYLE = ServerConfigurationService.getString("lessonbuilder.export.ckheader4.style", "font-weight: bold;font-size: 11.1870493369pt;color: #555;font-family: Calibri;");

    public static final List<String> CK_HEADER_LIST = Arrays.asList(CK_HEADER1_STYLE, CK_HEADER2_STYLE, CK_HEADER3_STYLE, CK_HEADER4_STYLE);
    
    public static final boolean ADD_ERROR_TO_DOC = Boolean.valueOf(ServerConfigurationService.getString("lessonbuilder.docx.error.add.to.doc", "false"));
    public static final boolean ABORT_ON_ERROR = Boolean.valueOf(ServerConfigurationService.getString("lessonbuilder.docx.error.abort.doc.creation", "false"));
    public static final boolean ADD_ERROR_TO_SESSION = (ABORT_ON_ERROR) ? true : Boolean.valueOf(ServerConfigurationService.getString("lessonbuilder.docx.error.add.to.session", "false"));
    public static final String CHARSET = ServerConfigurationService.getString("lessonbuilder.docx.export.charset", "UTF-8");
    public static final List<String> STYLE_LIST = Arrays.asList("Title","Subtitle", "Heading1", "Heading2", "Heading3", "Heading4", "Heading5", "Heading6", "Heading7", "Heading8",
                                                         "CKTitle","CKSubtitle", "CKHeading1", "CKHeading2", "CKHeading3", "CKHeading4", "CKHeading5", "CKHeading6", "CKHeading7", "CKHeading8");
    // List of file extensions checked when searching for image type
    public static final List<String> IMAGE_EXTENSIONS = Arrays.asList("bmp", "gif", "icns", "ico", "jpg", "jpeg", "png", "tiff", "tif");
    
}
