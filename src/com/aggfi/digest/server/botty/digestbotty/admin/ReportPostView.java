package com.aggfi.digest.server.botty.digestbotty.admin;


import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheManager;

import org.json.JSONException;
import org.json.JSONObject;

import com.aggfi.digest.server.botty.digestbotty.dao.ExtDigestDao;
import com.aggfi.digest.server.botty.digestbotty.dao.ExtDigestDaoImpl;
import com.aggfi.digest.server.botty.digestbotty.dao.TrackerEventDao;
import com.aggfi.digest.server.botty.digestbotty.model.ExtDigest;
import com.aggfi.digest.server.botty.digestbotty.model.TrackerEvent;
import com.aggfi.digest.server.botty.google.forumbotty.dao.ForumPostDao;
import com.aggfi.digest.server.botty.google.forumbotty.model.ForumPost;
import com.vegalabs.general.server.rpc.util.Util;
import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;
import com.google.inject.Inject;
import com.vegalabs.general.server.command.Command;

public class ReportPostView extends Command {
	  private static final Logger LOG = Logger.getLogger(ReportPostView.class.getName());
	  private Util util = null;
	  private TrackerEventDao trackerEventDao;
	  private ForumPostDao forumPostDao;
	  private ExtDigestDao  extDigestDao;
	  
	  private static Cache cache;
	  static{
		  try {
		        
		        Map<String, Integer> props = new HashMap<String, Integer>();
		        props.put(GCacheFactory.EXPIRATION_DELTA, 300);
		        cache = CacheManager.getInstance().getCacheFactory().createCache(props);
		    } catch (CacheException e) {
		        LOG.log(Level.SEVERE,"cache init",e);
		    }
	  }
	  
	 

	  @Inject
		public ReportPostView(Util util, TrackerEventDao trackerEventDao, ForumPostDao forumPostDao, ExtDigestDao  extDigestDao) {
			this.util = util;
			this.trackerEventDao = trackerEventDao;
			this.forumPostDao = forumPostDao;
			this.extDigestDao = extDigestDao;
		}

	  @Override
	  public JSONObject execute() throws JSONException {
		  LOG.entering(this.getClass().getName(), "execute", toString());
	    String projectId = this.getParam("projectId");
//	    if (util.isNullOrEmpty(projectId)) {
//	      throw new IllegalArgumentException("Missing required param: projectId");
//	    }
	    
	    
	    String userId = this.getParam("userId");
	    if (util.isNullOrEmpty(userId)) {
	      throw new IllegalArgumentException("Missing required param: userId");
	    }
	    String waveId = this.getParam("waveId");
	    if (util.isNullOrEmpty(waveId)) {
	      throw new IllegalArgumentException("Missing required param: waveId");
	    }
	    
	    if("none".equals(projectId) ||  util.isNullOrEmpty(projectId)){
	    	ForumPost forumPost = null;
	    	try{
	    		forumPost = forumPostDao.getForumPost(waveId);
	    	}catch(Exception e){
	    		LOG.warning("Cannot find wave with id: " + waveId + ", " + e.getMessage());
	    	}
	    	if(forumPost != null){
	    		projectId = forumPost.getProjectId();
	    	}else{
	    		ExtDigest extDigest = null;
	    		try{
	    			extDigest = extDigestDao.retrDigestById(waveId);
	    		}catch(Exception e){
	    			if(extDigest != null){
	    				projectId = extDigest.getProjectId();
	    			}
	    		}
	    	}
	    	
	    }
	    
	    String value = this.getParam("value");
	    
	    String eventType = this.getParam("type");
	    
	    
	    TrackerEvent trackerEvent = new TrackerEvent(userId, projectId, waveId, eventType);
	    trackerEvent.setEventValue(value);
	    
	    LOG.fine("Tracker event: " + trackerEvent);
	    
	    trackerEventDao.save(trackerEvent);
	    Integer count = null;
	    Object o = cache.get(ReportPostView.class.getName() + waveId);
	    if(o != null){
	    	count = (Integer)o + 1;
	    	 cache.put(ReportPostView.class.getName() + waveId, count);
	    }
	    
	   
	    
	    //retrieve map of users/views for this day
	    //retrieve list
	    //record total numbers of views for this forum into digest per day
	    
	    
	    
		
	    JSONObject json = new JSONObject();
	    json.put("success", "true");
	    return json;
	  }
}

