/**
 * $Id$
 * $URL$
 * ErrorHandler.java - blog-wow - Aug 1, 2008 12:20:19 PM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Aaron Zeckoski
 * Licensed under the Apache License, Version 2.0
 * 
 * A copy of the Apache License has been included in this 
 * distribution and is available at: http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Aaron Zeckoski (azeckoski @ gmail.com) (aaronz @ vt.edu) (aaron @ caret.cam.ac.uk)
 */

package org.sakaiproject.blogwow.tool.beans;

import uk.org.ponder.messageutil.TargettedMessage;
import uk.org.ponder.messageutil.TargettedMessageList;
import uk.org.ponder.rsf.state.support.ErrorStateManager;
import uk.org.ponder.util.RunnableInvoker;
import uk.org.ponder.util.UniversalRuntimeException;


/**
 * Allows all errors to pass through to the outside portal
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class ErrorHandler implements RunnableInvoker {

   private ErrorStateManager errorStateManager;
   public void setErrorStateManager(ErrorStateManager errorStateManager) {
      this.errorStateManager = errorStateManager;
   }

   /* (non-Javadoc)
    * @see uk.org.ponder.util.RunnableInvoker#invokeRunnable(java.lang.Runnable)
    */
   public void invokeRunnable(Runnable torun) {
      TargettedMessageList tml = errorStateManager.getTargettedMessageList();
      for (int i = 0; i < tml.size(); ++ i) {
         TargettedMessage message = tml.messageAt(i);
         if (message.exception != null) { 
            throw UniversalRuntimeException.accumulate(message.exception);
         }
      }
      torun.run();
   }

}
