package org.sakaiproject.lessonbuildertool.service;

import java.util.List;
import org.sakaiproject.api.app.messageforums.MessageForumsForumManager;
import org.sakaiproject.api.app.messageforums.Topic;
import org.sakaiproject.assignment.api.Assignment;
import org.sakaiproject.assignment.cover.AssignmentService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.lessonbuildertool.SimplePage;
import org.sakaiproject.lessonbuildertool.SimplePageItem;
import org.sakaiproject.lessonbuildertool.model.SimplePageToolDao;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedAssessmentData;
import org.sakaiproject.tool.assessment.facade.PublishedAssessmentFacadeQueriesAPI;

/**
 * Process data for Lesson tool and provide the various exporters a common
 * location for retrieving and parsing the same data
 *
 * @author OpenCollab
 *
 */
public class DataExportServiceImpl implements DataExportService {

    private SimplePageToolDao simplePageToolDao;
    private ContentHostingService contentHostingService;
    private SiteService siteService;
    private PublishedAssessmentFacadeQueriesAPI publishedAssessmentFacadeQueries;
    private final MessageForumsForumManager forumManager = (MessageForumsForumManager) ComponentManager.get("org.sakaiproject.api.app.messageforums.MessageForumsForumManager");

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SimplePage> getLessonStructure(String siteId) {
        return simplePageToolDao.getSitePages(siteId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SimplePage getLessonData(Long pageId) {
        return simplePageToolDao.getPage(pageId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SimplePageItem> getPageItems(long pageId) {
        return simplePageToolDao.findItemsOnPage(pageId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContentResource getContentResourceData(String resourceId) throws Exception {
        return contentHostingService.getResource(resourceId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Site getSite(String siteId) throws IdUnusedException {
        if (null == siteService) {
            return null;
        }
        return siteService.getSite(siteId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Assignment getAssignment(SimplePageItem spi) throws IdUnusedException, PermissionException {
        return AssignmentService.getAssignment(spi.getSakaiId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAssignmentTitle(SimplePageItem spi) throws IdUnusedException, PermissionException {
        return getAssignment(spi).getTitle();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAssignmentURL(SimplePageItem spi, Site site) throws IdUnusedException, PermissionException {
        ToolConfiguration tool = site.getToolForCommonId("sakai.assignment.grades");
        return ServerConfigurationService.getToolUrl() + "/" + tool.getId() + "?assignmentReference=/assignment/a/"
                + site.getId() + "/" + spi.getSakaiId() + "&panel=Main&sakai_action=doView_submission";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PublishedAssessmentData getAssessment(SimplePageItem spi) {
        String sakaiId = spi.getSakaiId();
        String assessmentIdString
                = sakaiId.lastIndexOf("/") == -1 ? sakaiId : sakaiId.substring(sakaiId.lastIndexOf("/") + 1);
        Long assessmentId = Long.parseLong(assessmentIdString);
        return publishedAssessmentFacadeQueries.loadPublishedAssessment(assessmentId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAssessmentTitle(SimplePageItem spi) {
        return getAssessment(spi).getTitle();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAssessmentURL(SimplePageItem spi, Site site) {
        ToolConfiguration tool = site.getToolForCommonId("sakai.lessonbuildertool");
        return ServerConfigurationService.getToolUrl() + "/" + tool.getId()
                + "/ShowItem?itemId=" + spi.getId() + "&source="
                + "%2Fsamigo-app%2Fservlet%2FLogin%3Fid%3D"
                + getAssessment(spi).getAssessmentMetaDataByLabel("ALIAS");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Topic getForumTopic(SimplePageItem spi) {
        String sakaiId = spi.getSakaiId();
        String assessmentIdString
                = sakaiId.lastIndexOf("/") == -1 ? sakaiId : sakaiId.substring(sakaiId.lastIndexOf("/") + 1);
        Long assessmentId = Long.parseLong(assessmentIdString);
        return forumManager.getTopicById(true, assessmentId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getForumTopicTitle(SimplePageItem spi) {
        return getForumTopic(spi).getTitle();
    }

    @Override
    public String getForumTopicURL(SimplePageItem spi, Site site) {
        ToolConfiguration tool = site.getToolForCommonId("sakai.lessonbuildertool");
        return ServerConfigurationService.getToolUrl() + "/" + tool.getId()
                + "/ShowItem?itemId=" + spi.getId() + "&source=%2Fdirect" + spi.getSakaiId();
    }

    // Setters and Getters
    public void setSimplePageToolDao(Object dao) {
        simplePageToolDao = (SimplePageToolDao) dao;
    }

    public void setContentHostingService(ContentHostingService chs) {
        contentHostingService = chs;
    }

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    /**
     * @param publishedAssessmentFacadeQueries the
     * publishedAssessmentFacadeQueries to set
     */
    public void setPublishedAssessmentFacadeQueries(PublishedAssessmentFacadeQueriesAPI publishedAssessmentFacadeQueries) {
        this.publishedAssessmentFacadeQueries = publishedAssessmentFacadeQueries;
    }
}
