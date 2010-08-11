package com.aggfi.digest.server.botty.digestbotty.admin;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.aggfi.digest.server.botty.digestbotty.dao.BlipSubmitedDao;
import com.aggfi.digest.server.botty.digestbotty.dao.BlipSubmitedDaoImpl;
import com.aggfi.digest.server.botty.digestbotty.model.BlipSubmitted;
import com.aggfi.digest.server.botty.google.forumbotty.Util;
import com.aggfi.digest.server.botty.google.forumbotty.admin.Command;
import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;
import com.google.inject.Inject;

public class GetContributorsPerInfluence extends Command {
	private final Logger LOG = Logger.getLogger(GetContributorsPerInfluence.class.getName());
  private static final int ONE_DAY = 60 * 60 * 24 * 1000;
  
  private Cache cache;
  private BlipSubmitedDao blipSubmitedDao = null;
  private Util util = null;
  
  @Inject
  public GetContributorsPerInfluence(BlipSubmitedDao blipSubmitedDao, Util util) {
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
	  LOG.entering(GetContributorsPerInfluence.class.getName(), "execute", this.getParams());
    String projectId = this.getParam("projectId");
    if (util.isNullOrEmpty(projectId)) {
      throw new IllegalArgumentException("Missing required param: id");
    }   
    JSONObject json = new JSONObject();
    JSONArray jsonArray = new JSONArray();
    
    Object o = cache.get(projectId + GetContributorsPerInfluence.class.getName());
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
        List<BlipSubmitted> blipsList = blipSubmitedDao.getBlipsFromDate(projectId, startDate);
        LOG.info("after getBlipsFromDate, size:" + blipsList.size());
        
        Map<String,BlipSubmitted> blipContributionMap = new HashMap<String, BlipSubmitted>();
        Map<String,Double> contributorsInfluenceMap = new HashMap<String,Double>();
        Map<String,Double> blipsInfluenceMap = new HashMap<String,Double>();
        Set<String> blipsIdSet = new LinkedHashSet<String>();
        for(BlipSubmitted blipSubmitted  : blipsList){
        	blipContributionMap.put(blipSubmitted.getBlipId(), blipSubmitted);
        	contributorsInfluenceMap.put(blipSubmitted.getCreator(), 0.0);
        	blipsInfluenceMap.put(blipSubmitted.getBlipId(),  0.0);
        }
        
        for(BlipSubmitted blipSubmitted  : blipsList){
        	if(blipsIdSet.contains(blipSubmitted.getBlipId()) && blipSubmitted.getModifier().equals(blipSubmitted.getCreator())){
        		continue; //count every blip only once for creator
        	}else if(blipSubmitted.getModifier().equals(blipSubmitted.getCreator())){
        		blipsIdSet.add(blipSubmitted.getBlipId());
        	}
        	if(!blipSubmitted.getModifier().equals(blipSubmitted.getCreator())){ //if someone edited blip of creator - creator scores half a point
        		double influence = contributorsInfluenceMap.get(blipSubmitted.getCreator());
        		contributorsInfluenceMap.put(blipSubmitted.getCreator(), influence + 0.5);
        	}
        	String parentBlipId = blipSubmitted.getParentBlipId();
        	assignInfluencePoints(parentBlipId,blipContributionMap,contributorsInfluenceMap,blipsInfluenceMap, 1.0);
        }
        ArrayList<BlipDataSortHelper> blipDataSortHelperlist = new ArrayList<GetContributorsPerInfluence.BlipDataSortHelper>(contributorsInfluenceMap.size());
        for(String key : contributorsInfluenceMap.keySet()){
        	BlipDataSortHelper blipDataSortHelper = new BlipDataSortHelper(key);
        	blipDataSortHelper.setInfluence(contributorsInfluenceMap.get(key));
        	blipDataSortHelperlist.add(blipDataSortHelper);
        }
        Collections.sort(blipDataSortHelperlist);
        
        StringBuilder sb = new StringBuilder();
        int counter = 0;
        for(BlipDataSortHelper blipDataSortHelper : blipDataSortHelperlist){
        	if(counter == 20)
        		break;
        	JSONObject entry = new JSONObject();
            try {
            	sb.append("ParticipantId: " + blipDataSortHelper.getParticipantId() + ", Influence: " + blipDataSortHelper.getInfluence() + ".\n");
              entry.put("participant", blipDataSortHelper.getParticipantId());
              entry.put("influence", blipDataSortHelper.getInfluence());
            } catch (JSONException e) {
              LOG.log(Level.SEVERE, "", e);
            }
            jsonArray.put(counter,entry);
            counter++;
        }
        cache.put(projectId + GetContributorsPerInfluence.class.getName(),jsonArray.toString());
        LOG.info("after creating jsonArray, size: " + jsonArray.length() + "\n" + sb.toString());
    }
    
    json.put("result", jsonArray);
    LOG.exiting(GetContributorsPerInfluence.class.getName(), "execute", jsonArray.toString());
    return json;       
  }
  
  private void assignInfluencePoints(String parentBlipId,Map<String, BlipSubmitted> blipContributionMap,Map<String, Double> contributorsInfluenceMap,Map<String, Double> blipsInfluenceMap, double influencePerLevel) {
	  if(parentBlipId == null || "".equals(parentBlipId))
		  return;
	  BlipSubmitted parentBlipSubmitted = blipContributionMap.get(parentBlipId);
	  if(parentBlipSubmitted != null){
		  String creator = parentBlipSubmitted.getCreator();
		  double contributorInfluenceValue = contributorsInfluenceMap.get(creator);
		  contributorsInfluenceMap.put(creator, contributorInfluenceValue + influencePerLevel);
		  
		  double blipInfluenceValue = blipsInfluenceMap.get(parentBlipId);
		  blipsInfluenceMap.put(parentBlipId, blipInfluenceValue + influencePerLevel);
		  
		  assignInfluencePoints(parentBlipSubmitted.getParentBlipId(),blipContributionMap,contributorsInfluenceMap,blipsInfluenceMap, influencePerLevel/2);
	  }
  }

private class BlipDataSortHelper implements Comparable<BlipDataSortHelper>{
	  public BlipDataSortHelper(String participantId){
		  this.participantId = participantId;
		  influence = 0;
	  }
	  public double getInfluence() {
		return influence;
	}
	public void setInfluence(double influence) {
		this.influence = influence;
	}
	public String getParticipantId() {
		return participantId;
	}
	  String participantId = null;
	  double influence = 0;
	@Override
	public int compareTo(BlipDataSortHelper other) {
		return (int)Math.round((other.getInfluence() - influence)*1000) ;
	}
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
	  double influence = 0;
	@Override
	public int compareTo(BlipContributor other) {
		return other.getBlipsCount() - blipsCount ;
	}
}  
 
}
