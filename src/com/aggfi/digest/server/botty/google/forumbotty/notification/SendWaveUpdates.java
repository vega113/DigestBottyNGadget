package com.aggfi.digest.server.botty.google.forumbotty.notification;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.aggfi.digest.server.botty.google.forumbotty.ForumBotty;
import com.aggfi.digest.server.botty.google.forumbotty.Util;
import com.aggfi.digest.server.botty.google.forumbotty.dao.AdminConfigDao;
import com.aggfi.digest.server.botty.google.forumbotty.dao.DigestDao;
import com.aggfi.digest.server.botty.google.forumbotty.dao.ForumPostDao;
import com.aggfi.digest.server.botty.google.forumbotty.dao.UserNotificationDao;
import com.aggfi.digest.server.botty.google.forumbotty.model.Digest;
import com.aggfi.digest.server.botty.google.forumbotty.model.ForumPost;
import com.aggfi.digest.server.botty.google.forumbotty.model.UserNotification;
import com.aggfi.digest.server.botty.google.forumbotty.model.UserNotification.NotificationType;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Method;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.wave.api.Blip;
import com.google.wave.api.BlipContentRefs;
import com.google.wave.api.Wavelet;

@Singleton
public class SendWaveUpdates extends HttpServlet {
  private static final Logger LOG = Logger.getLogger(SendWaveUpdates.class.getName());

  public static final String DIGEST_DATA_DOC_NAME = "forumbotty_digest";

  private static final int ADD_PARTICIPANT_CHUNK_SIZE = 20;

  private boolean test_mode = false;

  private ForumPostDao forumPostDao = null;
  private UserNotificationDao userNotificationDao = null;
  private AdminConfigDao adminConfigDao = null;
  private DigestDao digestDao = null;
  private Util util = null;
  private ForumBotty robot = null;

