package com.aggfi.digest.server.botty.google.forumbotty.dao;

import java.util.Date;
import java.util.List;

import com.aggfi.digest.server.botty.google.forumbotty.model.ForumPost;
import com.google.wave.api.Wavelet;

public interface ForumPostDao {

  public abstract ForumPost save(ForumPost entry);

  public abstract ForumPost getForumPost(String id);
  
  public abstract ForumPost getForumPost(String domain, String waveId);

  public abstract List<ForumPost> getRecentlyUpdated(String projectId, int limit);

  public abstract int getTotalCount(String projectId, String tag);

  public abstract List<ForumPost> getForumPosts(String projectId, int limit);

  public abstract List<ForumPost> getForumPostsByTag(String projectId, String tag, int limit);

  public abstract ForumPost syncTags(String projectId, ForumPost entry, Wavelet wavelet);

  int getPostCount(String projectId, Date target);

}
