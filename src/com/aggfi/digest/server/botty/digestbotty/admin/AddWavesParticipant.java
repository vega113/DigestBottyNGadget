package com.aggfi.digest.server.botty.digestbotty.admin;


import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.waveprotocol.wave.model.id.WaveId;
import org.waveprotocol.wave.model.id.WaveletId;

import com.aggfi.digest.server.botty.digestbotty.dao.ExtDigestDao;
import com.aggfi.digest.server.botty.google.forumbotty.ForumBotty;
import com.aggfi.digest.server.botty.google.forumbotty.Util;
import com.aggfi.digest.server.botty.google.forumbotty.admin.Command;
import com.aggfi.digest.server.botty.google.forumbotty.dao.AdminConfigDao;
import com.aggfi.digest.server.botty.google.forumbotty.dao.ForumPostDao;
import com.aggfi.digest.server.botty.google.forumbotty.model.ForumPost;
import com.google.inject.Inject;
import com.google.wave.api.Wavelet;

public class AddWavesParticipant extends Command {
  private static final Logger LOG = Logger.getLogger(AddWavesParticipant.class.getName());
  private ForumPostDao forumPostDao;
  private ForumBotty robot;
  private Util util = null;

  @Inject
	public AddWavesParticipant(Util util, ForumPostDao forumPostDao,ForumBotty robot) {
		this.util = util;
		this.robot = robot;
		this.forumPostDao = forumPostDao;
	}

  @Override
  public JSONObject execute() throws JSONException {
    String projectId = this.getParam("projectId");
    if (util.isNullOrEmpty(projectId)) {
      throw new IllegalArgumentException("Missing required param: id");
    }

    String participantId = this.getParam("participantId");
    if (util.isNullOrEmpty(participantId)) {
      throw new IllegalArgumentException("Missing required param: participantId");
    }
    String tag = this.getParam("tag");
    Wavelet wavelet = null;
    //retrieve a list of all forumPosts for this projectId
    List<ForumPost> entries = forumPostDao.getForumPostsByTag(projectId,tag,Integer.parseInt(System.getProperty("MAX_WAVELET_FETCH_SIZE")));
    Wavelet firstWavelet = null;
    try {
    	for(ForumPost entry : entries){

    		wavelet = robot.fetchWavelet( new WaveId(entry.getDomain(), entry.getWaveId()), new WaveletId(entry.getDomain(), "conv+root"), entry.getProjectId(),  robot.getRpcServerUrl());
    		
    		if(!wavelet.getParticipants().contains(participantId)){
    			wavelet.getParticipants().add(participantId);
        		if(firstWavelet == null){
        			firstWavelet = wavelet;
        		}
        		wavelet.submitWith(firstWavelet);
    		}

    	}
    	robot.submit(firstWavelet, robot.getRpcServerUrl());
    } catch (IOException e) {
		LOG.log(Level.SEVERE,"",e);
	}
    

    JSONObject json = new JSONObject();
    json.put("result", String.valueOf(Math.max(entries.size(),200)));
    return json;
  }

}

