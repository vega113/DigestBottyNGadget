package com.aggfi.digest.server.botty.google.forumbotty.admin;

import java.util.Date;
import com.vegalabs.general.server.command.Command;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vegalabs.general.server.rpc.util.Util;
import com.aggfi.digest.server.botty.google.forumbotty.dao.ForumPostDao;
import com.google.inject.Inject;

public class GetPostCounts extends Command {
  private static final int ONE_DAY = 60 * 60 * 24 * 1000;
  
  private ForumPostDao forumPostDao = null;
  private Util util = null;
  
  @Inject
  public GetPostCounts(ForumPostDao forumPostDao, Util util) {
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
    
    for (int i = 0; i < durationDays+1; i++) {      
      int count = forumPostDao.getPostCount(projectId, startDate);
      JSONObject entry = new JSONObject();
      try {
        entry.put("date", startDate);
        entry.put("count", count);
      } catch (JSONException e) {
        e.printStackTrace();
      }
      jsonArray.put(entry);
      startDate = new Date(startDate.getTime() + ONE_DAY);
    }
    
    json.put("result", jsonArray);
    
    return json;       
  }
}
