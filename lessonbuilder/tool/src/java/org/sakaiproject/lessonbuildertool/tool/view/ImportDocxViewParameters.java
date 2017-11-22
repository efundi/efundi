package org.sakaiproject.lessonbuildertool.tool.view;

import uk.org.ponder.rsf.viewstate.SimpleViewParameters;

/**
 *
 * @author OpenCollab
 */
public class ImportDocxViewParameters extends SimpleViewParameters {

    private long pageId;
    private String url = "";
    private String fileName = "";
    private String originalFileName = "";

    public ImportDocxViewParameters() {
        super();
    }

    public ImportDocxViewParameters(String VIEW_ID) {
        super(VIEW_ID);
    }

    public long getPageId() {
        return pageId;
    }

    public void setPageId(long pageId) {
        this.pageId = pageId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }
}
