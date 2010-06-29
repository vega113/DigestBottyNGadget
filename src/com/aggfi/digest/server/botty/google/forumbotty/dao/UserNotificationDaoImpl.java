package com.aggfi.digest.server.botty.google.forumbotty.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.aggfi.digest.server.botty.google.forumbotty.model.UserNotification;
import com.aggfi.digest.server.botty.google.forumbotty.model.UserNotification.NotificationType;
import com.google.inject.Inject;

public class UserNotificationDaoImpl implements UserNotificationDao {
	private final Logger LOG = Logger.getLogger(UserNotificationDaoImpl.class.getName());

	private PersistenceManagerFactory pmf = null;

	@Inject
	public UserNotificationDaoImpl(PersistenceManagerFactory pmf) {
		this.pmf = pmf;
	}


	@Override
	public UserNotification getUserNotification(String id) {
		PersistenceManager pm = pmf.getPersistenceManager();

		try {
			Query query = pm.newQuery(UserNotification.class);
			query.declareParameters("String id_");
			String filters = "id == id_";
			query.setFilter(filters);
			List<UserNotification> users = (List<UserNotification>) query.execute(id);
			if (users.size() > 0) {
				return pm.detachCopy(users.get(0));
			}
		} finally {
			pm.close();
		}

		return null;
	}

	@Override
	public UserNotification save(UserNotification entry) {
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
	public List<UserNotification> getAllUserNotifications(String projectId, NotificationType type) {
		PersistenceManager pm = pmf.getPersistenceManager();
		List<UserNotification> entries = new ArrayList<UserNotification>();

		try {
			Query query = pm.newQuery(UserNotification.class);
			query.declareParameters("String projectId_, String notificationType_");
			String filters = "projectId == projectId_ && notificationType == notificationType_";
			query.setFilter(filters);
			entries = (List<UserNotification>) query.execute(projectId, type.toString());
			entries = (List<UserNotification>) pm.detachCopyAll(entries);
		} finally {
			pm.close();
		}
		return entries;
	}

}
