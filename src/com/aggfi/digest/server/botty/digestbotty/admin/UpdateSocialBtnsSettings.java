package com.aggfi.digest.server.botty.digestbotty.admin;

import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import com.vegalabs.general.server.rpc.util.Util;
import com.aggfi.digest.server.botty.google.forumbotty.dao.AdminConfigDao;
import com.aggfi.digest.server.botty.google.forumbotty.model.AdminConfig;
import com.google.inject.Inject;
import com.vegalabs.general.server.command.Command;

public class UpdateSocialBtnsSettings extends Command {
  private static final Logger LOG = Logger.getLogger(UpdateSocialBtnsSettings.class.getName());
  private Util util = null;
  private AdminConfigDao adminConfigDao = null;

  @Inject
	public UpdateSocialBtnsSettings(Util util, AdminConfigDao adminConfigDao) {
		this.util = util;
		this.adminConfigDao = adminConfigDao;
	}

  @Override
  public JSONObject execute() throws JSONException {
    String projectId = this.getParam("projectId");
    if (util.isNullOrEmpty(projectId)) {
      throw new IllegalArgumentException("Missing required param: id");
    }

    String isDiggEnabled = this.getParam("isDiggEnabled");
    if (util.isNullOrEmpty(isDiggEnabled)) {
      throw new IllegalArgumentException("Missing required param: isDiggEnabled");
    }
    String isBuzzEnabled = this.getParam("isBuzzEnabled");
    if (util.isNullOrEmpty(isBuzzEnabled)) {
      throw new IllegalArgumentException("Missing required param: isBuzzEnabled");
    }
    String isTweetEnabled = this.getParam("isTweetEnabled");
    if (util.isNullOrEmpty(isTweetEnabled)) {
      throw new IllegalArgumentException("Missing required param: isTweetEnabled");
    }
    String isFaceEnabled = this.getParam("isFaceEnabled");
    if (util.isNullOrEmpty(isFaceEnabled)) {
      throw new IllegalArgumentException("Missing required param: isFaceEnabled");
    }
  
    AdminConfig adminConfig = adminConfigDao.getAdminConfig(projectId);
    adminConfig.setDiggBtnEnabled(Boolean.parseBoolean(isDiggEnabled));
    adminConfig.setBuzzBtnEnabled(Boolean.parseBoolean(isBuzzEnabled));
    adminConfig.setTweetBtnEnabled(Boolean.parseBoolean(isTweetEnabled));
    adminConfig.setFaceBtnEnabled(Boolean.parseBoolean(isFaceEnabled));
    adminConfigDao.save(adminConfig);
    JSONObject json = new JSONObject();
    json.put("success", "true");
    return json;
  }

}
