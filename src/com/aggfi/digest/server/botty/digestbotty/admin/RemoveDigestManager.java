package com.aggfi.digest.server.botty.digestbotty.admin;

import org.json.JSONException;
import com.vegalabs.general.server.command.Command;
import org.json.JSONObject;
import com.vegalabs.general.server.rpc.util.Util;
import com.aggfi.digest.server.botty.google.forumbotty.dao.AdminConfigDao;
import com.google.inject.Inject;

public class RemoveDigestManager extends Command {
  private AdminConfigDao adminConfigDao = null;
  private Util util = null;

  @Inject
  public RemoveDigestManager(Util util, AdminConfigDao adminConfigDao) {
    this.adminConfigDao = adminConfigDao;
    this.util = util;
  }

  @Override
  public JSONObject execute() throws JSONException {
    String projectId = this.getParam("projectId");
    if (util.isNullOrEmpty(projectId)) {
      throw new IllegalArgumentException("Missing required param: id");
    }

    String managerId = this.getParam("managerId");
    if (util.isNullOrEmpty(managerId)) {
      throw new IllegalArgumentException("Missing required param: ManagerId");
    }

    this.adminConfigDao.removeDigestManager(projectId, managerId);
    JSONObject json = new JSONObject();
    json.put("success", "true");
    return json;
  }

}
