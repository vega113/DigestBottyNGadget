package com.aggfi.digest.server.botty.digestbotty.admin;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.waveprotocol.wave.model.id.WaveId;
import org.waveprotocol.wave.model.id.WaveletId;
import com.aggfi.digest.server.botty.google.forumbotty.ForumBotty;
import com.vegalabs.general.server.rpc.util.Util;
import com.aggfi.digest.server.botty.google.forumbotty.dao.AdminConfigDao;
import com.aggfi.digest.server.botty.google.forumbotty.dao.ForumPostDao;
import com.aggfi.digest.server.botty.google.forumbotty.model.AdminConfig;
import com.aggfi.digest.server.botty.google.forumbotty.model.ForumPost;
import com.google.inject.Inject;
import com.google.wave.api.Wavelet;
import com.vegalabs.general.server.command.Command;

public class UpdateViewsTracking extends Command {
	  private static final Logger LOG = Logger.getLogger(UpdateViewsTracking.class.getName());
	  private Util util = null;
	  private AdminConfigDao adminConfigDao;
	  private ForumPostDao forumPostDao = null;
	  private ForumBotty robot = null;

	  @Inject
		public UpdateViewsTracking(Util util,ForumBotty robot, AdminConfigDao adminConfigDao, ForumPostDao forumPostDao) {
			this.util = util;
			this.adminConfigDao = adminConfigDao;
			this.forumPostDao = forumPostDao;
		    this.robot = robot;
		}

	  @Override
	  public JSONObject execute() throws JSONException {
		  LOG.info( "UpdateViewsTracking: " +  toString());
	  String isEnableViewsTrackingStr = this.getParam("isEnableViewsTracking");
	    if (util.isNullOrEmpty(isEnableViewsTrackingStr)) {
	      throw new IllegalArgumentException("Missing required param: isEnableViewsTracking");
	    }
	    boolean isEnableViewsTracking = Boolean.parseBoolean(isEnableViewsTrackingStr);
	    
	    
	    String isSyncStr = this.getParam("isSync");
	    if (util.isNullOrEmpty(isSyncStr)) {
	      throw new IllegalArgumentException("Missing required param: isSync");
	    }
	    boolean isSync = Boolean.parseBoolean(isSyncStr);
	    
	    String projectId = this.getParam("projectId");
	    if ( util.isNullOrEmpty(projectId)) {
	      throw new IllegalArgumentException("Missing required param: projectId");
	    }
	    Wavelet firstWavelet = null;
		  try {
			  if(isSync){
				  //retrieve all forumPosts for this projectId
				  LOG.log(Level.FINE,"Syncing views tracking gadget for project: " + projectId);
				  Wavelet wavelet = null;
				  //retrieve a list of all forumPosts for this projectId
				  List<ForumPost> entries = forumPostDao.getForumPostsByTag(projectId,null,Integer.parseInt(System.getProperty("MAX_WAVELET_FETCH_SIZE")));
				  LOG.info("In UpdateViewsTracking - retrieved " + entries.size() + " entries for forum: " + projectId );
				  for(ForumPost entry : entries){
					  try{
						  wavelet = robot.fetchWavelet( new WaveId(entry.getDomain(), entry.getWaveId()), new WaveletId(entry.getDomain(), "conv+root"), entry.getProjectId(),  robot.getRpcServerUrl());
					  }catch (IOException e) {
						  LOG.log(Level.FINER,"can happen if the robot was removed manually from the wave.",e);
						  JSONObject json = new JSONObject();
						    json.put("error", e.getMessage());
						    return json;
					  }

					  if(isEnableViewsTracking){

						  robot.appendViewsTrackingGadget(wavelet.getRootBlip(), projectId);
						  if(firstWavelet == null){
							  firstWavelet = wavelet;
						  }
						  wavelet.submitWith(firstWavelet);
					  }else {
						  robot.removeViewsTrackingGadget(wavelet.getRootBlip(), projectId);
						  if(firstWavelet == null){
							  firstWavelet = wavelet;
						  }
						  wavelet.submitWith(firstWavelet);
					  }
				  }
				  if(firstWavelet != null){
					  robot.submit(firstWavelet, robot.getRpcServerUrl());
				  }
				  
				  
				  
			  }else{
				  LOG.log(Level.FINE,"Sync is: " + isSyncStr + ", projectId: " + projectId);
			  }
		  } catch (IOException e) {
			  LOG.log(Level.SEVERE,"",e);
			  throw new RuntimeException(e);
		  }
	    
	    LOG.info("UpdateViewsTracking: " + isEnableViewsTrackingStr);
	    AdminConfig adminConfig = adminConfigDao.getAdminConfig(projectId);
	    

    	adminConfig.setViewsTrackingEnabled(isEnableViewsTracking);
    	adminConfigDao.save(adminConfig);
		
	    JSONObject json = new JSONObject();
	    json.put("success", "true");
	    return json;
	  }
}

