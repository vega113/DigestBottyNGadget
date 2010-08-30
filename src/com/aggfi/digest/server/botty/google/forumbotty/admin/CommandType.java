package com.aggfi.digest.server.botty.google.forumbotty.admin;

import com.aggfi.digest.server.botty.digestbotty.admin.AddDigestManager;
import com.aggfi.digest.server.botty.digestbotty.admin.AddReadOnlyPostGadget;
import com.aggfi.digest.server.botty.digestbotty.admin.AddWavesParticipant;
import com.aggfi.digest.server.botty.digestbotty.admin.CreateDigest;
import com.aggfi.digest.server.botty.digestbotty.admin.CreateNewPost;
import com.aggfi.digest.server.botty.digestbotty.admin.GetAllForumIds;
import com.aggfi.digest.server.botty.digestbotty.admin.GetBlipsCount;
import com.aggfi.digest.server.botty.digestbotty.admin.GetBlipsPerContributor;
import com.aggfi.digest.server.botty.digestbotty.admin.GetContributorsPerInfluence;
import com.aggfi.digest.server.botty.digestbotty.admin.GetNextPubPerProject;
import com.aggfi.digest.server.botty.digestbotty.admin.GetPostByActivity;
import com.aggfi.digest.server.botty.digestbotty.admin.GetProjectsPerUser;
import com.aggfi.digest.server.botty.digestbotty.admin.RemoveDigestManager;
import com.aggfi.digest.server.botty.digestbotty.admin.UpdateAtomFeedPublic;
import com.aggfi.digest.server.botty.digestbotty.admin.UpdateSocialBtnsSettings;
import com.aggfi.digest.server.botty.google.forumbotty.admin.AddAutoTag;
import com.aggfi.digest.server.botty.google.forumbotty.admin.AddDefaultParticipant;
import com.aggfi.digest.server.botty.google.forumbotty.admin.AddDefaultTag;
import com.aggfi.digest.server.botty.google.forumbotty.admin.Command;
import com.aggfi.digest.server.botty.google.forumbotty.admin.GetAdminConfig;
import com.aggfi.digest.server.botty.google.forumbotty.admin.GetPostCounts;
import com.aggfi.digest.server.botty.google.forumbotty.admin.GetTagCounts;
import com.aggfi.digest.server.botty.google.forumbotty.admin.RemoveAutoTag;
import com.aggfi.digest.server.botty.google.forumbotty.admin.RemoveDefaultParticipant;
import com.aggfi.digest.server.botty.google.forumbotty.admin.RemoveDefaultTag;


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
  REMOVE_DIGEST_MANAGER(RemoveDigestManager.class),
  ADD_WAVES_PARTICIPANT(AddWavesParticipant.class),
  GET_ALL_FORUMS_IDS(GetAllForumIds.class),
  GET_BLIPS_COUNT(GetBlipsCount.class),
  GET_BLIPS_PER_CONTRIBUTOR(GetBlipsPerContributor.class),
  GET_CONTRIBUTORS_PER_INFLUENCE(GetContributorsPerInfluence.class),
  GET_POST_BY_ACTIVITY(GetPostByActivity.class),
  GET_NEXT_PUB_PER_PROJECT(GetNextPubPerProject.class),
  UPDATE_ATOM_FEED_PUBLIC(UpdateAtomFeedPublic.class),
  CREATE_NEW_POST(CreateNewPost.class),
  ADD_SECURE_POST_GADGET(AddReadOnlyPostGadget.class),
  UPDATE_SOCIAL_BTNS_SETTINGS(UpdateSocialBtnsSettings.class),
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
