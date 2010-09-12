package com.aggfi.digest.server.botty.digestbotty.admin;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.aggfi.digest.server.botty.digestbotty.dao.TrackerEventDao;
import com.aggfi.digest.server.botty.digestbotty.model.TrackerEvent;
import com.vegalabs.general.server.rpc.util.Util;
import com.vegalabs.general.server.command.Command;
import com.google.inject.Inject;

public class GetViewsCount extends Command {
	public static final String VIEW_POST_EVENT_TYPE = "VIEW_POST";
	Logger LOG  = Logger.getLogger(GetViewsCount.class.getName());
  private static final int ONE_DAY = 60 * 60 * 24 * 1000;
  
  private TrackerEventDao trackerEventDao = null;
  private Util util = null;
  
  @Inject
  public GetViewsCount(TrackerEventDao trackerEventDao, Util util) {
    this.trackerEventDao = trackerEventDao;
    this.util = util;
  }

  @Override
  public JSONObject execute() throws JSONException {    
	  LOG.entering(this.getClass().getName(), "execute", toString());
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
      List<TrackerEvent> trackerEventsList = trackerEventDao.getEventsDuringDate(projectId, startDate, VIEW_POST_EVENT_TYPE);
      Set<String> uniqueViewersSet = new LinkedHashSet<String>();
      for(TrackerEvent trackerEvent  : trackerEventsList){
    	  uniqueViewersSet.add(trackerEvent.getSourceId());
      }
      JSONObject entry = new JSONObject();
      try {
        entry.put("date", startDate);
        entry.put("count", trackerEventsList.size());
        entry.put("uniqueCount", uniqueViewersSet.size());
      } catch (JSONException e) {
        LOG.severe(e.getMessage());
      }
      jsonArray.put(entry);
      startDate = new Date(startDate.getTime() + ONE_DAY);
    }
    
    json.put("result", jsonArray);
    
    return json;       
  }
}


