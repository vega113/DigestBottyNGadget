package com.aggfi.digest.server.botty.google.forumbotty.upgrade;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vegalabs.general.server.rpc.util.Util;
import com.aggfi.digest.server.botty.google.forumbotty.dao.ForumPostDao;
import com.aggfi.digest.server.botty.google.forumbotty.dao.TagDao;
import com.aggfi.digest.server.botty.google.forumbotty.model.ForumPost;
import com.aggfi.digest.server.botty.google.forumbotty.model.Tag;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class MigrateTagsTask extends HttpServlet {
  private static final Logger LOG = Logger.getLogger(MigrateTagsTask.class.getName());
  @Inject
  private Util util;

  @Inject
  private ForumPostDao forumPostDao = null;
  
  @Inject
  private TagDao tagDao = null;  
  
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {    
    String id = req.getParameter("id");
    id = id.replaceAll(" ", "+");
    LOG.info("migrating ForumPost id = " + id);
    ForumPost entry = forumPostDao.getForumPost(id);
    
    for (String tag : entry.getTags()) {
      Tag tagModel = tagDao.getTag(entry.getProjectId(), tag);
      tagModel = tagDao.incrementCount(tagModel);
      tagDao.save(tagModel);
    }   
  }
}