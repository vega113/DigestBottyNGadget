package com.aggfi.digest.server.botty.digestbotty.admin;


import java.util.ArrayList;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.vegalabs.general.server.command.Command;
import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.aggfi.digest.server.botty.digestbotty.dao.BlipSubmitedDao;
import com.aggfi.digest.server.botty.digestbotty.model.BlipSubmitted;
import com.vegalabs.general.server.rpc.util.Util;
import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;
import com.google.inject.Inject;

public class GetBlipsPerContributor extends Command {
	private final Logger LOG = Logger.getLogger(GetBlipsPerContributor.class.getName());
  private static final int ONE_DAY = 60 * 60 * 24 * 1000;
  
  private Cache cache;
  private BlipSubmitedDao blipSubmitedDao = null;
  private Util util = null;
  
  @Inject
  public GetBlipsPerContributor(BlipSubmitedDao blipSubmitedDao, Util util) {
    this.blipSubmitedDao = blipSubmitedDao;
    this.util = util;
    try {
    	Map<String, Integer> props = new HashMap<String, Integer>();
        props.put(GCacheFactory.EXPIRATION_DELTA, 30);
        cache = CacheManager.getInstance().getCacheFactory().createCache(props);
    } catch (CacheException e) {
        LOG.log(Level.SEVERE,"cache init",e);
    }
  }

  @Override
  public JSONObject execute() throws JSONException {  
	  LOG.entering(GetBlipsPerContributor.class.getName(), "execute", this.getParams());
    String projectId = this.getParam("projectId");
    if (util.isNullOrEmpty(projectId)) {
      throw new IllegalArgumentException("Missing required param: id");
    }   
    JSONObject json = new JSONObject();
    JSONArray jsonArray = new JSONArray();
    
    Object o = cache.get(projectId + GetBlipsPerContributor.class.getName());
    if(o != null){
    	jsonArray = (JSONArray) new JSONArray((String)o);
    	 LOG.info("taking result form cache");
    }else{
    	int durationDays = 14;    
        if (!util.isNullOrEmpty(this.getParam("days"))) {      
          durationDays = Integer.parseInt(this.getParam("days"));
        }        
        
        long targetDate = (new Date()).getTime(); // Today base on server clock
        if (!util.isNullOrEmpty(this.getParam("target"))) {      
          targetDate = Long.parseLong(this.getParam("target"));
        }       
        
        
            
        Date target = new Date(targetDate);
        target.setHours(0);
        target.setMinutes(0);
        target.setSeconds(0);
        
        Date startDate = new Date(target.getTime() - (ONE_DAY * durationDays));
        
        LOG.info("before getBlipsFromDate");
        java.util.List<BlipSubmitted> blipsList = blipSubmitedDao.getBlipsFromDate(projectId, startDate);
        LOG.info("after getBlipsFromDate, size:" + blipsList.size());
        Map<String,BlipContributor> blipContributionMap = new HashMap<String, GetBlipsPerContributor.BlipContributor>();
        Set<String> blipsIdSet = new LinkedHashSet<String>();
        LOG.info("before creating blipContributionMap");
        for(BlipSubmitted blipSubmitted  : blipsList){
        	if(blipsIdSet.contains(blipSubmitted.getBlipId()) && blipSubmitted.getModifier().equals(blipSubmitted.getCreator())){
        		continue; //count every blip only once
        	}else if(blipSubmitted.getModifier().equals(blipSubmitted.getCreator())){
        		blipsIdSet.add(blipSubmitted.getBlipId());
        	}
        	BlipContributor blipContributor = blipContributionMap.get(blipSubmitted.getModifier());
        	if(blipContributor == null){
        		blipContributor =  new BlipContributor(blipSubmitted.getModifier());
        		blipContributionMap.put(blipSubmitted.getModifier(), blipContributor);
        	}
        	blipContributor.incCount();
        }
        LOG.info("after creating blipContributionMap, size: " + blipContributionMap.size());
        
        ArrayList<BlipContributor> sortedBlipContributorsList = new ArrayList<BlipContributor>();
        for(String key :blipContributionMap.keySet()){
        	BlipContributor blipContributor = blipContributionMap.get(key);
        	sortedBlipContributorsList.add(blipContributor);
        }
        LOG.info("before sortedBlipContributorsList");
        Collections.sort(sortedBlipContributorsList);
        LOG.info("after sortedBlipContributorsList");
        
        LOG.info("before creating jsonArray");
        int counter = 0;
        for(BlipContributor blipContributor : sortedBlipContributorsList){
        	if(counter == 20)
        		break;
        	
        	JSONObject entry = new JSONObject();
            try {
              entry.put("participant", blipContributor.getParticipantId());
              entry.put("count", blipContributor.getBlipsCount());
            } catch (JSONException e) {
              LOG.log(Level.SEVERE, "", e);
            }
            jsonArray.put(counter,entry);
            counter++;
        }
        cache.put(projectId + GetBlipsPerContributor.class.getName(),jsonArray.toString());
        LOG.info("after creating jsonArray, size: " + jsonArray.length());
    }
    
    json.put("result", jsonArray);
    LOG.exiting(GetBlipsPerContributor.class.getName(), "execute", jsonArray.toString());
    return json;       
  }
  
  private class BlipContributor implements Comparable<BlipContributor>{
	  public BlipContributor(String participantId){
		  this.participantId = participantId;
		  blipsCount = 0;
	  }
	  public int incCount(){
		  blipsCount++;
		  return blipsCount;
	  }
	  public String getParticipantId() {
		return participantId;
	}
	public int getBlipsCount(){
		  return blipsCount;
	  }
	  String participantId = null;
	  int blipsCount = 0;
	@Override
	public int compareTo(BlipContributor other) {
		return other.getBlipsCount() - blipsCount ;
	}
  }  
 
}


