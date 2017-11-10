/*
 * Created on 10 Jun 2007
 */

package org.sakaiproject.blogwow.tool.params;

public class BlogEntryParams extends SimpleBlogParams {

    public String entryid;

    public BlogEntryParams() {
    }

    public BlogEntryParams(String viewid, String entryid) {
        this.viewID = viewid;
        this.entryid = entryid;
    }

    public BlogEntryParams(String viewid, String blogid, String entryid) {
        this.viewID = viewid;
        this.blogid = blogid;
        this.entryid = entryid;
    }

}
