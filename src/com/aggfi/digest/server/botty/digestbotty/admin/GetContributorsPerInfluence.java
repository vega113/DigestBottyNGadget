package com.aggfi.digest.server.botty.digestbotty.admin;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import umontreal.iro.lecuyer.probdist.NormalDist;

import com.aggfi.digest.server.botty.digestbotty.dao.BlipSubmitedDao;
import com.aggfi.digest.server.botty.digestbotty.dao.ComplReplyProbDao;
import com.aggfi.digest.server.botty.digestbotty.dao.InfluenceDao;
import com.aggfi.digest.server.botty.digestbotty.model.BlipSubmitted;
import com.aggfi.digest.server.botty.digestbotty.model.ComplReplyProb;
import com.aggfi.digest.server.botty.digestbotty.model.Influence;
import com.aggfi.digest.server.botty.digestbotty.utils.InfluenceUtils;
import com.aggfi.digest.server.botty.google.forumbotty.Util;
import com.aggfi.digest.server.botty.google.forumbotty.admin.Command;
import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;
import com.google.inject.Inject;

public class GetContributorsPerInfluence extends Command {
	private static final String COMPLIMENTARY_REPLY_PROB_PER_FORUM_FOR_DAY = "calcComplimentaryReplyProb";
	private static final String INFLUENCE_PER_FORUM_PER_DAY = "INFLUENCE_PER_FORUM_PER_DAY";
	private final static Logger LOG = Logger.getLogger(GetContributorsPerInfluence.class.getName());
  private static final int ONE_DAY = 60 * 60 * 24 * 1000;
  
  static{
	  try {
	        Map<String, Integer> props1 = new HashMap<String, Integer>();
	        props1.put(GCacheFactory.EXPIRATION_DELTA, 60*60);
	        cacheDailyInfluence = CacheManager.getInstance().getCacheFactory().createCache(props1);
	        
	        Map<String, Integer> props = new HashMap<String, Integer>();
	        props.put(GCacheFactory.EXPIRATION_DELTA, 60);
	        cache = CacheManager.getInstance().getCacheFactory().createCache(props);
	    } catch (CacheException e) {
	        LOG.log(Level.SEVERE,"cache init",e);
	    }
  }
  
  private static Cache cache;
  private static Cache cacheDailyInfluence;
  private BlipSubmitedDao blipSubmitedDao = null;
  private ComplReplyProbDao complReplyProbDao;
  private InfluenceDao influenceDao;
  private Util util = null;
  
  @Inject
  public GetContributorsPerInfluence(BlipSubmitedDao blipSubmitedDao,InfluenceDao influenceDao, ComplReplyProbDao complReplyProbDao, Util util) {
    this.blipSubmitedDao = blipSubmitedDao;
    this.influenceDao = influenceDao;
    this.complReplyProbDao = complReplyProbDao;
    this.util = util;
  }
  
  Map<String,BlipSubmitted> blipContributionMap = new HashMap<String, BlipSubmitted>();
  Map<String,Double> contributorsInfluenceMap = new HashMap<String,Double>();
  Map<String,Double> blipsInfluenceMap = new HashMap<String,Double>();
  
  

