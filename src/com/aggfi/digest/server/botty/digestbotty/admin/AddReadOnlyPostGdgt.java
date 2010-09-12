package com.aggfi.digest.server.botty.digestbotty.admin;

import java.io.IOException;
import com.vegalabs.general.server.command.Command;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import com.aggfi.digest.server.botty.digestbotty.dao.ExtDigestDao;
import com.aggfi.digest.server.botty.google.forumbotty.ForumBotty;
import com.vegalabs.general.server.rpc.util.Util;
import com.aggfi.digest.server.botty.google.forumbotty.dao.AdminConfigDao;
import com.aggfi.digest.server.botty.google.forumbotty.model.AdminConfig;
import com.aggfi.digest.server.botty.google.forumbotty.model.ForumPost;
import com.google.inject.Inject;
import com.google.wave.api.Blip;
import com.google.wave.api.Gadget;
import com.google.wave.api.Participants;
import com.google.wave.api.Wavelet;

public class AddReadOnlyPostGdgt extends Command {
	  private static final Logger LOG = Logger.getLogger(AddReadOnlyPostGdgt.class.getName());
	  private Util util = null;
	private ForumBotty robot = null;
	private AdminConfigDao adminConfigDao = null;
	private ExtDigestDao extDigestDao = null;

	  @Inject
		public AddReadOnlyPostGdgt(Util util,ForumBotty robot, AdminConfigDao adminConfigDao, ExtDigestDao extDigestDao) {
			this.util = util;
			this.robot = robot;
			this.adminConfigDao = adminConfigDao;
			this.extDigestDao = extDigestDao;
		}

	  @Override
	  public JSONObject execute() throws JSONException {
		  LOG.entering(this.getClass().getName(), "execute", toString());
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
			newWavelet = robot.newWave(robot.getDomain(), participantsSet ,"NEW_POST",projectId,robot.getRpcServerUrl());
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "", e);
		}
		String title = "New Secure Post";
		newWavelet.setTitle(title);
		if(newWavelet.getParticipants().contains(System.getProperty("PUBLIC_GROUP"))){
			newWavelet.getParticipants().setParticipantRole(System.getProperty("PUBLIC_GROUP"), Participants.Role.READ_ONLY);
		}else if(newWavelet.getParticipants().contains("public@a.gwave.com")){
			newWavelet.getParticipants().setParticipantRole("public@a.gwave.com", Participants.Role.READ_ONLY);
		}
		
		ForumPost entry = robot.addOrUpdateDigestWave(projectId, newWavelet, newWavelet.getRootBlip(), userId);
		String projectName = extDigestDao.retrDigestsByProjectId(projectId).get(0).getName();
		appendNewPostGadget(projectId, newWavelet.getRootBlip(), projectName);
		 if( adminConfig.isAdsEnabled()){
			  robot.appendAd2Blip(newWavelet.getRootBlip(),newWavelet.getRootBlip().getBlipId(), projectId, false);
		  }
		  robot.addBack2Digest2RootBlip(projectId, newWavelet.getRootBlip(), entry);
		
		  if(adminConfig.isViewsTrackingEnabled()){
			  robot.appendViewsTrackingGadget(newWavelet.getRootBlip(), projectId);
		  }else{
			  LOG.warning("views tracking not enabled for: " + projectId);
		  }
		  
		try {
			robot.submit(newWavelet, robot.getRpcServerUrl());
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "", e);
		}
	    JSONObject json = new JSONObject();
	    json.put("success", "true");
	    return json;
	  }

	public static void appendNewPostGadget(String projectId, Blip blip, String projectName) {
		LOG.info("Appneding new post gadget: " + projectId);
		String gadgetUrl = System.getProperty("READONLYPOST_GADGET_URL");
		Gadget gadget = null;
		gadget = new Gadget(gadgetUrl);
		gadget.setProperty("projectId", projectId);
		gadget.setProperty("projectName", projectName);
		blip.append("\n");
		blip.append(gadget);
	}
}
