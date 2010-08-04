package com.aggfi.digest.server.botty.google.forumbotty.feeds;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import com.aggfi.digest.server.botty.digestbotty.dao.ExtDigestDao;
import com.aggfi.digest.server.botty.digestbotty.model.ExtDigest;
import com.aggfi.digest.server.botty.google.forumbotty.Util;
import com.aggfi.digest.server.botty.google.forumbotty.dao.ForumPostDao;
import com.aggfi.digest.server.botty.google.forumbotty.model.ForumPost;
import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@SuppressWarnings("unchecked")
@Singleton
public class AtomGenerator extends HttpServlet {
  private static final Logger LOG = Logger.getLogger(AtomGenerator.class.getName());

  private ForumPostDao forumPostDao = null;
  private ExtDigestDao extDigestDao = null;
  private Util util = null;

  private static Cache cache = null;
  private static final String FEED_CACHE_NAME = "atom";
  private static final int FEED_CACHE_TIME_LIMIT = 60; // in sec

  static {
    try {
      CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
      Map props = new HashMap();
      props.put(GCacheFactory.EXPIRATION_DELTA, FEED_CACHE_TIME_LIMIT);
      cache = cacheFactory.createCache(props);
    } catch (CacheException e) {
    }
  }

  @Inject
  public AtomGenerator(ForumPostDao forumPostDao, ExtDigestDao extDigestDao, Util util) {
    this.forumPostDao = forumPostDao;
    this.extDigestDao = extDigestDao;
    this.util = util;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    int count = 10;
    if (!util.isNullOrEmpty(req.getParameter("count"))) {
      count = Integer.parseInt(req.getParameter("count"));
    }

    String projectId = req.getParameter("id");
    if (util.isNullOrEmpty(projectId)) {
      throw new IllegalArgumentException("Missing required param: id");
    }

    if (cache.containsKey(FEED_CACHE_NAME+"."+projectId)) {
      resp.setHeader("content-type", "application/atom+xml");
      resp.getWriter().println(cache.get(FEED_CACHE_NAME+"."+projectId));
    } 
    else
    {
      List<ForumPost> entries = this.forumPostDao.getRecentlyUpdated(projectId, count);

      Date latestUpdate = new Date();
      if (entries.size() > 0) {
        latestUpdate = entries.get(0).getLastUpdated();
      }
      
      List<ExtDigest>  digests = extDigestDao.retrDigestsByProjectId(projectId);
      String forumName = "DigestBotty";
      if(digests.size() > 0){
      	forumName = digests.get(0).getName();
      }
      String serverHost = req.getServerName();

      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ssZ");

      StringBuffer content = new StringBuffer();
      content.append("<feed xmlns=\"http://www.w3.org/2005/Atom\">");
      content.append(String.format("<id>http://%s/feeds/atom?id=%s</id>", serverHost, projectId));
      content.append("<title type=\"text\">" + forumName +"</title>");
      content.append(String.format(
          "<link href=\"http://%s/feeds/atom?id=%s\" rel=\"self\"></link>", serverHost, projectId));
      content.append("<author><name>" + System.getProperty("APP_DOMAIN") + "</name></author>");
      content.append(String.format("<updated>%s</updated>", String.format("%sT%s", dateFormat
          .format(latestUpdate), timeFormat.format(latestUpdate))));

      for (ForumPost entry : entries) {
        String author = entry.getCreator();
        if(author == null || "".equals(author)){
        	author = System.getProperty("APP_DOMAIN") + "@appspot.com";
        }
        String title = entry.getTitle();
        Date updated = entry.getLastUpdated();
        String id = URLEncoder.encode(entry.getId(), "UTF-8");

        String waveUrl = String.format("https://wave.google.com/wave/waveref/%s", id.replaceFirst("%21", "/"));
        String feedTxt = entry.getFirstBlipContent() != null ? entry.getFirstBlipContent().getValue() + "\n\nlink: " + waveUrl : waveUrl;

        content.append("<entry>");
        content.append(String.format("<id>http://%s/post/%s</id>", serverHost, id));
        content.append(String.format("<title type=\"text\">%s</title>", title));
//        content.append(String.format("<link href=\"http://%s/post/%s\" rel=\"self\"></link>", serverHost, id));//Trying
        content.append(String.format("<link href=\"%s\" rel=\"self\"></link>", waveUrl));
        content.append(String.format("<author><name>%s</name></author>", author));
        content.append(String.format("<updated>%s</updated>", String.format("%sT%s", dateFormat
            .format(updated), timeFormat.format(updated))));
        content.append(String.format("<content type=\"text\">%s</content>", feedTxt));
        content.append("</entry>");
      }

      content.append("</feed>");

      cache.put(FEED_CACHE_NAME+"."+projectId, content.toString());

      resp.setHeader("content-type", "application/atom+xml");
      resp.getWriter().println(content.toString());
    }
  }

}