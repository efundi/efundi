package org.sakaiproject.blogwow.tool.params;

/*
 * These view parameters specify what you need to pull up a single
 * blog. The location of the blog and the owner's userId.
 * 
 * The location is going to be a ref something like:
 *   /site/chem101 or
 *   /user/userid
 */
public class BlogParams extends BlogEntryParams {
  
  public boolean showcomments;
  public boolean addcomment;
  public Integer skip;
  public String locationId;
  
  public BlogParams() {}

 public BlogParams(String viewid, boolean showcomments, String locationId){
	 this.viewID = viewid;
	 this.showcomments = showcomments;
	 this.locationId = locationId;
 }
  
  public BlogParams(String viewid, String blogid, String entryid, boolean showcomments) {
      this.viewID = viewid;
      this.blogid = blogid;
      this.entryid = entryid;
      this.showcomments = showcomments;
      this.addcomment = false;
      this.skip = 0;
  }
  
  public BlogParams(String viewid, String blogid, String entryid, boolean showcomments, boolean addcomment) {
      this.viewID = viewid;
      this.blogid = blogid;
      this.entryid = entryid;
      this.showcomments = showcomments;
      this.addcomment = addcomment;
      this.skip = 0;
  }
  
  public BlogParams(String viewid, String blogid, int skip) {
      this.viewID = viewid;
      this.blogid = blogid;
      this.entryid = null;
      this.showcomments = false;
      this.addcomment = false;
      this.skip = skip;
  }
}
