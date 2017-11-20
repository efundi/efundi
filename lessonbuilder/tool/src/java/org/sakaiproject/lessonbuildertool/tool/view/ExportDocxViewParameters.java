package org.sakaiproject.lessonbuildertool.tool.view;

import uk.org.ponder.rsf.viewstate.SimpleViewParameters;

/**
 * @author OpenCollab
 */
public class ExportDocxViewParameters extends SimpleViewParameters {

	private boolean exportDocx = false;
	private String toolId = "";
	private String url = "";
	private Long pageId = 0L;

	public ExportDocxViewParameters() {
		super();
	}

	public ExportDocxViewParameters(String VIEW_ID) {
		super(VIEW_ID);
	}

	public boolean isExportDocx() {
		return exportDocx;
	}

	public void setExportDocx(boolean exportDocx) {
		this.exportDocx = exportDocx;
	}

	public String getToolId() {
		return toolId;
	}

	public void setToolId(String toolId) {
		this.toolId = toolId;
	}

	public Long getPageId() {
		return pageId;
	}

	public void setPageId(Long pageId) {
		this.pageId = pageId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}