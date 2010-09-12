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

public class AddViewsCounterInstaller extends Command {
	  private static final Logger LOG = Logger.getLogger(AddViewsCounterInstaller.class.getName());
	  private Util util = null;
	private ForumBotty robot = null;

	  @Inject
		public AddViewsCounterInstaller(Util util,ForumBotty robot, ExtDigestDao extDigestDao) {
			this.util = util;
			this.robot = robot;
		}

	  @Override
	  public JSONObject execute() throws JSONException {
		  LOG.info("AddViewsCounterInstaller: " + toString());
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
		String title = "[Views counter Installer]";
		newWavelet.setTitle(title);
		
		appendViewsCounterInstaller(newWavelet.getRootBlip(), userId);
		
		  
		try {
			robot.submit(newWavelet, robot.getRpcServerUrl());
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "", e);
		}
	    JSONObject json = new JSONObject();
	    json.put("success", "true");
	    return json;
	  }

	public void appendViewsCounterInstaller(Blip blip, String userId) {
		LOG.info("appendViewsCounterInstaller: ");
		String installertUrl = "http://" + System.getProperty("APP_DOMAIN") + ".appspot.com/viewsCounterInstaller1.xml";
		blip.append(new Installer(installertUrl)); 
		blip.append("\n");
		blip.append("Use the Installer above to install the Views counter gadget. The installer will be placed in the toolbar menu. Use it in order to add the gadget to your forum waves.");
		
		LOG.info("appendViewsCounterInstaller: tracker.xml");
		installertUrl = "http://" + System.getProperty("APP_DOMAIN") + ".appspot.com/viewsTrackerInstaller.xml";
		blip.append(new Installer(installertUrl)); 
		blip.append("\n");
		blip.append("Use the Installer above to install the Views tracker gadget. The installer will be placed in the toolbar menu. Use it in order to add the gadget to your forum waves.");
	}
}


