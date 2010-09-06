package com.aggfi.digest.server.botty.digestbotty.admin;

import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import com.aggfi.digest.server.botty.digestbotty.dao.ContributorDao;
import com.aggfi.digest.server.botty.digestbotty.model.Contributor;
import com.aggfi.digest.server.botty.google.forumbotty.ForumBotty;
import com.vegalabs.general.server.rpc.util.Util;
import com.aggfi.digest.server.botty.google.forumbotty.dao.AdminConfigDao;
import com.aggfi.digest.server.botty.google.forumbotty.model.AdminConfig;
import com.vegalabs.general.server.command.Command;
import com.google.inject.Inject;

public class GetAdSenseCode extends Command {
	  private static final Logger LOG = Logger.getLogger(GetAdSenseCode.class.getName());
	  private Util util = null;
	private ContributorDao contributorDao = null;
	 private AdminConfigDao adminConfigDao;
	  @Inject
		public GetAdSenseCode(Util util,ForumBotty robot, AdminConfigDao adminConfigDao, ContributorDao contributorDao) {
			this.util = util;
			this.contributorDao = contributorDao;
			this.adminConfigDao = adminConfigDao;
		}

	  @Override
	  public JSONObject execute() throws JSONException {
	  String isAdSenseUpdate4UserStr = this.getParam("isAdSenseUpdate4User");
	    if (util.isNullOrEmpty(isAdSenseUpdate4UserStr)) {
	      throw new IllegalArgumentException("Missing required param: isAdSenseUpdate4User");
	    }
	    boolean isAdSenseUpdate4User = Boolean.parseBoolean(isAdSenseUpdate4UserStr);
	    
	    String projectId = this.getParam("projectId");
	    if (!isAdSenseUpdate4User && util.isNullOrEmpty(projectId)) {
	      throw new IllegalArgumentException("Missing required param: projectId");
	    }

	    String userId = this.getParam("userId");
	    if (util.isNullOrEmpty(userId)) {
	      throw new IllegalArgumentException("Missing required param: userId");
	    }
	    
	    
	    
	    
	   
	    
	    String adSenseCodeStr = null;
	    adSenseCodeStr = retrAdSenseCode(isAdSenseUpdate4User, projectId,userId,adminConfigDao,contributorDao);
	    
	    String adSenseCode4UserStr = "";
	    if(!isAdSenseUpdate4User){
	    	adSenseCode4UserStr = retrAdSenseCode(true, projectId,userId,adminConfigDao,contributorDao);
	    }
		
	    JSONObject json = new JSONObject();
	    JSONObject result = new JSONObject();
	    result.put("adSenseCode", adSenseCodeStr);
	    result.put("adSenseCode4User", adSenseCode4UserStr);
	    json.put("result", result);
	    return json;
	  }

	public static String retrAdSenseCode(boolean isAdSenseUpdate4User,
			String projectId, String userId, AdminConfigDao adminConfigDao, ContributorDao contributorDao) {
		String adSenseCodeStr;
		if(isAdSenseUpdate4User){
	    	 Contributor contributor = contributorDao.get(userId);
	    	 adSenseCodeStr = contributor.getGoogleAdsenseCode() != null ? contributor.getGoogleAdsenseCode().getValue() : "";
	    	LOG.fine("Loading AdSense code for contributor: " + contributor.toString());
	    }else{
	    	AdminConfig adminConfig = adminConfigDao.getAdminConfig(projectId);
	    	adSenseCodeStr = adminConfig.getAdsense() != null ? adminConfig.getAdsense().getValue() : "";
	    	LOG.fine("Loading AdSense code for digest: " + adminConfig);
	    }
		return adSenseCodeStr;
	}
}

