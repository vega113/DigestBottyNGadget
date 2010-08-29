package com.aggfi.digest.server.botty.digestbotty.admin;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import com.aggfi.digest.server.botty.digestbotty.dao.ExtDigestDao;
import com.aggfi.digest.server.botty.google.forumbotty.ForumBotty;
import com.aggfi.digest.server.botty.google.forumbotty.Util;
import com.aggfi.digest.server.botty.google.forumbotty.admin.Command;
import com.aggfi.digest.server.botty.google.forumbotty.dao.AdminConfigDao;
import com.aggfi.digest.server.botty.google.forumbotty.model.AdminConfig;
import com.aggfi.digest.server.botty.google.forumbotty.model.ForumPost;
import com.google.inject.Inject;
import com.google.wave.api.Gadget;
import com.google.wave.api.Participants;
import com.google.wave.api.Wavelet;

public class AddReadOnlyPostGadget extends Command {
	  private static final Logger LOG = Logger.getLogger(AddReadOnlyPostGadget.class.getName());
	  private Util util = null;
	private ForumBotty robot = null;
	private AdminConfigDao adminConfigDao = null;
	private ExtDigestDao extDigestDao = null;

	  @Inject
		public AddReadOnlyPostGadget(Util util,ForumBotty robot, AdminConfigDao adminConfigDao, ExtDigestDao extDigestDao) {
			this.util = util;
			this.robot = robot;
			this.adminConfigDao = adminConfigDao;
			this.extDigestDao = extDigestDao;
		}

	  @Override
	  public JSONObject execute() throws JSONException {
	    String projectId = this.getParam("projectId");
	    if (util.isNullOrEmpty(projectId)) {
	      throw new IllegalArgumentException("Missing required param: projectId");
	    }

	    String userId = this.getParam("userId");
	    if (util.isNullOrEmpty(userId)) {
	      throw new IllegalArgumentException("Missing required param: userId");
	    }
	    
	    
	    AdminConfig adminConfig = adminConfigDao.getAdminConfig(projectId);
	    Set<String> participantsSet = new LinkedHashSet<String>();
	    participantsSet.addAll(adminConfig.getDefaultParticipants());
	    participantsSet.add(userId);
	    Wavelet newWavelet = null;
		try {
			newWavelet = robot.newWave(robot.getDomain(), participantsSet ,"NEW_POST","gadget",robot.getRpcServerUrl());
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "", e);
		}
		String title = "Create \"View Only\" posts here.";
		newWavelet.setTitle(title);
		 if(newWavelet.getParticipants().contains("public@a.gwave.com")){
			newWavelet.getParticipants().setParticipantRole("public@a.gwave.com", Participants.Role.READ_ONLY);
		}
		 for(String participant : newWavelet.getParticipants()){
				if(participant.indexOf("googlegroups.com") > 0){
					newWavelet.getParticipants().setParticipantRole(participant, Participants.Role.READ_ONLY);
				}
		}
		 newWavelet.getParticipants().add(System.getProperty("APP_DOMAIN") + "+" + projectId + "@appspot.com");
		
		ForumPost entry = robot.addOrUpdateDigestWave(projectId, newWavelet, newWavelet.getRootBlip(), userId);
		String projectName = extDigestDao.retrDigestsByProjectId(projectId).get(0).getName();
		String gadgetUrl = System.getProperty("READONLYPOST_GADGET_URL");
		Gadget gadget = null;
		gadget = new Gadget(gadgetUrl);
		gadget.setProperty("projectId", projectId);
		gadget.setProperty("projectName", projectName);
		newWavelet.getRootBlip().append(gadget);
		 if( adminConfig.isAdsEnabled()){
			  robot.appendAd2Blip(newWavelet.getRootBlip(), projectId, false);
		  }
		  robot.addBack2Digest2RootBlip(projectId, newWavelet.getRootBlip(), entry);
		
		try {
			robot.submit(newWavelet, robot.getRpcServerUrl());
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "", e);
		}
	    JSONObject json = new JSONObject();
	    json.put("success", "true");
	    return json;
	  }
}
