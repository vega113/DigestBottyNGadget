package com.aggfi.digest.server.botty.google.forumbotty.admin;

import org.json.JSONException;
import org.json.JSONObject;

import com.aggfi.digest.server.botty.google.forumbotty.Util;
import com.aggfi.digest.server.botty.google.forumbotty.dao.AdminConfigDao;
import com.google.inject.Inject;

public class RemoveAutoTag extends Command {
  private AdminConfigDao adminConfigDao = null;
  private Util util = null;

  @Inject
  public RemoveAutoTag(Util util, AdminConfigDao adminConfigDao) {
    this.adminConfigDao = adminConfigDao;
    this.util = util;
  }

  @Override
  public JSONObject execute() throws JSONException {
    String tag = this.getParam("tag");
    if (util.isNullOrEmpty(tag)) {
      throw new IllegalArgumentException("Missing required param: tag");
    }

    String projectId = this.getParam("projectId");
    if (util.isNullOrEmpty(projectId)) {
      throw new IllegalArgumentException("Missing required param: id");
    }

    this.adminConfigDao.removeAutoTagRegex(projectId, tag);
    JSONObject json = new JSONObject();
    json.put("success", "true");
    return json;
  }

}
