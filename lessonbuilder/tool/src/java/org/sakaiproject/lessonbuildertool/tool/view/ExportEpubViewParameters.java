package org.sakaiproject.lessonbuildertool.tool.view;

import uk.org.ponder.rsf.viewstate.SimpleViewParameters;

/**
 * View parameters used to build parameters for EPUB export link
 * 
 * @author OpenCollab
 */
public class ExportEpubViewParameters extends SimpleViewParameters {

	private long pageId;
	private String url = "";
	
	public ExportEpubViewParameters() {
		super();
	}

	public ExportEpubViewParameters(String VIEW_ID) {
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
}
