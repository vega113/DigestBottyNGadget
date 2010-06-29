package com.aggfi.digest.server.botty.google.forumbotty.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.aggfi.digest.server.botty.google.forumbotty.model.Tag;
import com.google.inject.Inject;

public class TagDaoImpl implements TagDao {
  private final Logger LOG = Logger.getLogger(TagDaoImpl.class.getName());

  private PersistenceManagerFactory pmf = null;
  private AdminConfigDao adminConfigDao = null;

  @Inject
  public TagDaoImpl(PersistenceManagerFactory pmf, AdminConfigDao adminConfigDao) {
    this.pmf = pmf;
    this.adminConfigDao = adminConfigDao;
  }
  
  @Override
  public Tag incrementCount(Tag tag) {
    if (tag != null) {
      tag.setCount(tag.getCount() + 1);
    }
    return tag;
  }
  
  @Override
  public Tag decrementCount(Tag tag) {
    if (tag != null && tag.getCount() > 0) {
      tag.setCount(tag.getCount() - 1);
    }
    return tag;
  }
  
  @Override
  public Tag getTag(String projectId, String tagString) {
    PersistenceManager pm = pmf.getPersistenceManager();
    Tag entry = null;

    try {
      String id = projectId + "__" + tagString;
      Query query = pm.newQuery(Tag.class);
      query.declareParameters("String id_");
      String filters = "id == id_";
      query.setFilter(filters);
      List<Tag> entries = (List<Tag>) query.execute(id);
      if (entries.size() > 0) {
        return pm.detachCopy(entries.get(0));
      }
    } finally {
      pm.close();
    }

    if (entry == null) {
      entry = new Tag(projectId, tagString);
      entry = save(entry);
    }

    return entry;
  }

  @Override
  public List<Tag> getTagCounts(String projectId, boolean filterDefault) {
    PersistenceManager pm = pmf.getPersistenceManager();
    List<Tag> entries = new ArrayList<Tag>();
    try {
      Query query = pm.newQuery(Tag.class);
      query.declareParameters("String projectId_");      
      String filters = "projectId == projectId_";
      query.setFilter(filters);
      query.setOrdering("count asc");
      entries = (List<Tag>) query.execute(projectId);
      entries = (List<Tag>) pm.detachCopyAll(entries);                  
      
      if (filterDefault) {
        List<Tag> filteredList = new ArrayList<Tag>();
        List<String> defaultTags = adminConfigDao.getAdminConfig(projectId).getDefaultTags();
        for (Tag tag : entries) {
          if (!defaultTags.contains(tag.getTag())) {
            filteredList.add(tag);
          }
        }
        entries = filteredList;
      }    
    } finally {
      pm.close();
    }
    return entries;
  }  
  
  @Override
  public Tag save(Tag entry) {
    PersistenceManager pm = pmf.getPersistenceManager();
    try {
      entry = pm.makePersistent(entry);
      entry = pm.detachCopy(entry);
    } finally {
      pm.close();
    }

    return entry;
  }
}