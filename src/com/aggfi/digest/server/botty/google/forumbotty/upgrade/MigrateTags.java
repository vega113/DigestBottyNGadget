package com.aggfi.digest.server.botty.google.forumbotty.upgrade;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aggfi.digest.server.botty.google.forumbotty.model.ForumPost;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Method;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class MigrateTags extends HttpServlet {
  private static final Logger log = Logger.getLogger(MigrateTags.class.getName());

  @Inject
  private PersistenceManagerFactory pmf;

  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    PersistenceManager pm = pmf.getPersistenceManager();
    try {
      Query query = pm.newQuery(ForumPost.class);
      List<ForumPost> list = (List<ForumPost>) query.execute();
      Queue queue = QueueFactory.getDefaultQueue();
      for (ForumPost entry : list) {
        String id = URLEncoder.encode(entry.getId(), "UTF-8");
        TaskOptions taskOptions = TaskOptions.Builder.url("/migrateDataTask?id=" +id).method(Method.GET);
        queue.add(taskOptions);
      }
    } finally {
      pm.close();
    }
  }
}