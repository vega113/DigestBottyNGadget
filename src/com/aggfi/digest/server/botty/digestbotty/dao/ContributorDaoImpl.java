package com.aggfi.digest.server.botty.digestbotty.dao;



import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.aggfi.digest.server.botty.digestbotty.model.BlipSubmitted;
import com.aggfi.digest.server.botty.digestbotty.model.Contributor;
import com.google.inject.Inject;

public class ContributorDaoImpl implements ContributorDao{
	private final Logger LOG = Logger.getLogger(ContributorDaoImpl.class.getName());

	private PersistenceManagerFactory pmf = null;

	@Inject
	public ContributorDaoImpl(PersistenceManagerFactory pmf) {
		this.pmf = pmf;
	}

	@Override
	public Contributor save(Contributor entry) {
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
	public Contributor get(String participantId) {
		PersistenceManager pm = pmf.getPersistenceManager();
		Contributor entry = null;
		try{
			try{
				entry = pm.getObjectById(Contributor.class, participantId);
			}catch(Exception npe){
				LOG.warning(npe.getMessage());
			};
			if(entry == null){
				entry = new Contributor(participantId);
				entry.setProps(new HashMap<String, Object>());
				try {
					entry = pm.makePersistent(entry);
					entry = pm.detachCopy(entry);
				}catch(Exception e){
					LOG.warning(e.getMessage());
				}
			}
		}catch(Exception e){}
		finally {
			pm.close();
		}

		return entry;
	}
	
	
	
}


