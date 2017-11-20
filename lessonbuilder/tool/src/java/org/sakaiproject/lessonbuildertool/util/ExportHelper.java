package org.sakaiproject.lessonbuildertool.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;

import uk.org.ponder.messageutil.MessageLocator;

public class ExportHelper {

    private final Log log = LogFactory.getLog(ExportHelper.class);
    // MessageLocator service required as per the Lessons tool exception
    private MessageLocator messageLocator;

    /**
     * Add errors to the tool session so that we display it to user when
     * refreshing the lesson page. We will still create the docx  || Epub but just
     * display the message
     *
     * @param errorMessage
     * @param showHeaderMessage
     * @param headerErrorMsg
     */
    public void setErrMessage(String errorMessage, boolean showHeaderMessage, String headerErrorMsg) {
        ToolSession toolSession = SessionManager.getCurrentToolSession();
        if (toolSession == null) {
            log.error("Lesson Builder error not in tool: " + errorMessage);
            return;
        }
        List<String> errors = (List<String>) toolSession.getAttribute("lessonbuilder.errors");
        if (errors == null) {
            errors = new ArrayList<String>();
            if (showHeaderMessage) {
                errors.add(messageLocator.getMessage(headerErrorMsg));
            }
            toolSession.setAttribute("lessonbuilder.errors", errors);
        }
        errors.add(errorMessage);
    }

    /**
     * Setter to inject MessageLocator
     *
     * @param messageLocator
     */
    public void setMessageLocator(MessageLocator messageLocator) {
        this.messageLocator = messageLocator;
    }
}
