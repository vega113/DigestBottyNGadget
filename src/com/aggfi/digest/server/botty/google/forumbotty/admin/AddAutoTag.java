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

import com.aggfi.digest.server.botty.digestbotty.admin.AddWavesParticipant;
import com.aggfi.digest.server.botty.google.forumbotty.ForumBotty;
import com.aggfi.digest.server.botty.google.forumbotty.Util;
import com.aggfi.digest.server.botty.google.forumbotty.dao.AdminConfigDao;
import com.aggfi.digest.server.botty.google.forumbotty.dao.ForumPostDao;
import com.aggfi.digest.server.botty.google.forumbotty.model.ForumPost;
import com.google.inject.Inject;
import com.google.wave.api.Blip;
import com.google.wave.api.Wavelet;

public class AddAutoTag extends Command {
  private static final Logger LOG = Logger.getLogger(AddAutoTag.class.getName());
  private AdminConfigDao adminConfigDao = null;
  private ForumPostDao forumPostDao = null;
  private ForumBotty robot = null;
  private Util util = null;

  @Inject
  public AddAutoTag(Util util, AdminConfigDao adminConfigDao, ForumPostDao forumPostDao,ForumBotty robot) {
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

	  String regex = this.getParam("regex");
	  if (util.isNullOrEmpty(regex)) {
		  throw new IllegalArgumentException("Missing required param: regex");
	  }

	  String projectId = this.getParam("projectId");
	  if (util.isNullOrEmpty(projectId)) {
		  throw new IllegalArgumentException("Missing required param: id");
	  }

	  String sync = this.getParam("sync");
	  if (util.isNullOrEmpty(projectId)) {
		  throw new IllegalArgumentException("Missing required param: Sync");
	  }
	  
	  int tagAppliedCount = 0;
	  Wavelet firstWavelet = null;
	  try {
		  this.adminConfigDao.addAutoTagRegex(projectId, tag, regex);
		  if(sync != null && !"".equals(sync)){
			  //retrieve all forumPosts for this projectId

			  Wavelet wavelet = null;
			  //retrieve a list of all forumPosts for this projectId
			  List<ForumPost> entries = forumPostDao.getForumPostsByTag(projectId,null,Integer.parseInt(System.getProperty("MAX_WAVELET_FETCH_SIZE")));
			  for(ForumPost entry : entries){
				  boolean isTagApplied = false;
				  wavelet = robot.fetchWavelet( new WaveId(entry.getDomain(), entry.getWaveId()), new WaveletId(entry.getDomain(), "conv+root"), entry.getProjectId(),  robot.getRpcServerUrl());
				  if(!wavelet.getTags().contains(tag)){
					  Map<String,Blip> blips = wavelet.getBlips();
					  for(String blipKey : blips.keySet()){
						  isTagApplied = robot.applyAutoTag(blips.get(blipKey), projectId, tag);
						  if(isTagApplied){
							  tagAppliedCount++;
							  if(firstWavelet == null){
								  firstWavelet = wavelet;
							  }
							  wavelet.submitWith(firstWavelet);
							  entry = forumPostDao.syncTags(projectId, entry, wavelet);
							  forumPostDao.save(entry);
							  break;
						  }
					  }
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
	  JSONObject json = new JSONObject();
	  if(!"true".equals(sync)){
		  json.put("success", "true");
	  }else{
		  json.put("result", tagAppliedCount);
	  }
	  return json;
  }

}
