package com.aggfi.digest.server.botty.google.forumbotty.admin;

import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import com.aggfi.digest.server.botty.digestbotty.dao.ExtDigestDao;
import com.aggfi.digest.server.botty.digestbotty.model.ExtDigest;
import com.vegalabs.general.server.rpc.util.Util;
import com.aggfi.digest.server.botty.google.forumbotty.dao.AdminConfigDao;
import com.aggfi.digest.server.botty.google.forumbotty.model.AdminConfig;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.vegalabs.general.server.command.Command;

@Singleton
public class GetAdminConfig extends Command {
  private static final Logger LOG = Logger.getLogger(GetAdminConfig.class.getName());

  private AdminConfigDao adminConfigDao = null;
  private Util util = null;
  private ExtDigestDao extDigestDao;

  @Inject
  public GetAdminConfig(Util util, AdminConfigDao adminConfigDao, ExtDigestDao extDigestDao) {
    this.util = util;
    this.adminConfigDao = adminConfigDao;
    this.extDigestDao = extDigestDao;
  }

  @Override
  public JSONObject execute() throws JSONException {
    String projectId = this.getParam("projectId");
    if (util.isNullOrEmpty(projectId)) {
      throw new IllegalArgumentException("Missing required param: projectId");
    }
    
//    String senderId = this.getParam("senderId");
//    if (!util.isNullOrEmpty(senderId)) {
//      verifyUserAccess(extDigestDao, projectId, senderId);
//    }
    ExtDigest digest = extDigestDao.retrDigestsByProjectId(projectId).get(0);
    AdminConfig adminConfig = adminConfigDao.getAdminConfig(projectId);
    JSONObject json = new JSONObject();
    JSONObject adminConfigJson = new JSONObject(util.toJson(adminConfig));
    adminConfigJson.put("digestWaveId", digest.getId());
    json.put("result",adminConfigJson );
    LOG.info(adminConfigJson.toString());
    return json;
  }
}