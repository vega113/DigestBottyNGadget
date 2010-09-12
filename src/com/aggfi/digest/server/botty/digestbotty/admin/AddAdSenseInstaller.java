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
import com.google.inject.Inject;
import com.google.wave.api.Blip;
import com.google.wave.api.Installer;
import com.google.wave.api.Wavelet;

public class AddAdSenseInstaller extends Command {
	  private static final Logger LOG = Logger.getLogger(AddAdSenseInstaller.class.getName());
	  private Util util = null;
	private ForumBotty robot = null;
	private ExtDigestDao extDigestDao = null;

	  @Inject
		public AddAdSenseInstaller(Util util,ForumBotty robot, ExtDigestDao extDigestDao) {
			this.util = util;
			this.robot = robot;
			this.extDigestDao = extDigestDao;
		}

	  @Override
	  public JSONObject execute() throws JSONException {
		  LOG.info("createViewsCounterInstaller: " + toString());
	    String projectId = this.getParam("projectId");
	    if (util.isNullOrEmpty(projectId)) {
	      throw new IllegalArgumentException("Missing required param: projectId");
	    }
	    String userId = this.getParam("userId");
	    if (util.isNullOrEmpty(userId)) {
	      throw new IllegalArgumentException("Missing required param: userId");
	    }
	    
	    
	    Set<String> participantsSet = new LinkedHashSet<String>();
	    participantsSet.add(userId);
	    Wavelet newWavelet = null;
		try {
			newWavelet = robot.newWave(robot.getDomain(), participantsSet ,"NEW_POST",null,robot.getRpcServerUrl());
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "", e);
		}
		newWavelet.getParticipants().add(userId);
		String forumName = extDigestDao.retrDigestsByProjectId(projectId).get(0).getName();
		String title = "[AdSense Gadget Installer] - " + forumName;
		newWavelet.setTitle(title);
		
		appendAdSenseInstaller(projectId,newWavelet.getRootBlip(),forumName);
		
		  
		try {
			robot.submit(newWavelet, robot.getRpcServerUrl());
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "", e);
		}
	    JSONObject json = new JSONObject();
	    json.put("success", "true");
	    return json;
	  }

	public void appendAdSenseInstaller(String projectId, Blip blip, String forumName) {
		LOG.info("appendAdSenseInstaller: " + projectId);
		String installertUrl = "http://" + System.getProperty("APP_DOMAIN") + ".appspot.com/serveAdInstaller?id=" + projectId;
		blip.append(new Installer(installertUrl)); 
		blip.append("\n");
		blip.append("Use the Installer above to install the AdSense gadget for the \"" + forumName + "\" forum. The installer will be placed in the toolbar menu. Use it in order to add your AdSense ads into blips.");
	}
}

