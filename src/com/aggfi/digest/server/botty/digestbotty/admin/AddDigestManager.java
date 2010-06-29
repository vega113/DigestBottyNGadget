package com.aggfi.digest.server.botty.digestbotty.admin;

import org.json.JSONException;
import org.json.JSONObject;

import com.aggfi.digest.server.botty.google.forumbotty.Util;
import com.aggfi.digest.server.botty.google.forumbotty.admin.Command;
import com.aggfi.digest.server.botty.google.forumbotty.dao.AdminConfigDao;
import com.google.inject.Inject;

public class AddDigestManager extends Command {
  private AdminConfigDao adminConfigDao = null;
  private Util util = null;

  @Inject
  public AddDigestManager(Util util, AdminConfigDao adminConfigDao) {
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
      throw new IllegalArgumentException("Missing required param: managerId");
    }

    this.adminConfigDao.addDigestManager(projectId, managerId);
    JSONObject json = new JSONObject();
    json.put("success", "true");
    return json;
  }

}

