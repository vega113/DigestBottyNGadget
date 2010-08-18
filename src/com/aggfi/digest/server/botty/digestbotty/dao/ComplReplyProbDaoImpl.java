package com.aggfi.digest.server.botty.digestbotty.dao;



import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.aggfi.digest.server.botty.digestbotty.model.BlipSubmitted;
import com.aggfi.digest.server.botty.digestbotty.model.ComplReplyProb;
import com.aggfi.digest.server.botty.digestbotty.model.Influence;
import com.aggfi.digest.server.botty.digestbotty.utils.InfluenceUtils;
import com.google.inject.Inject;

public class ComplReplyProbDaoImpl implements ComplReplyProbDao {
	private final Logger LOG = Logger.getLogger(ComplReplyProbDaoImpl.class.getName());

	private PersistenceManagerFactory pmf = null;

	@Inject
	public ComplReplyProbDaoImpl(PersistenceManagerFactory pmf) {
		this.pmf = pmf;
	}

	@Override
	public ComplReplyProb save(ComplReplyProb entry) {
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
	
	@Override
	@SuppressWarnings("unchecked")
	public ComplReplyProb getComplReplyProb(String projectId, Date forDate) {

		PersistenceManager pm = pmf.getPersistenceManager();


		List<ComplReplyProb> entries = new ArrayList<ComplReplyProb>();
		try {
			Query query = pm.newQuery(ComplReplyProb.class);
			String forDateStr_ = InfluenceUtils.getSdf().format(forDate);
			query.declareParameters("String projectId_, String forDateStr_");
			
			String filters = "projectId == projectId_ && forDateStr == forDateStr_";    
			query.setFilter(filters);
			entries = (List<ComplReplyProb>) query.execute(projectId, forDateStr_);   
		} finally {
			pm.detachCopyAll(entries);
			pm.close();
		}
		if(entries.size() > 1){
			LOG.warning("more than 1 ComplReplyProb with same projectId_ and forDate" + entries.get(0).toString());
		}
		if(entries.size() > 0) 
			return entries.get(0);
		else return null;
	}

}

