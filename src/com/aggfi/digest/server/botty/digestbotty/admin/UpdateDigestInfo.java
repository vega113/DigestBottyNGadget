package com.aggfi.digest.server.botty.digestbotty.admin;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.waveprotocol.wave.model.id.WaveId;
import org.waveprotocol.wave.model.id.WaveletId;
import com.aggfi.digest.server.botty.digestbotty.dao.ExtDigestDao;
import com.aggfi.digest.server.botty.digestbotty.model.ExtDigest;
import com.aggfi.digest.server.botty.digestbotty.utils.WaveUtils;
import com.aggfi.digest.server.botty.google.forumbotty.ForumBotty;
import com.vegalabs.general.server.rpc.util.Util;
import com.aggfi.digest.server.botty.google.forumbotty.dao.AdminConfigDao;
import com.aggfi.digest.server.botty.google.forumbotty.dao.ForumPostDao;
import com.aggfi.digest.server.botty.google.forumbotty.model.AdminConfig;
import com.aggfi.digest.server.botty.google.forumbotty.model.ForumPost;
import com.aggfi.digest.shared.FieldVerifier;
import com.google.appengine.api.datastore.Text;
import com.google.inject.Inject;
import com.google.wave.api.Annotation;
import com.google.wave.api.Annotations;
import com.google.wave.api.Blip;
import com.google.wave.api.Wavelet;
import com.vegalabs.general.server.command.Command;

public class UpdateDigestInfo extends Command {
	  private static final Logger LOG = Logger.getLogger(UpdateDigestInfo.class.getName());
	  private Util util = null;

	  
	  private ExtDigestDao extDigestDao = null;
	  protected ForumBotty robot = null;
	  protected AdminConfigDao adminConfigDao;
	  protected ForumPostDao forumPostDao;
	  
	  @Inject
	  public UpdateDigestInfo(Util util, ForumBotty robot, ExtDigestDao extDigestDao, AdminConfigDao adminConfigDao, ForumPostDao forumPostDao) {
		  this.util = util;
		  this.extDigestDao = extDigestDao;
		  this.robot= robot;
		  this.adminConfigDao = adminConfigDao;
		  this.forumPostDao = forumPostDao;
	  }

