package org.sakaiproject.lessonbuildertool.service;

import java.util.List;
import org.sakaiproject.api.app.messageforums.Topic;
import org.sakaiproject.assignment.api.Assignment;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.lessonbuildertool.SimplePage;
import org.sakaiproject.lessonbuildertool.SimplePageItem;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.tool.assessment.data.ifc.assessment.PublishedAssessmentIfc;

/**
 * ExportService will gather and parse data required by the DocxExporter and
 * EpubExporter classes.
 *
 * @author OpenCollab
 *
 */
public interface DataExportService {

    /**
     * Retrieve the Lesson structure for the Site and return a list of
     * SimplePage data
     *
     * @param siteId
     * @return
     */
    public List<SimplePage> getLessonStructure(String siteId);

    /**
     *
     * @param pageId
     * @return
     */
    public SimplePage getLessonData(Long pageId);

    /**
     * Retrieve a list of SimplePageItems when given a pageId.
     *
     * @param pageId
     * @return
     */
    public List<SimplePageItem> getPageItems(long pageId);

    /**
     * Retrieve the byte Array of a resource when given the resourceId
     *
     * @param resourceId
     * @return
     */
    public ContentResource getContentResourceData(String resourceId) throws Exception;

    public Site getSite(String siteId) throws IdUnusedException;

    /**
     * Export the Assignment object for a specific SinglePageItem
     *
     * @param spi
     * @return Assignment
     * @throws IdUnusedException
     * @throws PermissionException
     */
    public Assignment getAssignment(SimplePageItem spi) throws IdUnusedException, PermissionException;

    /**
     * Export the Assignment title for a specific SinglePageItem
     *
     * @param spi
     * @return Assignment
     * @throws IdUnusedException
     * @throws PermissionException
     */
    public String getAssignmentTitle(SimplePageItem spi) throws IdUnusedException, PermissionException;

    /**
     * Export the Assignment URL for a specific SinglePageItem
     *
     * @param spi
     * @param site
     * @return Assignment
     * @throws IdUnusedException
     * @throws PermissionException
     */
    public String getAssignmentURL(SimplePageItem spi, Site site) throws IdUnusedException, PermissionException;

    /**
     * Export the Assessment object for a specific SinglePageItem
     *
     * @param spi
     * @return Assignment
     */
    public PublishedAssessmentIfc getAssessment(SimplePageItem spi);

    /**
     * Export the Assessment title for a specific SinglePageItem
     *
     * @param spi
     * @return Assignment
     */
    public String getAssessmentTitle(SimplePageItem spi);

    /**
     * Export the Assessment URL for a specific SinglePageItem
     *
     * @param spi
     * @param site
     * @return Assignment
     */
    public String getAssessmentURL(SimplePageItem spi, Site site);

    /**
     * Export the ForumTopic object for a specific SinglePageItem
     *
     * @param spi
     * @return Assignment
     */
    public Topic getForumTopic(SimplePageItem spi);

    /**
     * Export the ForumTopic title for a specific SinglePageItem
     *
     * @param spi
     * @return Assignment
     */
    public String getForumTopicTitle(SimplePageItem spi);

    /**
     * Export the ForumTopic URL for a specific SinglePageItem
     *
     * @param spi
     * @param site
     * @return Assignment
     */
    public String getForumTopicURL(SimplePageItem spi, Site site);

}
