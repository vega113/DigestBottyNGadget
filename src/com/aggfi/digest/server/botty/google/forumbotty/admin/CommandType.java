package com.aggfi.digest.server.botty.google.forumbotty.admin;

import com.aggfi.digest.server.botty.digestbotty.admin.AddDigestManager;
import com.aggfi.digest.server.botty.digestbotty.admin.CreateDigest;
import com.aggfi.digest.server.botty.digestbotty.admin.GetProjectsPerUser;


public enum CommandType {
  GET_ADMIN_CONFIG(
      GetAdminConfig.class),
  ADD_AUTO_TAG(
      AddAutoTag.class),
  REMOVE_AUTO_TAG(
      RemoveAutoTag.class),
  ADD_DEFAULT_TAG(
      AddDefaultTag.class),
  REMOVE_DEFAULT_TAG(
      RemoveDefaultTag.class),
  ADD_DEFAULT_PARTICIPANT(
      AddDefaultParticipant.class),
  REMOVE_DEFAULT_PARTICIPANT(
      RemoveDefaultParticipant.class),
  GET_TAG_COUNTS(GetTagCounts.class),
  GET_POST_COUNTS(GetPostCounts.class),
  ADD_DIGEST_MANAGER(AddDigestManager.class),
  GET_PROJECTS_PER_USER(GetProjectsPerUser.class),
  CREATE_DIGEST(CreateDigest.class);

  private Class<? extends Command> clazz = null;

  CommandType(Class<? extends Command> clazz) {
    this.clazz = clazz;
  }

  public Class<? extends Command> getClazz() {
    return clazz;
  }

  public static CommandType valueOfIngoreCase(String name) {
    return CommandType.valueOf(name.toUpperCase());
  }
}
