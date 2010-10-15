package com.aggfi.digest.server.botty.digestbotty.admin;

import java.util.List;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import com.aggfi.digest.server.botty.digestbotty.dao.ExtDigestDao;
import com.aggfi.digest.server.botty.digestbotty.model.ExtDigest;
import com.vegalabs.general.server.rpc.util.Util;
import com.vegalabs.general.server.command.Command;
import com.google.inject.Inject;

public class GetDigestInfo extends Command {
	  private static final Logger LOG = Logger.getLogger(GetDigestInfo.class.getName());
	  private Util util = null;
	  private ExtDigestDao extDigestDao = null;
	  
	  @Inject
	  public GetDigestInfo(Util util, ExtDigestDao extDigestDao) {
		  this.util = util;
		  this.extDigestDao = extDigestDao;
	  }

	  @Override
	  public JSONObject execute() throws JSONException {
	    
	    String projectId = this.getParam("projectId");
	    if (util.isNullOrEmpty(projectId)) {
	      throw new IllegalArgumentException("Missing required param: projectId");
	    }
	    
	    List<ExtDigest> extList = extDigestDao.retrDigestsByProjectId(projectId);
	    ExtDigest extDigest = extList.get(0);
	    
	    String authorName = extDigest.getAuthor();
	    String forumName = extDigest.getName();
	    String forumDescription = extDigest.getDescription();
	    String installerThumbnailUrl = extDigest.getInstallerThumbnailUrl();
	    String forumSiteUrl = extDigest.getForumSiteUrl();
	    
		
	    JSONObject json = new JSONObject();
	    JSONObject result = new JSONObject();
	    result.put("authorName", authorName);
	    result.put("forumName", forumName);
	    result.put("forumDescription", forumDescription);
	    result.put("installerThumbnailUrl", installerThumbnailUrl);
	    result.put("forumSiteUrl", forumSiteUrl);
	    json.put("result", result);
	    return json;
	  }
}

