package com.aggfi.digest.server.botty.digestbotty.admin;

import java.util.logging.Logger;

import org.json.JSONException;
import com.vegalabs.general.server.command.Command;
import org.json.JSONObject;
import com.vegalabs.general.server.rpc.util.Util;
import com.aggfi.digest.server.botty.google.forumbotty.dao.AdminConfigDao;
import com.google.inject.Inject;

public class AddDigestManager extends Command {
	Logger LOG  = Logger.getLogger(AddDigestManager.class.getName());
  private AdminConfigDao adminConfigDao = null;
  private Util util = null;

  @Inject
  public AddDigestManager(Util util, AdminConfigDao adminConfigDao) {
    this.adminConfigDao = adminConfigDao;
    this.util = util;
  }

  @Override
  public JSONObject execute() throws JSONException {
	  LOG.entering(this.getClass().getName(), "execute", toString());
    String projectId = this.getParam("projectId");
    if (util.isNullOrEmpty(projectId)) {
      throw new IllegalArgumentException("Missing required param: id");
    }

    String managerId = this.getParam("managerId");
    if (util.isNullOrEmpty(managerId)) {
      throw new IllegalArgumentException("Missing required param: managerId");
    }

    this.adminConfigDao.addDigestManager(projectId, managerId);
    JSONObject json = new JSONObject();
    json.put("success", "true");
    return json;
  }

}

