package com.aggfi.digest.server.botty.digestbotty.admin;


import org.json.JSONException;
import org.json.JSONObject;

import com.aggfi.digest.server.botty.digestbotty.dao.ContributorDao;
import com.aggfi.digest.server.botty.google.forumbotty.Util;
import com.aggfi.digest.server.botty.google.forumbotty.admin.Command;
import com.google.inject.Inject;

public class GetNextPubPerProject extends Command {
  private ContributorDao contributorDao = null;
  private Util util = null;

  @Inject
  public GetNextPubPerProject(Util util, ContributorDao contributorDao) {
    this.contributorDao = contributorDao;
    this.util = util;
  }

  @Override
  public JSONObject execute() throws JSONException {
    String projectId = this.getParam("projectId");
    if (util.isNullOrEmpty(projectId)) {
      throw new IllegalArgumentException("Missing required param: id");
    }



    JSONObject json = new JSONObject();
    JSONObject result = new JSONObject();
    result.put("pub", "ca-pub-3589749845269196");
    json.put("result", result);
    return json;
  }

}

