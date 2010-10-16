package com.aggfi.digest.server.botty.google.forumbotty.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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
    	LOG.info("Saving forumPost before: " + entry.toString());
    	Long primaryKey = null;
    	if(entry.getPrimaryKey() == 0){
    		primaryKey = System.currentTimeMillis();
    		entry.setPrimaryKey(primaryKey);
    	}
    	String uuid = null;
    	if(entry.getRealId() == null){
    		uuid = UUID.randomUUID().toString();
    		entry.setId(uuid);
    	}
    	LOG.info("Saving forumPost after: " + entry.toString() + ", ### prinaryKey = " + primaryKey + ", uuid: " + uuid);
      try{
    	  pm.makePersistent(entry);
    	  entry = pm.detachCopy(entry);
      }catch(Exception e){
    	  LOG.warning("Exception: " + e.getMessage() + ", entry: " + entry.toString()  );
      }
    } finally {
      pm.close();
    }

    return entry;
  }
  
  @Override
  public ForumPost remove(ForumPost entry) {
    PersistenceManager pm = pmf.getPersistenceManager();
    try {
     pm.deletePersistent(entry);
      try{
    	  entry = pm.detachCopy(entry);
      }catch(Exception e){}
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
      entries = filterNonActive(entries);
      count = entries.size();      
    } finally {
      pm.close();
    }
    return count;
  }
  
  @Override
  public ForumPost getForumPost(String id) {
    String[] split = id.split("!");
    return getForumPost(split[0], split[1]);
  }
  
  
  

