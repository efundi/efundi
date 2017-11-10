/******************************************************************************
 * BlogWowEntry.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2006 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.blogwow.model;

import java.util.Date;

/**
 * This is a single blog entry in a blog
 * 
 * @author Sakai App Builder -AZ
 */
public class BlogWowEntry {

    private String id;

    private BlogWowBlog blog;

    private String ownerId; // Sakai userId

    private String title;

    private String text;

    private String privacySetting;

    private Date dateModified;

    private Date dateCreated;

    /**
     * Default constructor
     */
    public BlogWowEntry() {
    }

    /**
     * Minimal constructor
     */
    public BlogWowEntry(BlogWowBlog blog, String ownerId, String title, String text, String privacySetting, Date dateModified) {
        this.blog = blog;
        this.ownerId = ownerId;
        this.title = title;
        this.text = text;
        this.privacySetting = privacySetting;
        this.dateModified = dateModified;
    }

    /**
     * Full constructor
     */
    public BlogWowEntry(BlogWowBlog blog, String ownerId, String title, String text, String privacySetting, Date dateModified,
            Date dateCreated) {
        this.blog = blog;
        this.ownerId = ownerId;
        this.title = title;
        this.text = text;
        this.privacySetting = privacySetting;
        this.dateModified = dateModified;
        this.dateCreated = dateCreated;
    }

    /**
     * Getters and Setters
     */
    public BlogWowBlog getBlog() {
        return blog;
    }

    public void setBlog(BlogWowBlog blog) {
        this.blog = blog;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrivacySetting() {
        return privacySetting;
    }

    public void setPrivacySetting(String privacySetting) {
        this.privacySetting = privacySetting;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

}
