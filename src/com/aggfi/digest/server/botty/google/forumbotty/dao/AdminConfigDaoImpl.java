package com.aggfi.digest.server.botty.google.forumbotty.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.aggfi.digest.server.botty.google.forumbotty.model.AdminConfig;
import com.google.inject.Inject;

public class AdminConfigDaoImpl implements AdminConfigDao {
	private final Logger LOG = Logger.getLogger(AdminConfigDaoImpl.class.getName());
	private PersistenceManagerFactory pmf = null;

	@Inject
	public AdminConfigDaoImpl(PersistenceManagerFactory pmf) {
		this.pmf = pmf;
	}

	@SuppressWarnings("unchecked")
	@Override
	public AdminConfig getAdminConfig(String id) {
		AdminConfig adminConfig = null;

		PersistenceManager pm = pmf.getPersistenceManager();

		try {
			Query query = pm.newQuery(AdminConfig.class);
			query.declareParameters("String id_");
			query.setFilter("id == id_");
			List<AdminConfig> adminConfigs = (List<AdminConfig>) query.execute(id);
			if (adminConfigs.size() > 0) {
				adminConfig = pm.detachCopy(adminConfigs.get(0));
				boolean isForumPublic = isForumPublicByGroup(adminConfig);
				
				boolean isUpdate = false;
				if(adminConfig.isAtomFeedPublic() == null){
					adminConfig.setAtomFeedPublic(isForumPublic);
					isUpdate = true;
				}
				
				if(adminConfig.isDiggBtnEnabled() == null){
					adminConfig.setDiggBtnEnabled(isForumPublic);
					isUpdate = true;
				}
				if(adminConfig.isDiggBtnEnabled() == null){
					adminConfig.setDiggBtnEnabled(isForumPublic);
					isUpdate = true;
				}
				if(adminConfig.isBuzzBtnEnabled() == null){
					adminConfig.setBuzzBtnEnabled(isForumPublic);
					isUpdate = true;
				}
				if(adminConfig.isTweetBtnEnabled() == null){
					adminConfig.setTweetBtnEnabled(isForumPublic);
					isUpdate = true;
				}
				if(adminConfig.isFaceBtnEnabled() == null){
					adminConfig.setFaceBtnEnabled(isForumPublic);
					isUpdate = true;
				}
				if(isUpdate == true){
					pm.makePersistent(adminConfig);
					adminConfig = pm.detachCopy(adminConfig);
				}
				
			} else {
				adminConfig = new AdminConfig(id);

				List<String> defaultParticipants = new ArrayList<String>();
				adminConfig.setDefaultParticipants(defaultParticipants);

				// Each new AdminConfig by default has its own id as the default tag
				List<String> defaultTags = new ArrayList<String>();
//				defaultTags.add(id);// I want to make default tags obsolete, will be substituted by auto tags
				adminConfig.setDefaultTags(defaultTags);

				List<String> managers = new ArrayList<String>();
				adminConfig.setManagers(managers);

				pm.makePersistent(adminConfig);
				adminConfig = pm.detachCopy(adminConfig);
			}
		} finally {
			pm.close();
		}

		return adminConfig;
	}

	public boolean isForumPublicByGroup(AdminConfig adminConfig) {
		return adminConfig.getDefaultParticipants().contains("public@a.gwave.com") || adminConfig.getDefaultParticipants().contains(System.getProperty("PUBLIC_GROUP"));
	}

	@Override
	public void addAutoTagRegex(String projectId, String tag, String regex) {
		AdminConfig adminConfig = getAdminConfig(projectId);
		adminConfig.getAutoTagRegexMap().put(tag, Pattern.compile(regex, Pattern.CASE_INSENSITIVE));
		save(adminConfig);
	}

	@Override
	public void removeAutoTagRegex(String projectId, String tag) {
		AdminConfig adminConfig = getAdminConfig(projectId);
		Map<String, Pattern> map = adminConfig.getAutoTagRegexMap();

		if (map.containsKey(tag)) {
			map.remove(tag);
			adminConfig.setAutoTagRegexMap(map);
			save(adminConfig);
		}
	}

	@Override
	public void addDefaultTag(String projectId, String tag) {
		AdminConfig adminConfig = getAdminConfig(projectId);
		List<String> tags = adminConfig.getDefaultTags();

		if (!tags.contains(tag)) {
			tags.add(tag);
			adminConfig.setDefaultTags(tags);
			save(adminConfig);
		}
	}

	@Override
	public void removeDefaultTag(String projectId, String tag) {
		AdminConfig adminConfig = getAdminConfig(projectId);
		List<String> tags = adminConfig.getDefaultTags();

		if (tags.contains(tag)) {
			tags.remove(tag);
			adminConfig.setDefaultTags(tags);
			save(adminConfig);
		}
	}

	@Override
	public void addDefaultParticipant(String projectId, String participantId) {
		AdminConfig adminConfig = getAdminConfig(projectId);
		List<String> participants = adminConfig.getDefaultParticipants();

		if (!participants.contains(participantId)) {
			participants.add(participantId);
			adminConfig.setDefaultParticipants(participants);
			save(adminConfig);
		}else{
			throw new IllegalArgumentException(participantId + " is already added to Default Participants!");
		}
	}

	@Override
	public void addDigestManager(String projectId, String managerId) {
		AdminConfig adminConfig = getAdminConfig(projectId);
		List<String> managers = adminConfig.getManagers();

		if (!managers.contains(managerId)) {
			managers.add(managerId);
			adminConfig.setDefaultParticipants(managers);
			save(adminConfig);
		}
	}

	@Override
	public void removeDefaultParticipant(String projectId, String participantId) {
		AdminConfig adminConfig = getAdminConfig(projectId);
		List<String> participants = adminConfig.getDefaultParticipants();

		if (participants.contains(participantId)) {
			participants.remove(participantId);
			adminConfig.setDefaultParticipants(participants);
			save(adminConfig);
		}
	}
	
	@Override
	public void removeDigestManager(String projectId, String managerId) {
		AdminConfig adminConfig = getAdminConfig(projectId);
		List<String> managers = adminConfig.getManagers();

		if (managers.contains(managerId)) {
			managers.remove(managerId);
			adminConfig.setManagers(managers);
			save(adminConfig);
		}
	}

	@Override
	public AdminConfig save(AdminConfig adminConfig) {
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			adminConfig = pm.makePersistent(adminConfig);
			adminConfig = pm.detachCopy(adminConfig);
		} finally {
			pm.close();
		}
		return adminConfig;
	}
}