	  @Override
	  public JSONObject execute() throws JSONException {

	    String projectId = this.getParam("projectId");
	    if (util.isNullOrEmpty(projectId)) {
	      throw new IllegalArgumentException("Missing required param: projectId");
	    }
	    String authorName = this.getParam("authorName");
	    if (util.isNullOrEmpty(authorName)) {
	      throw new IllegalArgumentException("Missing required param: authorName");
	    }
	    String forumName = this.getParam("forumName");
	    if (util.isNullOrEmpty(forumName)) {
	      throw new IllegalArgumentException("Missing required param: forumName");
	    }
	    String description = this.getParam("description");
	    if (util.isNullOrEmpty(description)) {
	    	description = "";
	    }
	    String installerThumbnailUrl = this.getParam("installerThumbnailUrl");
	    if (util.isNullOrEmpty(installerThumbnailUrl)) {
	    	installerThumbnailUrl = "";
	    }
	    String forumSiteUrl = this.getParam("forumSiteUrl");
	    if (util.isNullOrEmpty(forumSiteUrl)) {
	    	forumSiteUrl = "";
	    }
	    
	    List<ExtDigest> extList = extDigestDao.retrDigestsByProjectId(projectId);
	    ExtDigest extDigest = extList.get(0);
	    
	    String oldForumName = extDigest.getName();
	    
	    extDigest.setAuthor(authorName);
	    extDigest.setName(forumName);
	    extDigest.setDescription(description);
	    extDigest.setInstallerThumbnailUrl(installerThumbnailUrl);
	    extDigest.setInstallerIconUrl(installerThumbnailUrl);
	    extDigest.setRobotThumbnailUrl(installerThumbnailUrl);
	    extDigest.setForumSiteUrl(forumSiteUrl);
	    int version = 10;
	    try{
	    	version = extDigest.getVersion();
	    }catch(Exception e){};
	    version++;
	    extDigest.setVersion(version);
	    extDigestDao.save(extDigest);
	    
	    
	    
	    //now what about installer?
	    //digest wave?
	    //forum installer and FAQ wave?
	   
	    LOG.fine(extDigest.toString());
	    String proxyFor = extDigest.getProjectId();
	    robot.clearCustomProfileFromCache(proxyFor);
	    
	    AdminConfig adminConfig = adminConfigDao.getAdminConfig(extDigest.getProjectId());
	    String digestWaveDomain = extDigest.getDomain();
	    String digestWaveId = extDigest.getWaveId();
	    Wavelet wavelet = null;
		try {
			wavelet = robot.fetchWavelet( new WaveId(digestWaveDomain, digestWaveId), new WaveletId(digestWaveDomain, "conv+root"), extDigest.getProjectId() + "-digest", robot.getRpcServerUrl());
		} catch (IOException e) {
			LOG.log(Level.SEVERE,"",e);
		}
		
		if(extDigest.getLastDigestBlipId() == null){
			 String blipId = WaveUtils.extractBlipIdFromAnnon(wavelet.getRootBlip(),"link/manual", ForumBotty.TO_BOTTOM);
			 extDigest.setLastDigestBlipId(blipId);
			 extDigestDao.save(extDigest);
		 }
		
		
		String postWaveId = extDigest.getPostWaveId();
		if(postWaveId == null){
			//find from blips
			for(String key : wavelet.getBlips().keySet()){
				Blip ablip = wavelet.getBlips().get(key);
				LOG.info("title: " + ablip.getContent());
				if(ablip.getContent().contains(AddReadOnlyPostGdgt.NEW_POST_GADGET)){
					postWaveId = WaveUtils.extractWaveIdFromAnnon(ablip,"link/wave", AddReadOnlyPostGdgt.NEW_POST_GADGET);
					extDigest.setPostWaveId(postWaveId);
					extDigestDao.save(extDigest);
				}
			}
		}else{
			if(postWaveId.contains("!")){
				postWaveId = postWaveId.split("!")[1];
			}
		}
		LOG.info("postWaveId: " + postWaveId );
		
		//fetch post gadget wavelet
		Wavelet postWavelet = null;
		if(postWaveId != null){
			try {
				postWavelet = robot.fetchWavelet( new WaveId(digestWaveDomain, postWaveId), new WaveletId(digestWaveDomain, "conv+root"), extDigest.getProjectId(), robot.getRpcServerUrl());
			} catch (IOException e) {
				LOG.log(Level.SEVERE,"",e);
			}
			Blip postBlip = postWavelet.getRootBlip();
			Map<String,String> delta = new HashMap<String,String>();
			delta.put("projectName", forumName);
			LOG.info("trying to update post gadget");
			robot.updateGadgetState(postBlip, System.getProperty("READONLYPOST_GADGET_URL"), delta);
			WaveUtils.updateLinkTitleInDigestBlip(forumName,oldForumName, postBlip,"link/manual");
		}
		 
		
		String faqWaveId = extDigest.getFaqWaveId();
		ForumPost faqForumPost =  null;
		if(faqWaveId != null){
			faqForumPost = forumPostDao.getForumPost(digestWaveDomain, faqWaveId);
		}
		if(faqWaveId == null ||  (faqWaveId != null && faqForumPost.getDigestBlipId() == null)){
			//find from blips
			for(String key : wavelet.getBlips().keySet()){
				Blip ablip = wavelet.getBlips().get(key);
				LOG.info("updating FAQ digest blip; title: " + ablip.getContent());
				if(ablip.getContent().contains("and FAQ]")){
					WaveUtils.updateLinkTitleInDigestBlip(forumName,oldForumName, ablip,"link/wave");
					faqWaveId = WaveUtils.extractWaveIdFromAnnon(ablip,"link/wave", "and FAQ]");
					if(faqForumPost == null){
						faqForumPost = forumPostDao.getForumPost(digestWaveDomain, faqWaveId);
					}
					if(faqForumPost.getDigestBlipId() == null){
						faqForumPost.setDigestBlipId(ablip.getBlipId());
						forumPostDao.save(faqForumPost);
					}
					extDigest.setFaqWaveId(faqWaveId);
					extDigestDao.save(extDigest);
				}
			}

		}else{
			LOG.info("faqWaveId: " + faqWaveId );
			if(faqWaveId.contains("!")){
				faqWaveId = faqWaveId.split("!")[1];
			}
			faqForumPost = forumPostDao.getForumPost(digestWaveDomain, faqWaveId);
			LOG.info("faqForumPost.getDigestBlipId: " + faqForumPost.getDigestBlipId());
			Blip ablip = wavelet.getBlip(faqForumPost.getDigestBlipId());
			WaveUtils.updateLinkTitleInDigestBlip(forumName,oldForumName, ablip,"link/wave");
		}
		
		String faqDigestBlipId = null;
		Map<String, Blip> blipsMap = wavelet.getBlips();
		if(faqForumPost != null){
			faqDigestBlipId = faqForumPost.getDigestBlipId();
			if(faqDigestBlipId != null){
				Blip ablip = blipsMap.get(faqDigestBlipId);
				WaveUtils.updateLinkTitleInDigestBlip(forumName,oldForumName, ablip,"link/wave");
			}
		}
		
		wavelet.getRootBlip().all().delete();
	    try {
			CreateDigest.appendDigestContent(adminConfig, extDigest,extDigestDao, robot, wavelet, false, false);
		} catch (IOException e) {
			LOG.log(Level.SEVERE,"",e);
		}
		 try {
			 LOG.info("last blip id: " + extDigest.getLastDigestBlipId());
			 if(extDigest.getLastDigestBlipId() != null){
				 robot.addOrUpdateLinkToBottomOrTopForDigestWave(wavelet.getRootBlip(), extDigest.getLastDigestBlipId(), true,false);
			 }
		} catch (IOException e) {
			LOG.log(Level.SEVERE,"",e);
		}
		
	
		
		Wavelet faqWavelet = null;
		if(faqWaveId != null){
			try {
				faqWavelet = robot.fetchWavelet( new WaveId(digestWaveDomain, faqWaveId), new WaveletId(digestWaveDomain, "conv+root"), extDigest.getProjectId(), robot.getRpcServerUrl());
			} catch (IOException e) {
				LOG.log(Level.SEVERE,"",e);
			}
			
			faqWavelet.getRootBlip().all().delete();
			LOG.info("recreating FAQ wave: " + faqWavelet.getWaveId().toString());
			CreateDigest.appendFaq2blip(forumName, projectId, digestWaveDomain + "!" +digestWaveId, extDigest.getOwnerId(), faqWavelet, adminConfig, robot);
			robot.addBack2Digest2RootBlip(projectId, faqWavelet.getRootBlip(), faqForumPost);
			
			faqForumPost.setTitle(faqWavelet.getTitle());
			faqForumPost.setFirstBlipContent(new Text (faqWavelet.getRootBlip().getContent()));
			forumPostDao.save(faqForumPost);
		}
		
		if(postWavelet != null){
			postWavelet.submitWith(wavelet);
		}
		if(faqWavelet != null){
			faqWavelet.submitWith(wavelet);
		}
		
		
		if(wavelet != null){
			try {
				robot.submit(wavelet, robot.getRpcServerUrl());
			} catch (IOException e) {
				LOG.log(Level.SEVERE,"",e);
			}
		}
		
	    JSONObject json = new JSONObject();
	    json.put("success", "true");
	    return json;
	  }


	
}
