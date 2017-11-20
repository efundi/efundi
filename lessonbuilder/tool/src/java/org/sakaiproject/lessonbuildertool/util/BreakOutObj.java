package org.sakaiproject.lessonbuildertool.util;

import org.jsoup.nodes.Element;

/**
 * Break Out object is used to determine whether Html paragraphs belong 
 * to a Container which breaks if it is not included as a whole. E.G. a table with body > tr > td, /td, td 
 * but is not closed the rest of the way will cause rendering errors.
 * @author OpenCollab
 */
public class BreakOutObj {
    private boolean childOfBreakOutContainer;
    private Element parentContainer;
    private int depth;

    /**
     * If the current paragraph is a child of a container return true
     * @return 
     */
    public boolean isChildOfBreakOutContainer() {
        return childOfBreakOutContainer;
    }

    /**
     * set the state of the paragraph, true if it is the child of a container, false if not.
     * @param childOfBreakOutContainer 
     */
    public void setIsChildOfBreakOutContainer(boolean childOfBreakOutContainer) {
        this.childOfBreakOutContainer = childOfBreakOutContainer;
    }

    /**
     * return the parent container data when the it is a child of a container
     * @return 
     */
    public Element getParentContainer() {
        return parentContainer;
    }
    
    /**
     * When it is the child of the container it must include the whole container element 
     * so that further tree walking is not required
     * @return 
     */
    public void setParentContainer(Element parentContainer) {
        this.parentContainer = parentContainer;
    }

    /**
     * Returns the depth of how deep the current paragraph is in the container.
     * Used for iteration in the import helper.
     * @return 
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Sets the depth of how deep the current paragraph is in the container.
     * Used for iteration in the import helper.
     */
    public void setDepth(int depth) {
        this.depth = depth;
    }

    @Override
    public String toString() {
        return "BreakOutObj{" + "childOfBreakOutContainer=" + childOfBreakOutContainer + ", parentContainer=" + parentContainer + ", depth=" + depth + '}';
    }
}
