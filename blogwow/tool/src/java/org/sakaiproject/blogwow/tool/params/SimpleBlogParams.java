/*
 * Created on 10 Jun 2007
 */
package org.sakaiproject.blogwow.tool.params;

import uk.org.ponder.rsf.viewstate.SimpleViewParameters;

public class SimpleBlogParams extends SimpleViewParameters {

    public String blogid;

    public SimpleBlogParams() {}

    public SimpleBlogParams(String viewid, String blogid) {
        this.viewID = viewid;
        this.blogid = blogid;
    }

}
