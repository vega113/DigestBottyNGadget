package com.aggfi.digest.server.botty.google.forumbotty.admin;

import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import com.aggfi.digest.server.botty.google.forumbotty.Util;
import com.aggfi.digest.server.botty.google.forumbotty.dao.AdminConfigDao;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GetAdminConfig extends Command {
  private static final Logger log = Logger.getLogger(GetAdminConfig.class.getName());

  private AdminConfigDao adminConfigDao = null;
  private Util util = null;

  @Inject
  public GetAdminConfig(Util util, AdminConfigDao adminConfigDao) {
    this.util = util;
    this.adminConfigDao = adminConfigDao;
  }

  @Override
  public JSONObject execute() throws JSONException {
    String projectId = this.getParam("projectId");
    if (util.isNullOrEmpty(projectId)) {
      throw new IllegalArgumentException("Missing required param: projectId");
    }

    JSONObject json = new JSONObject();
    json.put("result", new JSONObject(util.toJson(adminConfigDao.getAdminConfig(projectId))));
    return json;
  }
}