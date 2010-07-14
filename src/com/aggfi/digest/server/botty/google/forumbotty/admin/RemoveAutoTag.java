package com.aggfi.digest.server.botty.google.forumbotty.admin;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.waveprotocol.wave.model.id.WaveId;
import org.waveprotocol.wave.model.id.WaveletId;

import com.aggfi.digest.server.botty.google.forumbotty.ForumBotty;
import com.aggfi.digest.server.botty.google.forumbotty.Util;
import com.aggfi.digest.server.botty.google.forumbotty.dao.AdminConfigDao;
import com.aggfi.digest.server.botty.google.forumbotty.dao.ForumPostDao;
import com.aggfi.digest.server.botty.google.forumbotty.model.ForumPost;
import com.google.inject.Inject;
import com.google.wave.api.Blip;
import com.google.wave.api.Wavelet;

public class RemoveAutoTag extends Command {

  private static final Logger LOG = Logger.getLogger(RemoveAutoTag.class.getName());
  private AdminConfigDao adminConfigDao = null;
  private ForumPostDao forumPostDao = null;
  private ForumBotty robot = null;
  private Util util = null;

  @Inject
  public RemoveAutoTag(Util util, AdminConfigDao adminConfigDao, ForumPostDao forumPostDao,ForumBotty robot) {
    this.adminConfigDao = adminConfigDao;
    this.forumPostDao = forumPostDao;
    this.robot = robot;
    this.util = util;
  }

  @Override
  public JSONObject execute() throws JSONException {
    String tag = this.getParam("tag");
    if (util.isNullOrEmpty(tag)) {
      throw new IllegalArgumentException("Missing required param: tag");
    }

    String projectId = this.getParam("projectId");
    if (util.isNullOrEmpty(projectId)) {
      throw new IllegalArgumentException("Missing required param: id");
    }
    
    
    String sync = this.getParam("sync");
	  if (util.isNullOrEmpty(projectId)) {
		  throw new IllegalArgumentException("Missing required param: Sync");
	  }
	  
	  int tagRemovedCount = 0;
	  Wavelet firstWavelet = null;
	  try {
		  if(sync != null && !"".equals(sync)){
			  //retrieve all forumPosts for this projectId

			  Wavelet wavelet = null;
			  //retrieve a list of all forumPosts for this projectId
			  List<ForumPost> entries = forumPostDao.getForumPostsByTag(projectId,null,Integer.parseInt(System.getProperty("MAX_WAVELET_FETCH_SIZE")));
			  for(ForumPost entry : entries){
				  try{
					  wavelet = robot.fetchWavelet( new WaveId(entry.getDomain(), entry.getWaveId()), new WaveletId(entry.getDomain(), "conv+root"), entry.getProjectId(),  robot.getRpcServerUrl());
				  }catch (IOException e) {
					  LOG.log(Level.FINER,"can happen if the robot was removed manually from the wave.",e);
				  }
				  if(wavelet.getTags().contains(tag)){
					  tagRemovedCount++;
					  if(firstWavelet == null){
						  firstWavelet = wavelet;
					  }
					  wavelet.getTags().remove(tag);
					  wavelet.submitWith(firstWavelet);
					  entry = forumPostDao.syncTags(projectId, entry, wavelet);
					  forumPostDao.save(entry);
				  }
			  }
			  if(firstWavelet != null){
				  robot.submit(firstWavelet, robot.getRpcServerUrl());
			  }
		  }
	  } catch (IOException e) {
		  LOG.log(Level.SEVERE,"",e);
		  throw new RuntimeException(e);
	  }

    this.adminConfigDao.removeAutoTagRegex(projectId, tag);
    JSONObject json = new JSONObject();
    if(!"true".equals(sync)){
		  json.put("success", "true");
	  }else{
		  json.put("result", tagRemovedCount);
	  }
    return json;
  }

}
