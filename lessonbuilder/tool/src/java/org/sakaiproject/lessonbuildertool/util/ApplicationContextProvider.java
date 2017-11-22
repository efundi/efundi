package org.sakaiproject.lessonbuildertool.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Application Context provider
 * 
 * @author OpenCollab
 */
public class ApplicationContextProvider implements ApplicationContextAware {
    private static ApplicationContext context;
 
    /**
     * Get the current application context
     * 
     * @return 
     */
    public static ApplicationContext getApplicationContext() {
        return context;
    }
 
    @Override
    public void setApplicationContext(ApplicationContext ctx) {
        context = ctx;
    }
}
