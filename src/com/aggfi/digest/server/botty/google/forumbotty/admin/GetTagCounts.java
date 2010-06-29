package com.aggfi.digest.server.botty.google.forumbotty.admin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.aggfi.digest.server.botty.google.forumbotty.Util;
import com.aggfi.digest.server.botty.google.forumbotty.dao.TagDao;
import com.google.inject.Inject;

public class GetTagCounts extends Command {
  private TagDao tagDao = null;
  private Util util = null;

  @Inject
  public GetTagCounts(Util util, TagDao tagDao) {
    this.tagDao = tagDao;
    this.util = util;
  }

  @Override
  public JSONObject execute() throws JSONException {      
    String projectId = this.getParam("projectId");
    if (util.isNullOrEmpty(projectId)) {
      throw new IllegalArgumentException("Missing required param: id");
    }    
    JSONObject json = new JSONObject();
    json.put("result", new JSONArray(util.toJson(tagDao.getTagCounts(projectId, true))));
    return json;
  }

}
