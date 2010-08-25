package com.aggfi.digest.server.botty.digestbotty.admin;


import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.aggfi.digest.server.botty.google.forumbotty.Util;
import com.aggfi.digest.server.botty.google.forumbotty.admin.Command;
import com.aggfi.digest.server.botty.google.forumbotty.dao.ForumPostDao;
import com.aggfi.digest.server.botty.google.forumbotty.model.ForumPost;
import com.google.inject.Inject;

public class GetPostByActivity extends Command {
	Logger LOG = Logger.getLogger(GetPostByActivity.class.getName());
  private static final int ONE_DAY = 60 * 60 * 24 * 1000;
  
  private ForumPostDao forumPostDao = null;
  private Util util = null;
  
  @Inject
  public GetPostByActivity(ForumPostDao forumPostDao, Util util) {
    this.forumPostDao = forumPostDao;
    this.util = util;
  }

  @Override
  public JSONObject execute() throws JSONException {    
    String projectId = this.getParam("projectId");
    if (util.isNullOrEmpty(projectId)) {
      throw new IllegalArgumentException("Missing required param: id");
    }    
    
    int durationDays = 14;    
    if (!util.isNullOrEmpty(this.getParam("days"))) {      
      durationDays = Integer.parseInt(this.getParam("days"));
    }        
    
    
    long targetDate = (new Date()).getTime(); // Today base on server clock
    if (!util.isNullOrEmpty(this.getParam("target"))) {      
      targetDate = Long.parseLong(this.getParam("target"));
    }       
    
    JSONObject json = new JSONObject();
    JSONArray jsonArray = new JSONArray();
        
    Date target = new Date(targetDate);
    target.setHours(0);
    target.setMinutes(0);
    target.setSeconds(0);
    
    Date startDate = new Date(target.getTime() - (ONE_DAY * durationDays));
    
    
    List<ForumPost> entries = null;
    
      entries = forumPostDao.getForumPostsFromDate(projectId, startDate);
      
      Collections.sort(entries, new ForumPostComparator());
      int count = 0;
      for(ForumPost post : entries){
    	  if(count == 20)
    		  break;
    	  JSONObject entry = new JSONObject();
    	    try {
    	      entry.put("title", post.getTitle());
    	      entry.put("count", post.getBlipCount());
    	      entry.put("link", post.getId());
    	    } catch (JSONException e) {
    	    	LOG.log(Level.SEVERE,"",e);
    	    }
    	    jsonArray.put(entry);
    	    count++;
      }
   
  
    
    json.put("result", jsonArray);
    
    return json;       
  }
  
  private class ForumPostComparator implements Comparator<ForumPost>{

	  @Override
	public  int compare(ForumPost o1, ForumPost o2) {
		return o2.getBlipCount() - o1.getBlipCount();
	}
	  
  }
}
