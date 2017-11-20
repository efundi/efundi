package org.sakaiproject.lessonbuildertool;

/**
 * This class is used to keep track of the tree structure 
 * when exporting the lesson content.
 * 
 * When the lesson content must be rendered out of line/sequence it must be aware of
 * whether the page has already been rendered.
 * @author OpenCollab
 *
 */
public class ExportPageStructure {
	private long id;
	private SimplePageItem spi;
	private long parentId;
	private boolean hasBeenRendered;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public SimplePageItem getSpi() {
		return spi;
	}
	public void setSpi(SimplePageItem spi) {
		this.spi = spi;
	}
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	public boolean isHasBeenRendered() {
		return hasBeenRendered;
	}
	public void setHasBeenRendered(boolean hasBeenRendered) {
		this.hasBeenRendered = hasBeenRendered;
	}
	@Override
	public String toString() {
		return "NonSequentialExportStructure [id=" + id + ", spi=" + spi
				+ ", parentId=" + parentId + ", hasBeenRendered="
				+ hasBeenRendered + "]";
	}
}