  @SuppressWarnings("unchecked")
@Override
  public JSONObject execute() throws JSONException {  
	  LOG.entering(GetContributorsPerInfluence.class.getName(), "execute", this.getParams());
	  	//clear just to be sure no junk in there from previous invocation
		blipContributionMap.clear();
		contributorsInfluenceMap.clear();
		blipsInfluenceMap.clear();
 
    String projectId = this.getParam("projectId");
    if (util.isNullOrEmpty(projectId)) {
      throw new IllegalArgumentException("Missing required param: id");
    }   
    JSONObject json = new JSONObject();
    JSONArray jsonArray = new JSONArray();
    
    Object o = cache.get(projectId + GetContributorsPerInfluence.class.getName());
    if(false && o != null){
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
        
        Date todayDate = new Date();
        todayDate.setHours(0);
        todayDate.setMinutes(0);
        todayDate.setSeconds(0);
        
        
       //go over durationDays and calculate influence per this day
        Map<String,Double> influencePerForum4AllPeriodMap = new HashMap<String,Double>();
        for(int i=durationDays; i>=0; i--){ //for each day - get influence form cache - or calculate
        	 Date startDate = new Date(target.getTime() - (ONE_DAY * i));
        	 Influence inf = null;
        	 inf = (Influence)cacheDailyInfluence.get(createInfPerFormPerDayHash(projectId, startDate, InfluenceUtils.getSdf()));//try to get it from cache
        	 Map<String,Double> influencePerForum4TargetDayMap = inf == null ? null : inf.getInfluenceMap();
        	 
        	 if( influencePerForum4TargetDayMap == null && !InfluenceUtils.getSdf().format(startDate).equals(todayDate) ){ //not found in cache - try to get from DB
        		 Influence influence = influenceDao.getInfluence(projectId, startDate);
        		 if(influence != null){
        			 LOG.fine("Taking from DB influence map for " + projectId + " at " + startDate.getTime());
        			 influencePerForum4TargetDayMap = influence.getInfluenceMap();
        			 cacheDailyInfluence.put(createInfPerFormPerDayHash(projectId, startDate, InfluenceUtils.getSdf()), influence);
        		 }
        	 }else{
        		 LOG.fine("Taking from Cache influence map for " + projectId + " at " + startDate.getTime());
        	 }
        	 if( influencePerForum4TargetDayMap == null){
        		 LOG.fine("Calculating influence map for " + projectId + " at " + startDate.getTime());
        		 Map<String,Double> contributorsReplyComplementaryProbMap  = calcComplimentaryReplyProb(startDate,projectId);

        		 influencePerForum4TargetDayMap = new HashMap<String, Double>();
        		 List<BlipSubmitted> blipsList = blipSubmitedDao.getBlipsDuringDate(projectId, startDate);
        		 influencePerForum4TargetDayMap = calcInfluenceFromBlips(blipsList,contributorsReplyComplementaryProbMap);//calculate
        		 Influence infNew = influenceDao.save(new Influence(projectId, startDate, influencePerForum4TargetDayMap));
        		 LOG.info(startDate.toString() + ": " + createInfPerFormPerDayHash(projectId, startDate, InfluenceUtils.getSdf()));
        		 cacheDailyInfluence.put(createInfPerFormPerDayHash(projectId, startDate, InfluenceUtils.getSdf()), infNew);
        		 LOG.finest(infNew.toString());

        	 }
        	 //update influence for all period with daily values
        	 for(String key : influencePerForum4TargetDayMap.keySet()){
    			 Double contributorInfluence4TargetDay = influencePerForum4TargetDayMap.get(key);
    			 Double contributorInfluence4AllPeriod = influencePerForum4AllPeriodMap.get(key);
    			 
    			 if(contributorInfluence4AllPeriod == null){
    				 influencePerForum4AllPeriodMap.put(key,contributorInfluence4TargetDay);
    			 }else{
    				 contributorInfluence4AllPeriod = contributorInfluence4AllPeriod + contributorInfluence4TargetDay;
    				 influencePerForum4AllPeriodMap.put(key, contributorInfluence4AllPeriod);
    			 }
    		 }
        	
             
        }
        double influenceSum = 0;
        ArrayList<BlipDataSortHelper> blipDataSortHelperlist = new ArrayList<BlipDataSortHelper>();
        for(String key : influencePerForum4AllPeriodMap.keySet()){
        	Double blipDataSortHelper4AllPeriod = influencePerForum4AllPeriodMap.get(key);
        	blipDataSortHelperlist.add(new BlipDataSortHelper(key,blipDataSortHelper4AllPeriod));
        	influenceSum += blipDataSortHelper4AllPeriod;
        }
    	Collections.sort(blipDataSortHelperlist);
    	
    	
        StringBuilder sb = new StringBuilder();
        int counter = 0;
        for(BlipDataSortHelper blipDataSortHelper : blipDataSortHelperlist){
        	if(counter == 20)
        		break;
        	JSONObject entry = new JSONObject();
            try {
            	double influence = blipDataSortHelper.getInfluence()/influenceSum*1000;
            	sb.append("ParticipantId: " + blipDataSortHelper.getParticipantId() + ", Influence: " + influence + ".\n");
              entry.put("participant", blipDataSortHelper.getParticipantId());
              entry.put("influence", influence);
            } catch (JSONException e) {
              LOG.log(Level.SEVERE, "", e);
            }
            jsonArray.put(counter,entry);
            counter++;
        }
        cache.put(projectId + GetContributorsPerInfluence.class.getName(),jsonArray.toString());
        LOG.finer("Influence table for period from: " + target.toString() + " and " + durationDays + " days "  +"\n" + sb.toString());
    }
    
    json.put("result", jsonArray);
    LOG.exiting(GetContributorsPerInfluence.class.getName(), "execute", jsonArray.toString());
    return json;       
  }

protected String createInfPerFormPerDayHash(String projectId, Date startDate,SimpleDateFormat sdf) {
	return INFLUENCE_PER_FORUM_PER_DAY + projectId + sdf.format(startDate.getTime());
}

protected Map<String,Double> calcInfluenceFromBlips(List<BlipSubmitted> blipsList, Map<String,Double> contributorsReplyComplementaryProbMap) {
	//these maps are fields - so to avoid passing them around in recursion
	for(BlipSubmitted blipSubmitted  : blipsList){
		blipContributionMap.put(blipSubmitted.getBlipId(), blipSubmitted);
		contributorsInfluenceMap.put(blipSubmitted.getCreator(), 0.0);
		blipsInfluenceMap.put(blipSubmitted.getBlipId(),  0.0);
	}

	Set<String> blipsIdSet = new LinkedHashSet<String>();
	for(BlipSubmitted blipSubmitted  : blipsList){
		if( blipsIdSet.contains(blipSubmitted.getBlipId() + blipSubmitted.getModifier()) ){
			continue; //count every blip only once for creator and only once per each modifier
		}else {
			blipsIdSet.add(blipSubmitted.getBlipId() + blipSubmitted.getModifier());
		}
		if(!blipSubmitted.getModifier().equals(blipSubmitted.getCreator())){ //if someone edited blip of creator - creator scores 
			double influence = contributorsInfluenceMap.get(blipSubmitted.getCreator());
			Double complementaryReplyProbOfContributor = contributorsReplyComplementaryProbMap.get(blipSubmitted.getCreator());
			complementaryReplyProbOfContributor = complementaryReplyProbOfContributor == null ? 0.5 : complementaryReplyProbOfContributor;
			contributorsInfluenceMap.put(blipSubmitted.getCreator(), influence + 0.25*(0.5 + complementaryReplyProbOfContributor.doubleValue())); 
		}
		String childBlipId = blipSubmitted.getBlipId();
		String parentBlipId = blipSubmitted.getParentBlipId();
		assignInfluencePoints(contributorsReplyComplementaryProbMap,childBlipId,parentBlipId, 0.1);

	}
	return contributorsInfluenceMap;
}

@SuppressWarnings("unchecked")
public  Map<String,Double> calcComplimentaryReplyProb(Date forDate, String projectId) {
	Object o = cacheDailyInfluence.get(COMPLIMENTARY_REPLY_PROB_PER_FORUM_FOR_DAY + forDate.getTime() + projectId );
	if(o != null){
		LOG.fine("Taking from cache: " + "calcComplimentaryReplyProb: " + forDate.getTime() + " : " + projectId);
		return (Map<String,Double>)o;
	}else{
		ComplReplyProb complReplyProb = complReplyProbDao.getComplReplyProb(projectId, forDate);
		if(complReplyProb != null){
			LOG.fine("Taking from DB: " + "COMPLIMENTARY_REPLY_PROB_PER_FORUM_FOR_DAY: " + forDate.getTime() + " : " + projectId);
			Map<String,Double> complProbMap = complReplyProb.getComplProbMap();
			cacheDailyInfluence.put(COMPLIMENTARY_REPLY_PROB_PER_FORUM_FOR_DAY + forDate.getTime() + projectId, complProbMap);
			return complProbMap;
		}
	}
	//TODO should be done in separate thread - return empty map - can't wait.
	LOG.fine("Not found -  calculating: " + "COMPLIMENTARY_REPLY_PROB_PER_FORUM_FOR_DAY: " + forDate.getTime() + " : " + projectId);
	Date fromDateRetrHist = new Date(forDate.getTime() - (ONE_DAY * 14));// take user participation history from 14 days
	List<BlipSubmitted> blipsList = null;
	blipsList = blipSubmitedDao.getBlipsDuringPeriod(projectId, fromDateRetrHist,forDate);
	Map<String,Double> contributorsFreqMap = new HashMap<String, Double>();
	  Map<String,Double> contributorsReplyComplementaryProbMap = new HashMap<String, Double>();
	for(BlipSubmitted blipSubmitted  : blipsList){
		if(contributorsFreqMap.get(blipSubmitted.getCreator()) == null){
			contributorsFreqMap.put(blipSubmitted.getCreator(), 1.0);
		}else{
			contributorsFreqMap.put(blipSubmitted.getCreator(), contributorsFreqMap.get(blipSubmitted.getCreator()) + 1.0);
		}
	}
	
	int i = 0;
	double[] observations = new double[contributorsFreqMap.size()];
	for(String key : contributorsFreqMap.keySet()){
		observations[i] = contributorsFreqMap.get(key);
		i++;
	}
	NormalDist nd = null;
	
	try{
		nd = NormalDist.getInstanceFromMLE(observations,observations.length);
		LOG.fine(forDate.toString() +  ". NormalDist -  mu: " + nd.getMu() + ", sigma: " + nd.getSigma() + ", n=" + observations.length);
		for(String key : contributorsFreqMap.keySet()){
			double freq = contributorsFreqMap.get(key);
	    	double complementaryReplyProb = nd.barF(freq);
	    	contributorsReplyComplementaryProbMap.put(key, complementaryReplyProb);
	    }
	}catch (Exception e) {
		LOG.warning(e.getMessage());
	}
	Date todayDate = new Date();
    todayDate.setHours(0);
    todayDate.setMinutes(0);
    todayDate.setSeconds(0);
    if(!todayDate.equals(forDate)){
    	complReplyProbDao.save(new ComplReplyProb(projectId, forDate, contributorsReplyComplementaryProbMap));
    }
	cacheDailyInfluence.put(COMPLIMENTARY_REPLY_PROB_PER_FORUM_FOR_DAY + forDate.getTime() + projectId, contributorsReplyComplementaryProbMap);
	return contributorsReplyComplementaryProbMap;
}

  
  private void assignInfluencePoints(Map<String,Double> contributorsReplyComplementaryProbMap, String childBlipId,String parentBlipId, double influencePerLevel) {
	  if(parentBlipId == null || "".equals(parentBlipId))
		  return;
	  BlipSubmitted childBlipSubmitted = blipContributionMap.get(childBlipId);
	  BlipSubmitted parentBlipSubmitted = blipContributionMap.get(parentBlipId);
	  if(parentBlipSubmitted != null && !childBlipSubmitted.getCreator().equals(parentBlipSubmitted.getCreator())){
		  String creator = parentBlipSubmitted.getCreator();
		  double contributorInfluenceValue = contributorsInfluenceMap.get(creator);
		  contributorsInfluenceMap.put(creator, contributorInfluenceValue + influencePerLevel);
		  
		  double blipInfluenceValue = blipsInfluenceMap.get(parentBlipId);
		  Double complementaryReplyProbOfContributor = contributorsReplyComplementaryProbMap.get(creator);
		  if(complementaryReplyProbOfContributor == null){
			  LOG.log(Level.FINER, "no reply prob for: " +creator );
		  }
		  complementaryReplyProbOfContributor = complementaryReplyProbOfContributor == null ? 0.5 : complementaryReplyProbOfContributor;
		  blipsInfluenceMap.put(parentBlipId, blipInfluenceValue + influencePerLevel*(0.5 + complementaryReplyProbOfContributor.doubleValue()));//if contributor is more active than average - reduce from influence
		  
		  assignInfluencePoints(contributorsReplyComplementaryProbMap,parentBlipSubmitted.getBlipId(), parentBlipSubmitted.getParentBlipId(), influencePerLevel*0.5);
	  }
  }

 
}
