package com.aggfi.digest.server.botty.google.forumbotty.feeds;

import java.io.IOException;
import java.util.Date;
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
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GetPostCounts extends HttpServlet {
  private static final Logger log = Logger.getLogger(GetPostCounts.class.getName());

  private ForumPostDao forumPostDao = null;
  private Util util = null;

  @Inject
  public GetPostCounts(ForumPostDao forumPostDao, Util util) {
    this.forumPostDao = forumPostDao;
    this.util = util;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
    String projectId = req.getParameter("id");
    if (util.isNullOrEmpty(projectId)) {
      throw new IllegalArgumentException("Missing required param: id");
    }        
    
    JSONObject json = new JSONObject();
    JSONArray jsonArray = new JSONArray();
        
    Date target = new Date();
    target.setHours(0);
    target.setMinutes(0);
    target.setSeconds(0);
    
    int oneDay = 60 * 60 * 24 * 1000;
    
    for (int i = 0; i < 7; i++) {      
      int count = forumPostDao.getPostCount(projectId, target);
      
      JSONObject entry = new JSONObject();
      try {
        entry.put("date", target);
        entry.put("count", count);
      } catch (JSONException e) {
        e.printStackTrace();
      }
      
      jsonArray.put(entry);
      
      target = new Date(target.getTime() - oneDay);
    }
        
    try {
      json.put("results", jsonArray);
      resp.getWriter().print(json.toString());
    } catch (JSONException e) {
      log.log(Level.SEVERE, e.getMessage(), e);
    }
  }
}