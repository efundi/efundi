/******************************************************************************
 * BlogWowComment.java - created by Sakai App Builder -AZ
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
 * This is a single comment on a blog entry
 * 
 * @author Sakai App Builder -AZ
 */
public class BlogWowComment {

    private String id;

    private BlogWowEntry entry;

    private String ownerId; // Sakai userId

    private String text;

    private Date dateModified;

    private Date dateCreated;

    /**
     * Default constructor
     */
    public BlogWowComment() {
    }

    /**
     * Minimal constructor
     */
    public BlogWowComment(BlogWowEntry entry, String ownerId, String text, Date dateModified) {
        this.entry = entry;
        this.ownerId = ownerId;
        this.text = text;
        this.dateModified = dateModified;
    }

    /**
     * Full constructor
     */
    public BlogWowComment(BlogWowEntry entry, String ownerId, String text, Date dateModified, Date dateCreated) {
        this.entry = entry;
        this.ownerId = ownerId;
        this.text = text;
        this.dateModified = dateModified;
        this.dateCreated = dateCreated;
    }

    /**
     * Getters and Setters
     */
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

    public BlogWowEntry getEntry() {
        return entry;
    }

    public void setEntry(BlogWowEntry entry) {
        this.entry = entry;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

}
