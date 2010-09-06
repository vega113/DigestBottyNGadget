package com.aggfi.digest.server.botty.digestbotty.admin;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.vegalabs.general.server.command.Command;
import org.json.JSONException;
import org.json.JSONObject;
import com.aggfi.digest.server.botty.google.forumbotty.ForumBotty;
import com.vegalabs.general.server.rpc.util.Util;
import com.aggfi.digest.server.botty.google.forumbotty.dao.AdminConfigDao;
import com.aggfi.digest.server.botty.google.forumbotty.model.AdminConfig;
import com.google.inject.Inject;
import com.google.wave.api.Participants;
import com.google.wave.api.Wavelet;

public class CreateNewPost extends Command {
	  private static final Logger LOG = Logger.getLogger(CreateNewPost.class.getName());
	  private Util util = null;
	private ForumBotty robot = null;
	private AdminConfigDao adminConfigDao = null;

	  @Inject
		public CreateNewPost(Util util,ForumBotty robot, AdminConfigDao adminConfigDao) {
			this.util = util;
			this.robot = robot;
			this.adminConfigDao = adminConfigDao;
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
	    String title = this.getParam("title");
	    if (util.isNullOrEmpty(projectId)) {
	      throw new IllegalArgumentException("Missing required param: title");
	    }
	    
	    AdminConfig adminConfig = adminConfigDao.getAdminConfig(projectId);
	    Set<String> participantsSet = new LinkedHashSet<String>();
	    participantsSet.addAll(adminConfig.getDefaultParticipants());
	    participantsSet.add(userId);
	    Wavelet newWavelet = null;
		try {
			newWavelet = robot.newWave(robot.getDomain(), participantsSet ,"NEW_POST",projectId,robot.getRpcServerUrl());
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "", e);
		}
		newWavelet.setTitle(title);
		if(newWavelet.getParticipants().contains("public@a.gwave.com")){
			newWavelet.getParticipants().setParticipantRole("public@a.gwave.com", Participants.Role.READ_ONLY);
		}
		for(String participant : newWavelet.getParticipants()){
			if(participant.indexOf("googlegroups.com") > 0){
				newWavelet.getParticipants().setParticipantRole(participant, Participants.Role.READ_ONLY);
			}
		}
		newWavelet.getRootBlip().append("\n" + userId + " - Please enter the post content.");
		
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
