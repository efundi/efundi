package org.sakaiproject.lessonbuildertool.tool.view;

import uk.org.ponder.rsf.viewstate.SimpleViewParameters;

/**
 *
 * @author OpenCollab
 */
public class ImportPdfViewParameters extends SimpleViewParameters {

	private long pageId;
	private String url = "";
	
	public ImportPdfViewParameters() {
		super();
	}

	public ImportPdfViewParameters(String VIEW_ID) {
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
