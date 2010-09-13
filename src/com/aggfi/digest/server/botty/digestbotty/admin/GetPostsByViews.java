package com.aggfi.digest.server.botty.digestbotty.admin;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.vegalabs.general.server.command.Command;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.vegalabs.general.server.rpc.util.Util;
import com.aggfi.digest.server.botty.digestbotty.dao.TrackerEventDao;
import com.aggfi.digest.server.botty.digestbotty.model.TrackerEvent;
import com.aggfi.digest.server.botty.google.forumbotty.dao.ForumPostDao;
import com.aggfi.digest.server.botty.google.forumbotty.model.ForumPost;
import com.google.inject.Inject;

public class GetPostsByViews extends Command {
	Logger LOG = Logger.getLogger(GetPostsByViews.class.getName());
  private static final int ONE_DAY = 60 * 60 * 24 * 1000;
  
  private TrackerEventDao trackerEventDao = null;
  private Util util = null;
  private ForumPostDao forumPostDao = null;
  
  @Inject
  public GetPostsByViews(TrackerEventDao trackerEventDao, ForumPostDao forumPostDao, Util util) {
    this.trackerEventDao = trackerEventDao;
    this.util = util;
    this.forumPostDao = forumPostDao;
  }

  @Override
  public JSONObject execute() throws JSONException {    
	 LOG.info("GetPostsByViews: " + toString());
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
    
    
    List<TrackerEvent> entries = null;
    
      entries = trackerEventDao.getTrackerEventsFromDate (projectId, startDate);
      
      
     
      Map<String,Integer> uniqueWavesViews = new HashMap<String, Integer>();
      Map<String,TrackerEvent> trackerEventsMap = new HashMap<String, TrackerEvent>();
      int count = 0;
      for(TrackerEvent event : entries){
    	 
    	 int viewsCount = uniqueWavesViews.get(event.getWaveId()) != null ? uniqueWavesViews.get(event.getWaveId()) : 0;
    	 uniqueWavesViews.put(event.getWaveId(), viewsCount + 1);
    	 if(viewsCount == 0){
    		 trackerEventsMap.put(event.getWaveId(), event);
    	 }
      }
      
      ArrayList<JSONObject> jsonsList = new ArrayList<JSONObject>();
      Set<String> keys = trackerEventsMap.keySet();
      for(String key : keys){
    	  TrackerEvent trackerEvent = trackerEventsMap.get(key);
    	ForumPost post = null;
    	try{
    		post = forumPostDao.getForumPost(trackerEvent.getWaveId());
    	}catch(Exception e){
    		LOG.log(Level.SEVERE,"there's views for wave without ForumPost : " + trackerEvent.toString() ,e);
    	}
    	if(post == null){
    		LOG.log(Level.SEVERE,"no post for waveId: " + trackerEvent.getWaveId() + " from: " + trackerEvent.toString());
    		//if post created with new post gadget - but wasnt updated by - o forumPost exist for it (yet)
    		continue;
    	}
    	  JSONObject entry = new JSONObject();
    	    try {
    	      entry.put("title", post.getTitle());
    	      entry.put("count", uniqueWavesViews.get(key));
    	      entry.put("link", post.getId());
    	    } catch (JSONException e) {
    	    	LOG.log(Level.SEVERE,"",e);
    	    }
    	    jsonsList.add(count, entry);
    	    count++;
      }
      Collections.sort(jsonsList, new TrackerEventComparator());
      
      for(JSONObject jsonObj : jsonsList ){
    	  if(count == 20)
    		  break;
    	  jsonArray.put(jsonObj);
      }
      
   
      
  
    
    json.put("result", jsonArray);
    
    return json;       
  }
  
  private class TrackerEventComparator implements Comparator<JSONObject>{

	  @Override
	public  int compare(JSONObject o1, JSONObject o2) {
		try {
			return Integer.parseInt(o2.get("count").toString()) - Integer.parseInt(o1.get("count").toString());
		} catch (NumberFormatException e) {
			LOG.warning(e.getMessage());
		} catch (JSONException e) {
			LOG.warning(e.getMessage());
		}
		return 0;
	}
	  
  }
}