  @Inject
  public SendWaveUpdates(ForumPostDao forumPostDao, UserNotificationDao userNotificationDao,
      AdminConfigDao adminConfigDao, DigestDao digestDao, Util util, ForumBotty robot) {
    this.forumPostDao = forumPostDao;
    this.userNotificationDao = userNotificationDao;
    this.adminConfigDao = adminConfigDao;
    this.digestDao = digestDao;
    this.util = util;
    this.robot = robot;
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

    NotificationType notificationType = NotificationType.DAILY;
    if (!util.isNullOrEmpty(req.getParameter("type"))) {
      notificationType = NotificationType.valueOf(req.getParameter("type").toUpperCase());
    }

    if (!util.isNullOrEmpty(req.getParameter("test"))) {
      test_mode = true;
      LOG.info("sending wave digest in test mode");
    } else {
      test_mode = false;
      LOG.info("sending wave digest in prod mode");
    }

    // Populate default participants list
    Set<String> defaultParticipants = new HashSet<String>();

    if (!test_mode) {
      for (String participant : this.adminConfigDao.getAdminConfig(projectId)
          .getDefaultParticipants()) {
        defaultParticipants.add(participant);
      }
    } else {
      String[] testReceivers = req.getParameter("test").split(",");
      for (String testReceiver : testReceivers) {
        defaultParticipants.add(testReceiver);
      }
    }

    Wavelet wavelet = null;

    wavelet = robot.newWave(robot.getDomain(), defaultParticipants, "test", projectId, robot
        .getRpcServerUrl());

    String title = "Wave API Digest";

    wavelet.getDataDocuments().set(DIGEST_DATA_DOC_NAME, projectId);

    if (!test_mode) {
      // Add the list of default tags
      for (String tag : this.adminConfigDao.getAdminConfig(projectId).getDefaultTags()) {
        wavelet.getTags().add(tag);
      }
      // Add another extra tag for digest
      wavelet.getTags().add("digest");
    }

    Blip rootBlip = wavelet.getRootBlip();

    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss zzz");

    wavelet.setTitle(title);

    String all = "\nAll recently updated -\n";

    rootBlip.append(all);

    int start = title.length() + 1 + all.length();
    int end = -1;

    List<ForumPost> entries = forumPostDao.getRecentlyUpdated(projectId, count);

    for (ForumPost entry : entries) {
      String entryTitle = entry.getTitle();
      Date lastUpdated = entry.getLastUpdated();
      String id = URLEncoder.encode(entry.getId(), "UTF-8");
      String domain = entry.getDomain();

      String dateString = sdf.format(lastUpdated);

      String line = entryTitle + "\n";

      rootBlip.append(line);

      end = start + line.length();

      //LOG.info(String.format("start=%d end=%d", start, end));

      BlipContentRefs.range(rootBlip, start, end).annotate("link/wave", entry.getId());

      start = end;
    }

//    String robots = "\nRecently updated with tag \"robot\" -\n";
//
//    rootBlip.append(robots);
//
//    start = start + robots.length();
//
//    entries = forumPostDao.getForumPostsByTag(projectId, "robot", count);
//
//    for (ForumPost entry : entries) {
//      String entryTitle = entry.getTitle();
//      Date lastUpdated = entry.getLastUpdated();
//
//      String line = entryTitle + "\n";
//
//      rootBlip.append(line);
//
//      end = start + line.length();
//
//      //LOG.info(String.format("start=%d end=%d", start, end));
//
//      BlipContentRefs.range(rootBlip, start, end).annotate("link/wave", entry.getId());
//
//      start = end;
//    }
//
//    String gadgets = "\nRecently updated with tag \"gadget\" -\n";
//
//    rootBlip.append(gadgets);
//
//    start = start + gadgets.length();
//
//    entries = forumPostDao.getForumPostsByTag(projectId, "gadget", count);
//
//    for (ForumPost entry : entries) {
//      String entryTitle = entry.getTitle();
//      Date lastUpdated = entry.getLastUpdated();
//
//      String line = entryTitle + "\n";
//
//      rootBlip.append(line);
//
//      end = start + line.length();
//
//      //LOG.info(String.format("start=%d end=%d", start, end));
//
//      BlipContentRefs.range(rootBlip, start, end).annotate("link/wave", entry.getId());
//
//      start = end;
//    }
//
//    String embeds = "\nRecently updated with tag \"embed\" -\n";
//
//    rootBlip.append(embeds);
//
//    start = start + embeds.length();
//
//    entries = forumPostDao.getForumPostsByTag(projectId, "embed", count);
//
//    for (ForumPost entry : entries) {
//      String entryTitle = entry.getTitle();
//      Date lastUpdated = entry.getLastUpdated();
//
//      String line = entryTitle + "\n";
//
//      rootBlip.append(line);
//
//      end = start + line.length();
//
//      LOG.info(String.format("start=%d end=%d", start, end));
//
//      BlipContentRefs.range(rootBlip, start, end).annotate("link/wave", entry.getId());
//
//      start = end;
//    }

    robot.submit(wavelet, robot.getRpcServerUrl());
    
    addNotificationUsersByChunks(wavelet, projectId, notificationType, ADD_PARTICIPANT_CHUNK_SIZE);
    
    digestDao.save(new Digest(wavelet.getDomain(), wavelet.getWaveId().getId(), projectId));
  }
  
  private void addNotificationUsersByChunks(Wavelet wavelet, String projectId, NotificationType notificationType, int chunkSize) {
    if (!test_mode) {
      List<UserNotification> userNotifications = userNotificationDao.getAllUserNotifications(
          projectId, notificationType);      

      JSONObject json = new JSONObject();
      try {
        json.put("domain", wavelet.getDomain());
        json.put("waveId", wavelet.getWaveId().getId());
        json.put("waveletId", wavelet.getWaveletId().getId());
        json.put("projectId", projectId);
        json.put("notificationType", notificationType.toString());
      } catch (JSONException e) {
        e.printStackTrace();
      }

      Queue queue = QueueFactory.getDefaultQueue();

      int startIndex = 0;
      int endIndex = -1;
      
      for (int i = 0; i < userNotifications.size(); i += chunkSize) {
        startIndex = i;
        endIndex = (startIndex + chunkSize < userNotifications.size()) ? startIndex + chunkSize : userNotifications.size();
        
        if (startIndex < endIndex) {
          try {
            json.put("startIndex", startIndex);
            json.put("endIndex", endIndex);
          } catch (JSONException e) {
            e.printStackTrace();
          }
          TaskOptions taskOptions = TaskOptions.Builder.url("/add_participants_task").method(
              Method.POST).payload(json.toString());
          queue.add(taskOptions);          
        }        
      }
    }    
  }
}
