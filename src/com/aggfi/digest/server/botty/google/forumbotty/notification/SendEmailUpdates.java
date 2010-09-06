package com.aggfi.digest.server.botty.google.forumbotty.notification;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vegalabs.general.server.rpc.util.Util;
import com.aggfi.digest.server.botty.google.forumbotty.dao.ForumPostDao;
import com.aggfi.digest.server.botty.google.forumbotty.model.ForumPost;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SendEmailUpdates extends HttpServlet {
  private static final Logger log = Logger.getLogger(SendEmailUpdates.class.getName());

  private static final String FROM_EMAIL = "austin.chau@gmail.com";
  private static List<String> bccList = new ArrayList<String>();

  private static final String EMAIL_SUBJECT = "Wave updates";

  static {
    //bccList.add("austin.chau@gmail.com");
    //bccList.add("pamelafox@gmail.com");
  }

  private ForumPostDao forumPostDao = null;
  private Util util = null;

  @Inject
  public SendEmailUpdates(ForumPostDao forumPostDao, Util util) {
    this.forumPostDao = forumPostDao;
    this.util = util;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    if (util.isNullOrEmpty(req.getParameter("count"))) {
      throw new IllegalArgumentException("Missing required param: count");
    }

    String projectId = req.getParameter("id");
    if (util.isNullOrEmpty(projectId)) {
      throw new IllegalArgumentException("Missing required param: id");
    }

    StringBuffer body = new StringBuffer();

    body.append(getRecentUpdates(projectId, Integer.parseInt(req.getParameter("count"))));
    body.append("<br><br>");
    body.append(getUpdatesByTag(projectId, "robot"));
    body.append("<br><br>");
    body.append(getUpdatesByTag(projectId, "gadget"));
    body.append("<br><br>");
    body.append(getUpdatesByTag(projectId, "embed"));

    sendEmail("majorfluxy@gmail.com", FROM_EMAIL, EMAIL_SUBJECT, body.toString());
  }

  public String getUpdatesByTag(String projectId, String tag) throws UnsupportedEncodingException {
    List<ForumPost> entries = forumPostDao.getForumPostsByTag(projectId, tag, 10);

    StringBuffer body = new StringBuffer();
    body.append("<b>" + tag + "</b><br>");

    for (ForumPost entry : entries) {
      String title = entry.getTitle();
      Date lastUpdated = entry.getLastUpdated();
      String id = URLEncoder.encode(entry.getId(), "UTF-8");
      String domain = entry.getDomain();

      SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss zzz");

      String waveUrl = String.format("https://wave.google.com/a/%s/#restored:wave:%s", domain, id);
      String waveLink = String.format("[%s] <a href='%s'>%s</a>", sdf.format(lastUpdated), waveUrl,
          title);
      body.append(waveLink);
      body.append("<br><br>");
    }
    return body.toString();
  }

  public String getRecentUpdates(String projectId, int count) throws UnsupportedEncodingException {
    List<ForumPost> entries = forumPostDao.getRecentlyUpdated(projectId, count);

    StringBuffer body = new StringBuffer();

    for (ForumPost entry : entries) {
      String title = entry.getTitle();
      Date lastUpdated = entry.getLastUpdated();
      String id = URLEncoder.encode(entry.getId(), "UTF-8");
      String domain = entry.getDomain();

      SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss zzz");

      String waveUrl = String.format("https://wave.google.com/a/%s/#restored:wave:%s", domain, id);
      String waveLink = String.format("[%s] <a href='%s'>%s</a>", sdf.format(lastUpdated), waveUrl,
          title);
      body.append(waveLink);
      body.append("<br><br>'");
    }
    return body.toString();
  }

  public void sendEmail(String toAddress, String fromAddress, String subject, String body) {

    if (toAddress == null || toAddress.trim().length() == 0) {
      return;
    }

    try {
      Properties props = new Properties();
      Session session = Session.getDefaultInstance(props, null);
      Message msg = new MimeMessage(session);

      msg.setFrom(new InternetAddress(fromAddress, fromAddress));
      msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress, toAddress));

      for (String bcc : bccList) {
        msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc, bcc));
      }

      msg.setSubject(subject);
      msg.setContent(body, "text/html");
      Transport.send(msg);

    } catch (IllegalArgumentException e) {
      log.log(Level.WARNING, "", e);
    } catch (UnsupportedEncodingException e) {
      log.log(Level.WARNING, "", e);
    } catch (MessagingException e) {
      log.log(Level.WARNING, "", e);
    }
  }
}