private ForumPost prepare4Ret(PersistenceManager pm, List<ForumPost> entries, int i) {
	ForumPost entry;
	entry = entries.get(i);
	  try{
		  if(entry.isDispayAtom() == null){
			  entry.setDispayAtom(true);
			  pm.makePersistent(entry);
		  }
	  }catch(Exception e){}
	 
	 try{
		 entry = pm.detachCopy(entry);
	 }catch(Exception e){}
	return entry;
}  
  
  @Override
  public ForumPost getForumPost(String domain, String waveId) {
    PersistenceManager pm = pmf.getPersistenceManager();
    ForumPost entry = null;

    try {
      Query query = pm.newQuery(ForumPost.class);
      query.declareParameters("String domain_, String waveId_");
      String filters = "domain == domain_ && waveId == waveId_";
      query.setFilter(filters);
      List<ForumPost> entries = (List<ForumPost>) query.execute(domain, waveId);
      entries = filterNonActive(entries);
      entries = (List<ForumPost>) pm.detachCopyAll(entries);
      if (entries.size() > 0) {
    	  entry = prepare4Ret(pm, entries, 0);
        return entry;
      }
    } finally {
      pm.close();
    }

    return entry;
  }
  
  @Override
  public ForumPost getForumPost(String domain, String waveId, String projectId) {
    PersistenceManager pm = pmf.getPersistenceManager();
    ForumPost entry = null;

    try {
      Query query = pm.newQuery(ForumPost.class);
      query.declareParameters("String domain_, String waveId_, String projectId_");
      String filters = "domain == domain_ && waveId == waveId_ && projectId == projectId_";
      query.setFilter(filters);
      List<ForumPost> entries = (List<ForumPost>) query.execute(domain, waveId,projectId);
      entries = (List<ForumPost>) pm.detachCopyAll(entries);
      entries = filterNonActive(entries);
      if (entries.size() > 0) {
    	  entry = prepare4Ret(pm, entries, 0);
        return entry;
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

  @SuppressWarnings("unchecked")
@Override
  public List<ForumPost> getForumPostsByTag(String projectId, String tag, int limit) {
    PersistenceManager pm = pmf.getPersistenceManager();
    List<ForumPost> entries = new ArrayList<ForumPost>();
    List<ForumPost> entries4Ret = new ArrayList<ForumPost>();
    
    try {
      Query query = pm.newQuery(ForumPost.class);
      if(tag != null && !"".equals(tag)){
    	  query.declareParameters("String projectId_, String tag");
          query.setFilter("projectId == projectId_ && tags.contains(tag) ");
          entries = (List<ForumPost>) query.execute(projectId, tag);
          entries = filterNonActive(entries);
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
      
      int i = 0;
      for(ForumPost entry2Ret : entries){
    	  entry2Ret = prepare4Ret(pm, entries, i);
    	  entries4Ret.add(entry2Ret);
    	  i++;
      }
    } finally {
      pm.close();
    }
    return entries4Ret;
  }

  @Override
  public int getTotalCount(String projectId, String tag) {
    PersistenceManager pm = pmf.getPersistenceManager();
    List<ForumPost> entries = new ArrayList<ForumPost>();

    try {
      Query query = pm.newQuery(ForumPost.class);
      query.declareParameters("String projectId_, String tag");
      query.setFilter("projectId == projectId_ && tags.contains(tag) ");
      entries = (List<ForumPost>) query.execute(projectId, tag);
      entries = filterNonActive(entries);
      return entries.size();
    } finally {
      pm.close();
    }
  }

  @Override
  public List<ForumPost> getForumPosts(String projectId, int limit) {
    PersistenceManager pm = pmf.getPersistenceManager();
    List<ForumPost> entries = new ArrayList<ForumPost>();
    List<ForumPost> entries4Ret = new ArrayList<ForumPost>();
    try {
      Query query = pm.newQuery(ForumPost.class);
      query.declareParameters("String projectId_");
      query.setFilter("projectId == projectId_ ");

      entries = (List<ForumPost>) query.execute(projectId);
      entries = filterNonActive(entries);
      if (limit > 0) {
        if (entries.size() > limit) {
          entries = entries.subList(0, limit);
        }
      }
      entries = (List<ForumPost>) pm.detachCopyAll(entries);
      
      int i = 0;
      for(ForumPost entry2Ret : entries){
    	  entry2Ret = prepare4Ret(pm, entries, i);
    	  i++;
    	  entries4Ret.add(entry2Ret);
      }
    } finally {
      pm.close();
    }
    return entries4Ret;
  }
  
@Override
  public List<ForumPost> getForumPostsFromDate(String projectId, Date fromDate) {
	    PersistenceManager pm = pmf.getPersistenceManager();
	    List<ForumPost> entries = new ArrayList<ForumPost>();
	    List<ForumPost> entries4Ret = new ArrayList<ForumPost>();
	    try {
	      Query query = pm.newQuery(ForumPost.class);
	      query.declareImports("import java.util.Date");
	      query.declareParameters("String projectId_, Date fromDate_");
	      query.setFilter("projectId == projectId_ && lastUpdated >= fromDate_ ");

	      entries = (List<ForumPost>) query.execute(projectId,fromDate);
	      entries = filterNonActive(entries);
	      for(ForumPost p : entries){
	    	  entries4Ret.add(pm.detachCopy(p));
	      }
	      
	    } finally {
	      pm.close();
	    }
	    return entries4Ret;
	  }

  @Override
  public List<ForumPost> getRecentlyUpdated(String projectId, int limit) {
    PersistenceManager pm = pmf.getPersistenceManager();
    List<ForumPost> entries = new ArrayList<ForumPost>();
    List<ForumPost> entries4Ret = new ArrayList<ForumPost>();
    try {
      Query query = pm.newQuery(ForumPost.class);
      query.declareImports("import java.util.Date");
      query.setOrdering("lastUpdated desc");
      query.declareParameters("String projectId_");
      query.setFilter("projectId == projectId_ ");

      entries = (List<ForumPost>) query.execute(projectId);
      entries = filterNonActive(entries);
      if (limit > 0) {
        if (entries.size() > limit) {
          entries = entries.subList(0, limit);
        }
      }
      entries = (List<ForumPost>) pm.detachCopyAll(entries);
      for(ForumPost entry2Ret : entries){
    	  if(entry2Ret.isDispayAtom() == null){
    		  entry2Ret.setDispayAtom(true);
			 try{
				 pm.makePersistent(entry2Ret);
				 entry2Ret = pm.detachCopy(entry2Ret);
			 }catch(Exception e){}
		  }
    	  entries4Ret.add(entry2Ret);
      }
      
    } finally {
      pm.close();
    }
    return entries4Ret;
  }

	private List<ForumPost> filterNonActive(List<ForumPost> entries) {
		List<ForumPost> filteredEntries = new ArrayList<ForumPost>();
		for(ForumPost entry : entries){
			if(entry.isActive() == null || entry.isActive() == true ){
				filteredEntries.add(entry);
			}
		}
		return filteredEntries;
	}
}
