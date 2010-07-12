package com.aggfi.digest.server.botty.google.forumbotty.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.aggfi.digest.server.botty.google.forumbotty.model.ForumPost;
import com.google.inject.Inject;
import com.google.wave.api.Wavelet;

public class ForumPostDaoImpl implements ForumPostDao {
  private final Logger LOG = Logger.getLogger(ForumPostDaoImpl.class.getName());

  private PersistenceManagerFactory pmf = null;
  private TagDao tagDao = null;

  @Inject
  public ForumPostDaoImpl(PersistenceManagerFactory pmf, TagDao tagDao) {
    this.pmf = pmf;
    this.tagDao = tagDao;
  }

  @Override
  public ForumPost save(ForumPost entry) {
    PersistenceManager pm = pmf.getPersistenceManager();
    try {
      entry = pm.makePersistent(entry);
      entry = pm.detachCopy(entry);
    } finally {
      pm.close();
    }

    return entry;
  }
  
  @Override
  public int getPostCount(String projectId, Date target) {
    int count = 0;
    
    PersistenceManager pm = pmf.getPersistenceManager();
    
    Date start = new Date(target.getTime());
    start.setHours(0);
    start.setMinutes(0);
    start.setSeconds(0);
    
    Date end = new Date(target.getTime());
    end.setHours(23);
    end.setMinutes(59);
    end.setSeconds(59);
        
    try {
      Query query = pm.newQuery(ForumPost.class);
      query.declareImports("import java.util.Date");
      query.declareParameters("String projectId_, Date start, Date end");
      String filters = "projectId == projectId_ && created >= start && created <= end";      
      query.setFilter(filters);
      List<ForumPost> entries = (List<ForumPost>) query.execute(projectId, start, end);      
      count = entries.size();      
    } finally {
      pm.close();
    }
    return count;
  }
  
  @Override
  public ForumPost getForumPost(String id) {
    PersistenceManager pm = pmf.getPersistenceManager();
    ForumPost entry = null;

    try {
      Query query = pm.newQuery(ForumPost.class);
      query.declareParameters("String id_");
      String filters = "id == id_";
      query.setFilter(filters);
      List<ForumPost> entries = (List<ForumPost>) query.execute(id);
      if (entries.size() > 0) {
        return pm.detachCopy(entries.get(0));
      }
    } finally {
      pm.close();
    }

    return entry;
  }  
  
  @Override
  public ForumPost getForumPost(String domain, String waveId) {
    PersistenceManager pm = pmf.getPersistenceManager();
    ForumPost entry = null;

    try {
      Query query = pm.newQuery(ForumPost.class);
      query.declareParameters("String id_");
      String filters = "id == id_";
      query.setFilter(filters);
      List<ForumPost> entries = (List<ForumPost>) query.execute(domain + "!" + waveId);
      if (entries.size() > 0) {
        return pm.detachCopy(entries.get(0));
      }
    } finally {
      pm.close();
    }

    return entry;
  }

  @Override
  public ForumPost syncTags(String projectId, ForumPost entry, Wavelet wavelet) {
    Set<String> dataStoreSet = entry.getTags();
    Set<String> waveletSet = new HashSet<String>();

    Iterator<String> iterator = wavelet.getTags().iterator();
    while (iterator.hasNext()) {
      waveletSet.add(iterator.next());
    }

    Set<String> difference = null;
    if (waveletSet.size() > dataStoreSet.size()) {
      difference = new HashSet<String>(waveletSet);
      difference.removeAll(dataStoreSet);
    } else {
      difference = new HashSet<String>(dataStoreSet);
      difference.removeAll(waveletSet);
    }

    for (String id : difference) {
      com.aggfi.digest.server.botty.google.forumbotty.model.Tag tag = tagDao.getTag(projectId, id);

      if (dataStoreSet.contains(id)) {
        dataStoreSet.remove(id);
        tag = tagDao.decrementCount(tag);
        tagDao.save(tag);
      } else {
        dataStoreSet.add(id);
        tag = tagDao.incrementCount(tag);
        tagDao.save(tag);
      }
    }

    entry.setTags(dataStoreSet);
    return entry;
  }

  @Override
  public List<ForumPost> getForumPostsByTag(String projectId, String tag, int limit) {
    PersistenceManager pm = pmf.getPersistenceManager();
    List<ForumPost> entries = new ArrayList<ForumPost>();

    try {
      Query query = pm.newQuery(ForumPost.class);
      if(tag != null && !"".equals(tag)){
    	  query.declareParameters("String projectId_, String tag");
          query.setFilter("projectId == projectId_ && tags.contains(tag)");
          entries = (List<ForumPost>) query.execute(projectId, tag);
      }else{
    	  query.declareParameters("String projectId_");
          query.setFilter("projectId == projectId_");
          entries = (List<ForumPost>) query.execute(projectId);
      }

      if (limit > 0) {
        if (entries.size() > limit) {
          entries = entries.subList(0, limit);
        }
      }
      entries = (List<ForumPost>) pm.detachCopyAll(entries);
    } finally {
      pm.close();
    }
    return entries;
  }

  @Override
  public int getTotalCount(String projectId, String tag) {
    PersistenceManager pm = pmf.getPersistenceManager();
    List<ForumPost> entries = new ArrayList<ForumPost>();

    try {
      Query query = pm.newQuery(ForumPost.class);
      query.declareParameters("String projectId_, String tag");
      query.setFilter("projectId == projectId_ && tags.contains(tag)");
      entries = (List<ForumPost>) query.execute(projectId, tag);
      return entries.size();
    } finally {
      pm.close();
    }
  }

  @Override
  public List<ForumPost> getForumPosts(String projectId, int limit) {
    PersistenceManager pm = pmf.getPersistenceManager();
    List<ForumPost> entries = new ArrayList<ForumPost>();

    try {
      Query query = pm.newQuery(ForumPost.class);
      query.declareParameters("String projectId_");
      query.setFilter("projectId == projectId_");

      entries = (List<ForumPost>) query.execute(projectId);
      if (limit > 0) {
        if (entries.size() > limit) {
          entries = entries.subList(0, limit);
        }
      }
      entries = (List<ForumPost>) pm.detachCopyAll(entries);
    } finally {
      pm.close();
    }
    return entries;
  }

  @Override
  public List<ForumPost> getRecentlyUpdated(String projectId, int limit) {
    PersistenceManager pm = pmf.getPersistenceManager();
    List<ForumPost> entries = new ArrayList<ForumPost>();

    try {
      Query query = pm.newQuery(ForumPost.class);
      query.declareImports("import java.util.Date");
      query.setOrdering("lastUpdated desc");
      query.declareParameters("String projectId_");
      query.setFilter("projectId == projectId_");

      entries = (List<ForumPost>) query.execute(projectId);
      if (limit > 0) {
        if (entries.size() > limit) {
          entries = entries.subList(0, limit);
        }
      }
      entries = (List<ForumPost>) pm.detachCopyAll(entries);
    } finally {
      pm.close();
    }
    return entries;
  }
}
