package com.aggfi.digest.server.botty.digestbotty.admin;


import java.util.logging.Logger;
import com.vegalabs.general.server.command.Command;
import org.json.JSONException;
import org.json.JSONObject;
import com.vegalabs.general.server.rpc.util.Util;
import com.aggfi.digest.server.botty.google.forumbotty.dao.AdminConfigDao;
import com.aggfi.digest.server.botty.google.forumbotty.model.AdminConfig;
import com.google.inject.Inject;

public class UpdateAtomFeedPublic extends Command {
  private static final Logger LOG = Logger.getLogger(UpdateAtomFeedPublic.class.getName());
  private Util util = null;
  private AdminConfigDao adminConfigDao = null;

  @Inject
	public UpdateAtomFeedPublic(Util util, AdminConfigDao adminConfigDao) {
		this.util = util;
		this.adminConfigDao = adminConfigDao;
	}

  @Override
  public JSONObject execute() throws JSONException {
    String projectId = this.getParam("projectId");
    if (util.isNullOrEmpty(projectId)) {
      throw new IllegalArgumentException("Missing required param: id");
    }

    String isMakeAtomFeedPublic = this.getParam("isMakeAtomFeedPublic");
    if (util.isNullOrEmpty(isMakeAtomFeedPublic)) {
      throw new IllegalArgumentException("Missing required param: isMakeAtomFeedPublic");
    }
  
    AdminConfig adminConfig = adminConfigDao.getAdminConfig(projectId);
    adminConfig.setAtomFeedPublic(Boolean.parseBoolean(isMakeAtomFeedPublic));
    adminConfigDao.save(adminConfig);
    JSONObject json = new JSONObject();
    json.put("success", "true");
    return json;
  }

}


