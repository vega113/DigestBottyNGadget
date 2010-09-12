package com.aggfi.digest.server.botty.digestbotty.admin;


import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import com.aggfi.digest.server.botty.digestbotty.dao.AdEventDao;
import com.aggfi.digest.server.botty.digestbotty.model.AdEvent;
import com.vegalabs.general.server.rpc.util.Util;
import com.google.inject.Inject;
import com.vegalabs.general.server.command.Command;

public class ReportAdView extends Command {
	  private static final Logger LOG = Logger.getLogger(ReportAdView.class.getName());
	  private Util util = null;
	  private AdEventDao adEventDao;

	  @Inject
		public ReportAdView(Util util, AdEventDao adEventDao ) {
			this.util = util;
			this.adEventDao = adEventDao;
		}

	  @Override
	  public JSONObject execute() throws JSONException {
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
	    
	    String value = this.getParam("value");
	    
	    String eventType = this.getParam("type");
	    
	    
	    AdEvent adEvent = new AdEvent(userId, projectId, waveId, eventType);
	    adEvent.setEventValue(value);
	    
	    LOG.info("Tracker event: " + adEvent);
	    
	    adEventDao.save(adEvent);
	    
	    //retrieve map of users/views for this day
	    //retrieve list
	    //record total numbers of views for this forum into digest per day
	    
	    
	    
		
	    JSONObject json = new JSONObject();
	    json.put("success", "true");
	    return json;
	  }
}

