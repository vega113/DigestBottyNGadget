
package com.aggfi.digest.server.botty.digestbotty.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheManager;

import org.json.JSONException;
import org.json.JSONObject;
import com.aggfi.digest.server.botty.digestbotty.dao.TrackerEventDao;
import com.aggfi.digest.server.botty.digestbotty.model.TrackerEvent;
import com.vegalabs.general.server.rpc.util.Util;
import com.vegalabs.general.server.command.Command;
import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;
import com.google.inject.Inject;

public class GetViewsCountPerWave extends Command {
	public static final String VIEW_POST_EVENT_TYPE = "VIEW_POST";
	static Logger LOG  = Logger.getLogger(GetViewsCountPerWave.class.getName());
  
  private TrackerEventDao trackerEventDao = null;
  private Util util = null;
  
  
  static{
	  try {
	        
	        Map<String, Integer> props = new HashMap<String, Integer>();
	        props.put(GCacheFactory.EXPIRATION_DELTA, 300);
	        cache = CacheManager.getInstance().getCacheFactory().createCache(props);
	    } catch (CacheException e) {
	        LOG.log(Level.SEVERE,"cache init",e);
	    }
  }
  
  private static Cache cache;
  
  @Inject
  public GetViewsCountPerWave(TrackerEventDao trackerEventDao, Util util) {
    this.trackerEventDao = trackerEventDao;
    this.util = util;
  }

  @Override
  public JSONObject execute() throws JSONException {    
	  LOG.entering(this.getClass().getName(), "execute", toString());
	  LOG.info("execute" + ", " + toString());
	  String waveId = this.getParam("waveId");
	  if (util.isNullOrEmpty(waveId)) {
		  throw new IllegalArgumentException("Missing required param: waveId");
	  }    
	  JSONObject json = new JSONObject();
	  JSONObject result = new JSONObject();
	  Object o = cache.get(ReportPostView.class.getName() + waveId);
	  if(o != null){
		  Integer count = (Integer)o;
		 
		  result.put("count", count);
		  result.put("waveId", waveId);

		  LOG.info("GetViewsCountPerWave output: " + ", " + result.toString());
	  }else{
		  List<TrackerEvent> trackerEventsList = trackerEventDao.getTrackerEventsByWaveId(waveId);
		  result.put("count", trackerEventsList.size());
		  result.put("waveId", waveId);
		  LOG.info("GetViewsCountPerWave output: " + ", " + result.toString());
		  cache.put(GetViewsCountPerWave.class.getName() + waveId,trackerEventsList.size());
	  }
	  json.put("result", result);
	  return json;   

  }
}



