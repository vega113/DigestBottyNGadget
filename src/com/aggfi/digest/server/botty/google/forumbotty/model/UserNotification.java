package com.aggfi.digest.server.botty.google.forumbotty.model;

import java.util.Date;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.gson.annotations.Expose;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class UserNotification {
  @PrimaryKey
  @Persistent
  @Expose
  private String id = null;

  @Persistent
  @Expose
  private String userId = null;

  @Persistent
  @Expose
  private String projectId = null;

  public enum NotificationType {
    NONE,
    DAILY,
    WEEKLY
  }

  @Persistent
  @Expose
  private NotificationType notificationType = NotificationType.NONE;

  @Persistent
  @Expose
  private Date lastUpdated = null;

  public UserNotification(String userId, String projectId) {
    this.userId = userId;
    this.projectId = projectId;
    this.id = userId + "__" + projectId;
    this.notificationType = NotificationType.NONE;
    this.lastUpdated = new Date();
  }

  public String getId() {
    return id;
  }

  public Date getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(Date lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public NotificationType getNotificationType() {
    return notificationType;
  }

  public void setNotificationType(NotificationType type) {
    this.notificationType = type;
    this.lastUpdated = new Date();
  }

  public String getUserId() {
    return userId;
  }

  public String getProjectId() {
    return projectId;
  }
}
