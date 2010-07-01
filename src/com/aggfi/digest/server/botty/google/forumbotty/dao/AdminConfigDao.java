package com.aggfi.digest.server.botty.google.forumbotty.dao;

import com.aggfi.digest.server.botty.google.forumbotty.model.AdminConfig;


public interface AdminConfigDao {

  public abstract void addAutoTagRegex(String projectId, String tag, String regex);

  public abstract void addDefaultTag(String projectId, String tag);

  public abstract void removeDefaultTag(String projectId, String tag);

  public abstract void removeAutoTagRegex(String projectId, String tag);

  public abstract AdminConfig save(AdminConfig adminConfig);

  public abstract AdminConfig getAdminConfig(String id);

  public abstract void addDefaultParticipant(String projectId, String participantId);

  public abstract void removeDefaultParticipant(String projectId, String participantId);

  public abstract void addDigestManager(String projectId, String managerId);

  public abstract void removeDigestManager(String projectId, String managerId);

}
