package com.aggfi.digest.server.botty.google.forumbotty.notification;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.waveprotocol.wave.model.id.WaveId;
import org.waveprotocol.wave.model.id.WaveletId;

import com.aggfi.digest.server.botty.google.forumbotty.ForumBotty;
import com.aggfi.digest.server.botty.google.forumbotty.Util;
import com.aggfi.digest.server.botty.google.forumbotty.dao.UserNotificationDao;
import com.aggfi.digest.server.botty.google.forumbotty.model.UserNotification;
import com.aggfi.digest.server.botty.google.forumbotty.model.UserNotification.NotificationType;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.wave.api.Wavelet;

@Singleton
public class AddParticipantsTask extends HttpServlet {
  private static final Logger LOG = Logger.getLogger(AddParticipantsTask.class.getName());

  private UserNotificationDao userNotificationDao = null;
  private Util util = null;
  private ForumBotty robot = null;

  @Inject
  public AddParticipantsTask(UserNotificationDao userNotificationDao, Util util, ForumBotty robot) {
    this.userNotificationDao = userNotificationDao;
    this.util = util;
    this.robot = robot;
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String json = util.getPostBody(req);
    JSONObject jsonObj;
    try {
      jsonObj = new JSONObject(json);
      String domain = jsonObj.getString("domain");
      String waveId = jsonObj.getString("waveId");
      String waveletId = jsonObj.getString("waveletId");
      String projectId = jsonObj.getString("projectId");
      NotificationType notificationType = NotificationType.valueOf(jsonObj.getString(
          "notificationType").toUpperCase());
      int startIndex = Integer.parseInt(jsonObj.getString("startIndex"));
      int endIndex = Integer.parseInt(jsonObj.getString("endIndex"));
      LOG.info("wave id = " + waveId);
      LOG.info("wavelet id = " + waveletId);
      LOG.info("startIndex = " + startIndex);
      LOG.info("endIndex = " + endIndex);
      LOG.info("notificationType = " + notificationType.toString());

      Wavelet wavelet = robot.fetchWavelet(new WaveId(domain, waveId), new WaveletId(domain,
          waveletId), projectId, robot.getRpcServerUrl());

      List<UserNotification> userNotifications = userNotificationDao.getAllUserNotifications(
          projectId, notificationType);

      for (int i = startIndex; i < endIndex; i++) {
        wavelet.getParticipants().add(userNotifications.get(i).getUserId());
      }
      robot.submit(wavelet, robot.getRpcServerUrl());
    } catch (JSONException e) {

    }
  }
}