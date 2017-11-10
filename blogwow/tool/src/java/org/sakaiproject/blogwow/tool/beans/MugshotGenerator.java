package org.sakaiproject.blogwow.tool.beans;

import java.util.List;

/** Generates a random (but stable) mugshot for a user based on their id - 
 * for visual variety.
 * @author Antranig Basman (amb26@ponder.org.uk)
 *
 */

public class MugshotGenerator {
  private List<String> mugshotImages;
  public void setMugshotImages(List<String> images) {
      this.mugshotImages = images;
  }
  
  public String getMugshotUrl(String userid) {
      int mugshot = Math.abs(userid.hashCode()) % mugshotImages.size();
      return mugshotImages.get(mugshot);
  }
    
}
