package org.sakaiproject.blogwow.tool.otp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.sakaiproject.blogwow.logic.CommentLogic;
import org.sakaiproject.blogwow.logic.ExternalLogic;
import org.sakaiproject.blogwow.model.BlogWowComment;

import uk.org.ponder.beanutil.WriteableBeanLocator;
import uk.org.ponder.messageutil.TargettedMessage;
import uk.org.ponder.messageutil.TargettedMessageList;

public class CommentLocator implements WriteableBeanLocator {

    public static final String NEW_PREFIX = "new ";
    public static final String NEW_1 = NEW_PREFIX + "1";

    private ExternalLogic externalLogic;
    private CommentLogic commentLogic;
    private TargettedMessageList messages;
    
    private Map<String, BlogWowComment> delivered = new HashMap<String, BlogWowComment>();


	public void setMessages(TargettedMessageList messages) {
		this.messages = messages;
	}
    
    public Object locateBean(String name) {
        BlogWowComment togo = delivered.get(name);
        if (togo == null) {
            if (name.startsWith(NEW_PREFIX)) {
                // create the new object
                togo = new BlogWowComment(null, externalLogic.getCurrentUserId(), null, new Date(), new Date());
            } else {
                togo = commentLogic.getCommentById(name, externalLogic.getCurrentLocationId());
            }
            delivered.put(name, togo);
        }
        return togo;
    }

    public String publishAll() {
        for (String key : delivered.keySet()) {
            BlogWowComment comment = delivered.get(key);
            //if (key.startsWith(NEW_PREFIX)) {
            //    // could do stuff here
            //}
            if (comment.getText() == null || "".equals(comment.getText().trim()) || comment.getText().length() == 0 ) {
            	messages.addMessage(new TargettedMessage("blogwow.blogview.emptycomment",null,TargettedMessage.SEVERITY_ERROR));
            	return "error";
            }
            commentLogic.saveComment(comment, externalLogic.getCurrentLocationId());
        }
        return "published";
    }

    public boolean remove(String beanname) {
        commentLogic.removeComment(beanname, externalLogic.getCurrentLocationId());
        delivered.remove(beanname);
        return true;
    }

    public String removeAll() {
    	for (BlogWowComment comment : delivered.values()) {
            commentLogic.removeComment(comment.getId(), externalLogic.getCurrentLocationId());
        }
        return "removed";
    }
    
    public void setCommentLogic(CommentLogic commentLogic) {
        this.commentLogic = commentLogic;
    }

    public void setExternalLogic(ExternalLogic externalLogic) {
        this.externalLogic = externalLogic;
    }

    public void set(String beanname, Object toset) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
