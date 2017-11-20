package org.sakaiproject.lessonbuildertool.pdfimport;

import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import org.sakaiproject.lessonbuildertool.tool.view.ImportPdfViewParameters;

/**
 *
 * @author OpenCollab
 */
public class PdfImport {

    private static final Logger LOG = Logger.getLogger(PdfImport.class.getName());
    /**
     * performs the
     *
     * @param sid
     * @param httpServletResponse
     * @param params
     */
    public void doImport(String sid, HttpServletResponse httpServletResponse,
            ImportPdfViewParameters params) {
        StringBuilder lessonName = new StringBuilder();
        LOG.info("here");
    }
}