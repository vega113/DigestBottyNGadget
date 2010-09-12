package com.aggfi.digest.server.botty.digestbotty.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.aggfi.digest.server.botty.digestbotty.admin.GetViewsCount;
import com.aggfi.digest.server.botty.digestbotty.model.TrackerEvent;
import com.aggfi.digest.server.botty.digestbotty.model.TrackerEvent;
import com.google.inject.Inject;

public class TrackerEventDaoImpl implements TrackerEventDao {
	private final Logger LOG = Logger.getLogger(TrackerEventDaoImpl.class.getName());

	private PersistenceManagerFactory pmf = null;

	@Inject
	public TrackerEventDaoImpl(PersistenceManagerFactory pmf) {
		this.pmf = pmf;
	}

	@Override
	public TrackerEvent save(TrackerEvent entry) {
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
	public List<TrackerEvent> getEventsDuringDate(String projectId, Date target, String viewPostEventType) {
		return getEventsDuringPeriod(projectId,target,target,viewPostEventType);
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<TrackerEvent> getEventsDuringPeriod(String projectId, Date fromDate, Date toDate, String viewPostEventType) {
		PersistenceManager pm = pmf.getPersistenceManager();

		Date start = new Date(fromDate.getTime());
		start.setHours(0);
		start.setMinutes(0);
		start.setSeconds(0);

		Date end = new Date(toDate.getTime());
		end.setHours(23);
		end.setMinutes(59);
		end.setSeconds(59);
		List<TrackerEvent> entries = new ArrayList<TrackerEvent>();
		List<TrackerEvent> entriesCopy =  new ArrayList<TrackerEvent>();
		try {
			Query query = pm.newQuery(TrackerEvent.class);
				      query.declareImports("import java.util.Date");
			query.declareParameters("String projectId_, Date start, Date end");
			String filters = "projectId == projectId_ && created >= start && created <= end";      
			query.setFilter(filters);
			entries = (List<TrackerEvent>) query.execute(projectId, start, end);     
			entriesCopy = new ArrayList<TrackerEvent>();
			pm.detachCopyAll(entries);
			for(TrackerEvent entry : entries){
				entriesCopy.add(pm.detachCopy(entry));
			}
		} finally {
			pm.close();
		}
		return entriesCopy;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<TrackerEvent> getTrackerEventsByWaveId(String waveId) {
		PersistenceManager pm = pmf.getPersistenceManager();
		List<TrackerEvent> entries = new ArrayList<TrackerEvent>();
		List<TrackerEvent> entries4Return = new ArrayList<TrackerEvent>();
		try {
			Query query = pm.newQuery(TrackerEvent.class);
			query.declareParameters("String waveId_");
			String filters = "waveId == waveId_";      
			query.setFilter(filters);
			entries = (List<TrackerEvent>) query.execute(waveId);     
			pm.detachCopyAll(entries);
			for(TrackerEvent entry4Return : entries){
				entry4Return = pm.detachCopy(entry4Return);
				entries4Return.add(entry4Return);
			}
		} finally {
			pm.close();
		}
		return entries4Return;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<TrackerEvent> getTrackerEventsFromDate(String projectId, Date target) {
		int count = 0;

		PersistenceManager pm = pmf.getPersistenceManager();

		Date start = new Date(target.getTime());
		start.setHours(0);
		start.setMinutes(0);
		start.setSeconds(0);

		List<TrackerEvent> entries = new ArrayList<TrackerEvent>();
		try {
			Query query = pm.newQuery(TrackerEvent.class);
			query.declareImports("import java.util.Date");
			query.declareParameters("String projectId_, Date start");
			String filters = "projectId == projectId_ && created >= start";      
			query.setFilter(filters);
			entries = (List<TrackerEvent>) query.execute(projectId, start);   
			count = entries.size();     
			LOG.info("getTrackerEventsDate. projectId: " + projectId + ", count:" + count);
		} finally {
			pm.close();
		}
		return entries;
	}

}


