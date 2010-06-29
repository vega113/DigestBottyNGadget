package com.aggfi.digest.server.botty.google.forumbotty.admin;

import org.json.JSONException;
import org.json.JSONObject;

import com.aggfi.digest.server.botty.google.forumbotty.Util;
import com.aggfi.digest.server.botty.google.forumbotty.dao.AdminConfigDao;
import com.google.inject.Inject;

public class AddDefaultParticipant extends Command {
  private AdminConfigDao adminConfigDao = null;
  private Util util = null;

  @Inject
  public AddDefaultParticipant(Util util, AdminConfigDao adminConfigDao) {
    this.adminConfigDao = adminConfigDao;
    this.util = util;
  }

  @Override
  public JSONObject execute() throws JSONException {
    String projectId = this.getParam("projectId");
    if (util.isNullOrEmpty(projectId)) {
      throw new IllegalArgumentException("Missing required param: id");
    }

    String participantId = this.getParam("participantId");
    if (util.isNullOrEmpty(participantId)) {
      throw new IllegalArgumentException("Missing required param: participantId");
    }

    this.adminConfigDao.addDefaultParticipant(projectId, participantId);
    JSONObject json = new JSONObject();
    json.put("success", "true");
    return json;
  }

}
