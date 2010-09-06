package com.aggfi.digest.server.botty.google.forumbotty.feeds;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vegalabs.general.server.rpc.util.Util;
import com.aggfi.digest.server.botty.google.forumbotty.dao.ForumPostDao;
import com.aggfi.digest.server.botty.google.forumbotty.model.ForumPost;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class JsonGenerator extends HttpServlet {
  private static final Logger log = Logger.getLogger(JsonGenerator.class.getName());

  private ForumPostDao forumPostDao = null;
  private Util util = null;

  @Inject
  public JsonGenerator(ForumPostDao forumPostDao, Util util) {
    this.forumPostDao = forumPostDao;
    this.util = util;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    int limit = 10;
    if (!util.isNullOrEmpty(req.getParameter("limit"))) {
      limit = Integer.parseInt(req.getParameter("limit"));
    }

    String projectId = req.getParameter("id");
    if (util.isNullOrEmpty(projectId)) {
      throw new IllegalArgumentException("Missing required param: id");
    }

    List<ForumPost> entries = forumPostDao.getRecentlyUpdated(projectId, limit);

    JSONObject json = new JSONObject();
    try {
      json.put("results", new JSONArray(util.toJson(entries)));
      resp.getWriter().print(json.toString());
    } catch (JSONException e) {
      log.log(Level.SEVERE, e.getMessage(), e);
    }
  }
}