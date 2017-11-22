package org.sakaiproject.lessonbuildertool.util;

/**
 * This class will be used when passing HTML into a tree like data 
 * structure
 * @author OpenCollab
 */
public class ImportTreeItem {
    private long id;
    private int level = 0;
    private int sequence = 0;
    private long parentId = 0;
    private String parentTitle = "";
    /**
     * 0 content
     * 1 new page/heading
     */
    private int type;
    private String title = "";
    private String content = "";
    private String htmlContent = "";

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    /**
     * Get the type
     * 0: content
     * 1: new page/heading
     * @return 
     */ 
    public int getType() {
        return type;
    }

    /**
     * Set the type
     * 0 content
     * 1 new page/heading
     * @param type 
     */
    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }
    
    /**
     * Returns the parent title, used for error messages in particular
     * @return 
     */
    public String getParentTitle() {
        return parentTitle;
    }

    public void setParentTitle(String parentTitle) {
        this.parentTitle = parentTitle;
    }
    
    @Override
    public String toString() {
        return "ImportTree{id=" + id + ", level=" + level + ", sequence=" + sequence + ", parentId=" + parentId + ", type=" + type + ", title=" + title + ", content=" + content + ", htmlContent=" + htmlContent + '}';
    }
}
