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
import com.aggfi.digest.shared.FieldVerifier;
import com.google.appengine.api.datastore.Text;
import com.google.inject.Inject;
import com.vegalabs.general.server.command.Command;

public class AddAdSenseCode extends Command {
	  private static final Logger LOG = Logger.getLogger(AddAdSenseCode.class.getName());
	  private Util util = null;
	  private AdminConfigDao adminConfigDao;
	private ContributorDao contributorDao = null;

	  @Inject
		public AddAdSenseCode(Util util,ForumBotty robot, AdminConfigDao adminConfigDao, ContributorDao contributorDao) {
			this.util = util;
			this.adminConfigDao = adminConfigDao;
			this.contributorDao = contributorDao;
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
	    
	    
	    
	    String adSenseCode = this.getParam("adSenseCode");
	    FieldVerifier.verifyAdSenseCode(adSenseCode);
	    
	    String userName = this.getParam("userName");
	    String userThumbnailUrl = this.getParam("userThumbnailUrl");
	    
	    Contributor contributor = contributorDao.get(userId);
	    contributor.setFullName(userName);
	    contributor.setIconUrl(userThumbnailUrl);
	    
	    
	    if(isAdSenseUpdate4User){
	    	contributor.setGoogleAdsenseCode(new Text(adSenseCode));
	    	LOG.fine("Saved AdSense code for contributor: " + contributor.toString());
	    }else{
	    	AdminConfig adminConfig = adminConfigDao.getAdminConfig(projectId);
	    	adminConfig.setAdsense(new Text(adSenseCode));
	    	adminConfigDao.save(adminConfig);
	    	LOG.fine("Saved AdSense code for forum: " + adminConfig);
	    }
	    contributorDao.save(contributor);
		
	    JSONObject json = new JSONObject();
	    json.put("success", "true");
	    return json;
	  }
}
