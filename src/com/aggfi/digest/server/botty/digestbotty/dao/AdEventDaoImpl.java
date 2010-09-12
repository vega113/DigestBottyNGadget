package com.aggfi.digest.server.botty.digestbotty.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.aggfi.digest.server.botty.digestbotty.model.AdEvent;
import com.google.inject.Inject;

public class AdEventDaoImpl implements AdEventDao {
	private final Logger LOG = Logger.getLogger(AdEventDaoImpl.class.getName());

	private PersistenceManagerFactory pmf = null;

	@Inject
	public AdEventDaoImpl(PersistenceManagerFactory pmf) {
		this.pmf = pmf;
	}

	@Override
	public AdEvent save(AdEvent entry) {
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
	public List<AdEvent> getEventsDuringDate(String projectId, Date target, String viewPostEventType) {
		return getEventsDuringPeriod(projectId,target,target,viewPostEventType);
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<AdEvent> getEventsDuringPeriod(String projectId, Date fromDate, Date toDate, String viewPostEventType) {
		PersistenceManager pm = pmf.getPersistenceManager();

		Date start = new Date(fromDate.getTime());
		start.setHours(0);
		start.setMinutes(0);
		start.setSeconds(0);

		Date end = new Date(toDate.getTime());
		end.setHours(23);
		end.setMinutes(59);
		end.setSeconds(59);
		List<AdEvent> entries = new ArrayList<AdEvent>();
		List<AdEvent> entriesCopy =  new ArrayList<AdEvent>();
		try {
			Query query = pm.newQuery(AdEvent.class);
				      query.declareImports("import java.util.Date");
			query.declareParameters("String projectId_, Date start, Date end, String eventType_");
			String filters = "projectId == projectId_ && created >= start && created <= end";      
			query.setFilter(filters);
			entries = (List<AdEvent>) query.execute(projectId, start, end);     
			entriesCopy = new ArrayList<AdEvent>();
			pm.detachCopyAll(entries);
			for(AdEvent entry : entries){
				entriesCopy.add(pm.detachCopy(entry));
			}
		} finally {
			pm.close();
		}
		return entriesCopy;
	}

}


