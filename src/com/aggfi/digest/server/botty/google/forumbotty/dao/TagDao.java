package com.aggfi.digest.server.botty.google.forumbotty.dao;

import java.util.List;

import com.aggfi.digest.server.botty.google.forumbotty.model.Tag;


public interface TagDao {

  public abstract Tag incrementCount(Tag tag);

  public abstract Tag decrementCount(Tag tag);

  public abstract Tag getTag(String projectId, String tagString);

  public abstract Tag save(Tag entry);

  List<Tag> getTagCounts(String projectId, boolean filterDefault);

}