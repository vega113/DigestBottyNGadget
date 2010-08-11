package com.aggfi.digest.server.botty.digestbotty.admin;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.aggfi.digest.server.botty.digestbotty.dao.BlipSubmitedDao;
import com.aggfi.digest.server.botty.digestbotty.model.BlipSubmitted;
import com.aggfi.digest.server.botty.google.forumbotty.Util;
import com.aggfi.digest.server.botty.google.forumbotty.admin.Command;
import com.google.inject.Inject;

public class GetBlipsCount extends Command {
  private static final int ONE_DAY = 60 * 60 * 24 * 1000;
  
  private BlipSubmitedDao blipSubmitedDao = null;
  private Util util = null;
  
  @Inject
  public GetBlipsCount(BlipSubmitedDao blipSubmitedDao, Util util) {
    this.blipSubmitedDao = blipSubmitedDao;
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
      List<BlipSubmitted> blipsList = blipSubmitedDao.getBlipsDuringDate(projectId, startDate);
      Set<String> blipsIdSet = new LinkedHashSet<String>();
      int count = 0;
      for(BlipSubmitted blipSubmitted  : blipsList){
      	if(blipsIdSet.contains(blipSubmitted.getBlipId()) && blipSubmitted.getModifier().equals(blipSubmitted.getCreator())){
      		continue; //count every blip only once for creator
      	}else if(blipSubmitted.getModifier().equals(blipSubmitted.getCreator())){
      		blipsIdSet.add(blipSubmitted.getBlipId());
      	}
      	count++;
      }
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

