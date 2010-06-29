package com.aggfi.digest.server.botty.google.forumbotty.feeds;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;

import com.aggfi.digest.server.botty.google.forumbotty.Util;
import com.aggfi.digest.server.botty.google.forumbotty.dao.DigestDao;
import com.aggfi.digest.server.botty.google.forumbotty.model.Digest;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GetLatestDigest extends HttpServlet {
  private static final Logger log = Logger.getLogger(GetLatestDigest.class.getName());

  private DigestDao digestDao = null;
  private Util util = null;

  @Inject
  public GetLatestDigest(DigestDao digestDao, Util util) {
    this.digestDao = digestDao;
    this.util = util;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    Digest digest = digestDao.getLatestDigest();

    JSONObject json = new JSONObject();
    try {
      json.put("results", new JSONObject(util.toJson(digest)));
      resp.getWriter().print(json.toString());
    } catch (JSONException e) {
      log.log(Level.SEVERE, e.getMessage(), e);
    }
  }
}