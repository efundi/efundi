/******************************************************************************
 * BlogConstants.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2006 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.blogwow.constants;

/**
 * Stores constants for use throughout the blog services, logic layer, and dao layer Render constants should not be stored here
 * 
 * @author Sakai App Builder -AZ
 */
public class BlogConstants {

    /**
     * visible to owner only
     */
    public final static String PRIVACY_PRIVATE = "private";

    /**
     * visible to owner and group leader (instructor) of group related to blog
     */
    public final static String PRIVACY_GROUP_LEADER = "leader";

    /**
     * visible to group related to blog
     */
    public final static String PRIVACY_GROUP = "group";

    /**
     * visible to anyone
     */
    public final static String PRIVACY_PUBLIC = "public";

}
