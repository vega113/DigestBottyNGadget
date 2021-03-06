package com.aggfi.digest.server.botty.digestbotty.dao;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.aggfi.digest.server.botty.digestbotty.admin.CreateDigest;
import com.aggfi.digest.server.botty.digestbotty.model.ExtDigest;
import com.aggfi.digest.server.botty.google.forumbotty.dao.DigestDaoImpl;
import com.aggfi.digest.server.botty.google.forumbotty.model.AdminConfig;
import com.aggfi.digest.server.botty.google.forumbotty.model.ForumPost;
import com.google.inject.Inject;

public class ExtDigestDaoImpl  extends DigestDaoImpl implements ExtDigestDao{
	private static final Logger LOG = Logger.getLogger(CreateDigest.class.getName());
	protected PersistenceManagerFactory pmf;

	@Inject
	public ExtDigestDaoImpl(PersistenceManagerFactory pmf) {
		super(pmf);
		this.pmf = pmf;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ExtDigest> retrDigestsByProjectId(String projectId) {
		 PersistenceManager pm = pmf.getPersistenceManager();
		 List<ExtDigest> extDigests = null;
		    try {
		      Query query = pm.newQuery(ExtDigest.class);
		      query.declareParameters("String projectId_");
		      query.setFilter("projectId == projectId_");
		      extDigests = (List<ExtDigest>) query.execute(projectId);
		      if (extDigests.size() > 0) {
		    	  if(extDigests.size() > 1){
		    		  LOG.severe("Oops! more than one Digest for this project id: " + projectId);
		    	  }else if(extDigests.size() == 0){
		    		  LOG.severe("Oops! no Digest for this project id: " + projectId);
		    	  }
		    	  extDigests = (List<ExtDigest>) pm.detachCopyAll(extDigests);
		      } else {
//		    	  LOG.log(Level.SEVERE, "There's no Digest Wave for project id: " + projectId);
		      }
		    }catch(Exception e){
		    	LOG.log(Level.SEVERE, "",e);
		    }
		    finally {
		      pm.close();
		    }
		return extDigests;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ExtDigest> retrDigestsByOwnerId(String projectId) {
		PersistenceManager pm = pmf.getPersistenceManager();
		 List<ExtDigest> extDigests = null;
		    try {
		      Query query = pm.newQuery(ExtDigest.class);
		      query.declareParameters("String ownerId_");
		      query.setFilter("ownerId == ownerId_");
		      extDigests = (List<ExtDigest>) query.execute(projectId);
		    }
		    finally {
//		      pm.close();
		    }
		return extDigests;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ExtDigest> retrDigestsByOwnerOrManagerId(String userId) {
		PersistenceManager pm = pmf.getPersistenceManager();
		List<AdminConfig> adminConfigs = null;
		List<ExtDigest> extDigests = new LinkedList<ExtDigest>();
		try {
			if(userId.equals("vega113@googlewave.com")){
				Query query1 = pm.newQuery(ExtDigest.class);
				extDigests = new LinkedList((List<ExtDigest>) query1
						.execute());
			}else{
				Query query = pm.newQuery(AdminConfig.class);
				query.declareParameters("String userId");
				query.setFilter("managers.contains(userId)");
				adminConfigs = (List<AdminConfig>) query.execute(userId);
				List projectIds = new LinkedList<String>();
				for(AdminConfig adminConfig : adminConfigs){
					projectIds.add(adminConfig.getId());
				}
				
				Query query1 = pm.newQuery(ExtDigest.class);
				query1.declareImports("import java.util.List");
				query1.declareParameters("List projectIds");
				query1.setFilter("projectIds.contains(projectId)");
				if (projectIds.size() > 0) {
					extDigests = new LinkedList((List<ExtDigest>) query1
							.execute(projectIds));
				}
				List<ExtDigest> extDigestsOwnerList = retrDigestsByOwnerId(userId);
				for(ExtDigest d : extDigestsOwnerList){
					if(!extDigests.contains(d)){
						extDigests.add(d);
					}
				}
			}
		}
		finally {
					      pm.close();
		}
		
		return extDigests;

	}
	
	@Override
	  public ExtDigest retrDigestById(String id) {
	    PersistenceManager pm = pmf.getPersistenceManager();
	    ExtDigest entry = null;

	    try {
	      Query query = pm.newQuery(ExtDigest.class);
	      query.declareParameters("String id_");
	      String filters = "id == id_";
	      query.setFilter(filters);
	      List<ExtDigest> entries = (List<ExtDigest>) query.execute( id);
	      entries = (List<ExtDigest>) pm.detachCopyAll(entries);
	      if (entries.size() > 0) {
	    	  entry = pm.detachCopy(entries.get(0));
	        return entry;
	      }
	    } finally {
	      pm.close();
	    }

	    return entry;
	  }
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ExtDigest> retrAllDigests() {
		PersistenceManager pm = pmf.getPersistenceManager();
		List<ExtDigest> extDigests = new LinkedList<ExtDigest>();
		Query query1 = pm.newQuery(ExtDigest.class);
		try {
			extDigests = (List<ExtDigest>) query1.execute();
			extDigests = (List<ExtDigest>) pm.detachCopyAll(extDigests);
		} finally {
			pm.close();
		}
		return extDigests;
	}
}
