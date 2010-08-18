package com.aggfi.digest.server.botty.digestbotty.dao;


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

import com.aggfi.digest.server.botty.digestbotty.model.BlipSubmitted;
import com.aggfi.digest.server.botty.google.forumbotty.model.ForumPost;
import com.google.inject.Inject;
import com.google.wave.api.Wavelet;

public class BlipSubmitedDaoImpl implements BlipSubmitedDao{
	private final Logger LOG = Logger.getLogger(BlipSubmitedDaoImpl.class.getName());

	private PersistenceManagerFactory pmf = null;

	@Inject
	public BlipSubmitedDaoImpl(PersistenceManagerFactory pmf) {
		this.pmf = pmf;
	}

	@Override
	public BlipSubmitted save(BlipSubmitted entry) {
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			entry = pm.makePersistent(entry);
			entry = pm.detachCopy(entry);
		}catch(Exception e){
			LOG.warning(e.getMessage());
		}
		finally {
			pm.close();
		}

		return entry;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<BlipSubmitted> getBlipsDuringDate(String projectId, Date target) {
		return getBlipsDuringPeriod(projectId,target,target);
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<BlipSubmitted> getBlipsFromDate(String projectId, Date target) {
		int count = 0;

		PersistenceManager pm = pmf.getPersistenceManager();

		Date start = new Date(target.getTime());
		start.setHours(0);
		start.setMinutes(0);
		start.setSeconds(0);

		List<BlipSubmitted> entries = new ArrayList<BlipSubmitted>();
		try {
			Query query = pm.newQuery(BlipSubmitted.class);
			//	      query.declareImports("import java.util.Date");
			query.declareParameters("String projectId_, long start");
			String filters = "projectId == projectId_ && createdTime >= start";      
			query.setFilter(filters);
			entries = (List<BlipSubmitted>) query.execute(projectId, start.getTime());   
			count = entries.size();     
			LOG.info("getBlipsFromDate. projectId: " + projectId + ", count:" + count);
		} finally {
			pm.close();
		}
		return entries;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public BlipSubmitted getBlipByWaveIdBlipIdVersion(String projectId, String waveId, String blipId, long version) {

		PersistenceManager pm = pmf.getPersistenceManager();


		List<BlipSubmitted> entries = new ArrayList<BlipSubmitted>();
		try {
			Query query = pm.newQuery(BlipSubmitted.class);
			query.declareParameters("String waveId_, String blipId_, long version_, String projectId_");
			
			String filters = "waveId == waveId_ && blipId == blipId_ && version == version_ && projectId == projectId_";    
			if(version < 0){
				filters = "waveId == waveId_ && blipId == blipId_ && version > -1 && projectId == projectId_";  
			}
			query.setFilter(filters);
			query.setOrdering("version desc");
			entries = (List<BlipSubmitted>) query.execute(waveId, blipId, version);   
			LOG.info("getBlipByWaveIdBlipIdVersion. waveId: " + waveId + ", blipId:" + blipId + ", version: " + version);
		} finally {
			pm.close();
		}
		if(entries.size() > 1){
			LOG.warning("more than 1 BlipSubmitted with same String waveId_, String blipId_, long version_, String projectId_" + entries.get(0).toString());
		}
		if(entries.size() > 0) 
			return entries.get(0);
		else return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BlipSubmitted> getBlipsDuringPeriod(String projectId, Date fromDate, Date toDate) {

		PersistenceManager pm = pmf.getPersistenceManager();

		Date start = new Date(fromDate.getTime());
		start.setHours(0);
		start.setMinutes(0);
		start.setSeconds(0);

		Date end = new Date(toDate.getTime());
		end.setHours(23);
		end.setMinutes(59);
		end.setSeconds(59);
		List<BlipSubmitted> entries = new ArrayList<BlipSubmitted>();
		try {
			Query query = pm.newQuery(BlipSubmitted.class);
			//	      query.declareImports("import java.util.Date");
			query.declareParameters("String projectId_, long start, long end");
			String filters = "projectId == projectId_ && createdTime >= start && createdTime <= end";      
			query.setFilter(filters);
			entries = (List<BlipSubmitted>) query.execute(projectId, start.getTime(), end.getTime());      
		} finally {
			pm.detachCopyAll(entries);
			pm.close();
		}
		return entries;
	}
}

