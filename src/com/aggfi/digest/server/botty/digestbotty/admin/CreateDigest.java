package com.aggfi.digest.server.botty.digestbotty.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.aggfi.digest.server.botty.digestbotty.dao.ExtDigestDao;
import com.aggfi.digest.server.botty.digestbotty.model.ExtDigest;
import com.aggfi.digest.server.botty.google.forumbotty.ForumBotty;
import com.aggfi.digest.server.botty.google.forumbotty.Util;
import com.aggfi.digest.server.botty.google.forumbotty.admin.Command;
import com.aggfi.digest.server.botty.google.forumbotty.dao.AdminConfigDao;
import com.google.gson.annotations.Expose;
import com.google.inject.Inject;
import com.google.wave.api.Blip;
import com.google.wave.api.BlipContentRefs;
import com.google.wave.api.Gadget;
import com.google.wave.api.Installer;
import com.google.wave.api.Participants;
import com.google.wave.api.Wavelet;

public class CreateDigest extends Command {

	private Logger LOG = Logger.getLogger(CreateDigest.class.getName());
	private Util util;
	private ExtDigestDao extDigestDao;
	private ForumBotty robot;
	private AdminConfigDao adminConfigDao;

	@Inject
	public CreateDigest(Util util, ExtDigestDao extDigestDao, AdminConfigDao adminConfigDao, ForumBotty robot) {
		this.extDigestDao = extDigestDao;
		this.util = util;
		this.robot = robot;
		this.adminConfigDao = adminConfigDao;
	}

	@Override
	public JSONObject execute() throws JSONException {      
		 LOG.entering(this.getClass().getName(), "execute()", this);
		
		String installerUrl = "http://" + System.getProperty("APP_DOMAIN") + "." + "appspot.com" + "/installNew?id=";
		//create one more wave, add there admin gadget and owner, and link to digest wave id,  url to installer with instructions how to create installer, address of project robot, url to admin page
		JSONObject json = new JSONObject();
		ExtDigest extDigest = null;
		Output output = null;
		try{
	    	extDigest = initDigest(this);
	    	String digestWaveDomain = extDigest.getDomain();
	    	String projectId = extDigest.getProjectId();
	    	String projectName= extDigest.getName();
	    	String ownerId = extDigest.getOwnerId();
	    	String googlegroups = extDigest.getGooglegroupsId();
	    	if(extDigestDao.retrDigestsByProjectId(projectId).size() > 0 ){
	    		throw new IllegalArgumentException("Project with id: " + projectId + " already exists! Please choose another project id."); 
	    	}
	    	int numOfOwnerDigests = extDigestDao.retrDigestsByOwnerId(ownerId).size();
	    	if(!ownerId.equals("vega113@googlewave.com") && numOfOwnerDigests > Integer.parseInt(System.getProperty("MAX_DIGESTS"))){
	    		throw new IllegalArgumentException("Max number of Digests per owner is: " + System.getProperty("MAX_DIGESTS")); 
	    	}
	    	boolean isPublicOnCreate = Boolean.parseBoolean(this.getParam("publicOnCreate"));
	    	Wavelet wavelet = null;
	    	try{
	    		wavelet = createDigestWave(digestWaveDomain,ownerId,projectId,projectName,googlegroups, isPublicOnCreate);
	    	}catch(IOException ioe){
	    		if(ioe.getMessage().indexOf("Timeout") > -1){
	    			wavelet = createDigestWave(digestWaveDomain,ownerId,projectId,projectName,googlegroups, isPublicOnCreate);
	    		}else{
	    			throw ioe;
	    		}
	    	}
	    	String digestWaveId = wavelet.getWaveId().getId();
	    	extDigest.setWaveId(digestWaveId);
	    	if(extDigest.getForumSiteUrl() == null || "".equals(extDigest.getForumSiteUrl())){
	    		extDigest.setForumSiteUrl("https://wave.google.com/wave/waveref/googlewave.com/" + digestWaveId);
	    	}
	    	//-------------
			extDigestDao.save(extDigest);
			//-------------
			
			String robotAddress = System.getProperty("APP_DOMAIN") + "+" + projectId +  "@appspot.com";
			
			//now create FAQ wave for the newly created Digest
			createFAQ(extDigest,digestWaveId,isPublicOnCreate);
			
			String message = "Success! You have " + numOfOwnerDigests + " digests. Maximum number of Digests per owner is: " + System.getProperty("MAX_DIGESTS") + 
			". Digest wave was created and you were added as participant.";
			output = new Output(installerUrl +extDigest.getProjectId(), digestWaveId, projectName, projectId,robotAddress , message );
		} catch (IOException e) {
			LOG.log(Level.SEVERE,"",e);
			StringWriter sw = new StringWriter(); 
			PrintWriter p = new PrintWriter(sw);
			e.printStackTrace(p);
			json.put("error", util.toJson(sw.getBuffer().toString()));
			return json;
		}catch (IllegalArgumentException e) {
			json.put("error", util.toJson(e.getMessage()));
			LOG.log(Level.SEVERE,"",e);
			LOG.exiting(this.getClass().getName(), "execute()");
			return json;
		}
		LOG.info("create_digest output: " + output.toString());
		json.put("result", util.toJson(output.message));
		
		LOG.exiting(this.getClass().getName(), "execute()");
		return json;
	}
	
	/*
	 * add first post in the Digest - with installer
	 */
	private void createFAQ(ExtDigest extDigest, String digestWaveId, boolean isPublicOnCreate) throws IOException {
		String domain = extDigest.getDomain();
    	String projectId = extDigest.getProjectId();
    	String projectName= extDigest.getName();
    	String ownerId = extDigest.getOwnerId();
    	String googlegroups = extDigest.getGooglegroupsId();
    	String installerUrl = "http://" + System.getProperty("APP_DOMAIN") + "." + "appspot.com" + "/installNew?id=" + projectId;
    	
		String robotAddress = System.getProperty("APP_DOMAIN") + "+" + projectId +  "@appspot.com";
		String rpcUrl = domain.equals(ForumBotty.PREVIEW_DOMAIN) ? ForumBotty.PREVIEW_RPC_URL : ForumBotty.SANDBOX_RPC_URL;
		
		Set<String> participants = new HashSet<String>();
		if(!util.isNullOrEmpty(googlegroups)){
			if(!googlegroups.contains("@googlegroups.com")){
				participants.add(googlegroups + "@googlegroups.com");
			}else{
				participants.add(googlegroups);
			}
		}
		if(isPublicOnCreate){
			participants.add("public@a.gwave.com");
		}
		
		participants.add(ownerId);
		LOG.info("IN createFAQ, robot name: " + robot.getRobotName() + ", " + robot.getRobotProfilePageUrl());
		Wavelet newWavelet = robot.newWave(domain, participants ,"NEW_FORUM_FAQ_CREATED_MSG","",rpcUrl);
		newWavelet.getParticipants().add(robotAddress);
		if(isPublicOnCreate){
			newWavelet.getParticipants().setParticipantRole("public@a.gwave.com", Participants.Role.READ_ONLY);
		}
		newWavelet.setTitle(projectName  + " Installer and FAQ");
		
		newWavelet.getRootBlip().append(new Installer(installerUrl)); 
		
		appendFaq2blip(projectName, digestWaveId,ownerId, newWavelet);
		
		newWavelet.getParticipants().setParticipantRole("public@a.gwave.com", Participants.Role.READ_ONLY);
	
		
		robot.submit(newWavelet, rpcUrl);
		
		//add this post to the digest
		robot.addPost2Digest(projectId, newWavelet);
	}

	protected void appendFaq2blip(String projectName, String digestWaveId,String ownerId,Wavelet newWavelet) {
		StringBuilderAnnotater sba = new StringBuilderAnnotater(newWavelet.getRootBlip());
		
		String styleFontWeight = "style/fontWeight";
		String styleFontStyle = "style/fontStyle";
		//0
		String faqStr = "FAQ:\n\n";
		sba.append(faqStr, styleFontWeight, "bold");
		//1
		String q1 = "Q: How can I create posts in this Forum?\n";
		sba.append(q1, styleFontWeight, "bold");
		String a1 = "A: Use the installer above to install " + projectName + " Forum. " + 
			"After installation you will have a \"New " + projectName + " Post\" option in the \"New Wave\" menu, use it to create new posts.\n\n";
		sba.append(a1, styleFontStyle, "italic");
		//2
		String q2 = "Q: Who is the Forum owner?\n";
		sba.append(q2, styleFontWeight, "bold");
		String a2 = "A: This forum was created by: " + ownerId + " , \"wave\" this id for all questions regarding the \"" +projectName + "\" forum.\n\n";
		sba.append(a2, styleFontStyle, "italic");
		//3
		String q3 = "Q: I have more questions regarding use of Forums Created by DigestBotty, where can I ask them?\n";
		sba.append(q3, styleFontWeight, "bold");
		String a3_1 = "A: Please visit the DigestBotty digest wave ";
		sba.append(a3_1, styleFontStyle, "italic");
		String a3_2 = "here";
		sba.append(a3_2, "link/wave", System.getProperty("DIGESTBOTTY_DIGEST_LINK"));
		String a3_3 = " and check if you can find your answer.";
		sba.append(a3_3, styleFontStyle, "italic");
		String a3_4 = " Also, you are welcome to install the DigestBotty Forum ";
		sba.append(a3_4, styleFontStyle, "italic");
		String a3_5 = "here";
		sba.append(a3_5, "link/wave", System.getProperty("DIGESTBOTTY_FORUM_LINK"));
		String a3_6 = " and then create a new post with your question.\n\n";
		sba.append(a3_6, styleFontStyle, "italic");
		//4
		String q4 = "Q: How can I be notified about new posts in this Forum?\n";
		sba.append(q4, styleFontWeight, "bold");
		String a4_1 = "A: You can go to the \"" + projectName + "\" ";
		sba.append(a4_1, styleFontStyle, "italic");
		String a4_2 = "Digest wave";
		sba.append(a4_2, "link/wave", "googlewave.com!" + digestWaveId);
		String a4_3 = " and then \"Follow\" it.\n\n";
		sba.append(a4_3, styleFontStyle, "italic");
		
		
		sba.flush2Blip();
	}
	
	class StringBuilderAnnotater{
		private StringBuilder sb;
		private ArrayList<Integer> annStarts;
		private ArrayList<Integer> annEnds;
		private ArrayList<String> annVal;
		private ArrayList<String> annType;
		private Blip blip;
		int prevBlipLength;
		
		public StringBuilderAnnotater(Blip blip){
			sb = new StringBuilder();
			annStarts = new ArrayList<Integer>();
			annEnds = new ArrayList<Integer>();
			annVal = new ArrayList<String>();
			annType = new ArrayList<String>();
			this.blip = blip;
			prevBlipLength = blip.length();
		}
		
		public void append(String str, String annTypeStr, String annValStr){
			int curLength = sb.length();
			int strLength = str.length();
			annStarts.add(curLength);
			annEnds.add(curLength + strLength);
			annVal.add(annValStr);
			annType.add(annTypeStr);
			sb.append(str);
		}
		
		public Blip flush2Blip(){
			blip.append(sb.toString());
			int length = annStarts.size();
			for(int i = 0; i < length; i++){
				int annStart = annStarts.get(i);
				int annEnd = annEnds.get(i);
				if(annVal.get(i) != null && annType.get(i) != null){
					BlipContentRefs.range(blip, prevBlipLength + annStart, prevBlipLength + annEnd).annotate( annType.get(i), annVal.get(i) );
				}
			}
			return blip;
		}
	}

	class Output {
		@Expose
		String installerUrl;
		@Expose
		String digestWaveId;
		@Expose
		String projectName;
		@Expose
		String projectId;
		@Expose
		String robotId;
		@Expose
		String message;
		
		public Output(String installerUrl, String digestWaveId,
				String projectName, String projectId, String robotId, String message) {
			super();
			this.installerUrl = installerUrl;
			this.digestWaveId = digestWaveId;
			this.projectName = projectName;
			this.projectId = projectId;
			this.robotId = robotId;
			this.message = message;
		}

		@Override
		public String toString() {
			return "Output [digestWaveId=" + digestWaveId + ", installerUrl="
					+ installerUrl + ", message=" + message + ", projectId="
					+ projectId + ", projectName=" + projectName + ", robotId="
					+ robotId + "]";
		}
		
	}
	
	public  ExtDigest initDigest(CreateDigest createDigest){
		String projectId = createDigest.getParam("projectId");
		if (util.isNullOrEmpty(projectId)) {
			throw new IllegalArgumentException("Missing required param: projectId");
		} 
		
		String description = createDigest.getParam("description");
		String installerThumbnailUrl = createDigest.getParam("installerThumbnailUrl");
		String name = createDigest.getParam("name");
		if (util.isNullOrEmpty(name)) {
			throw new IllegalArgumentException("Missing required param: Name of the project");
		} 
		String installerIconUrl = createDigest.getParam("installerIconUrl");
		String robotThumbnailUrl = createDigest.getParam("robotThumbnailUrl");
		String forumSiteUrl = createDigest.getParam("forumSiteUrl");
		String googlegroupsId = createDigest.getParam("googlegroupsId");
		String ownerId = createDigest.getParam("ownerId");
		if (util.isNullOrEmpty(ownerId)) {
			throw new IllegalArgumentException("Missing required param: ownerId");
		} 
		String domain = createDigest.getParam("domain");
		if (util.isNullOrEmpty(domain)) {
			throw new IllegalArgumentException("Missing required param: domain");
		}
		String author = createDigest.getParam("author");
		int maxDigests = 1;
		if(System.getProperty("MAX_DIGESTS") != null){
			try{
				maxDigests = Integer.parseInt(System.getProperty("MAX_DIGESTS"));
			}catch(Exception e){
				LOG.log(Level.WARNING, "Cannot find system property: MAX_DIGESTS" );
				maxDigests = 2;
			}
		}
		ExtDigest digest = new ExtDigest(domain, "TBD_ID", projectId, description, name, installerThumbnailUrl, installerIconUrl, robotThumbnailUrl, forumSiteUrl, googlegroupsId, ownerId,author,maxDigests);
		return digest;
	}
	
	private Wavelet createDigestWave(String domain, String ownerId, String projectId, String projectName, String googlegroups, boolean isPublicOnCreate) throws IOException {
		Set<String> participants = new HashSet<String>(); 
		participants.add(ownerId);
		try{
			adminConfigDao.addDefaultParticipant(projectId, ownerId);
		}catch (Exception e) {
			//FIXME why this happens? it (sometime) says default participant already exists
			LOG.log(Level.SEVERE,"",e);
		}
		if(!util.isNullOrEmpty(googlegroups)){
			participants.add(googlegroups);
			//add googlegroups to project default participants
			adminConfigDao.addDefaultParticipant(projectId, googlegroups);
		}
		String rpcUrl = domain.equals(ForumBotty.PREVIEW_DOMAIN) ? ForumBotty.PREVIEW_RPC_URL : ForumBotty.SANDBOX_RPC_URL;
		Wavelet newWavelet = robot.newWave(domain, participants ,"NEW_FORUM_CREATED_MSG",projectId + "-digest",rpcUrl);
		if (isPublicOnCreate) {
			newWavelet.getParticipants().add("public@a.gwave.com");
			adminConfigDao.addDefaultParticipant(projectId, "public@a.gwave.com");
		}
		newWavelet.getParticipants().setParticipantRole("public@a.gwave.com", Participants.Role.READ_ONLY);
		newWavelet.setTitle(projectName  + " Digest Wave");
		String gadgetUrl = System.getProperty("CLICK_GADGET_URL");
		Gadget gadget = new Gadget(gadgetUrl);
		gadget.setProperty("waveid", newWavelet.getWaveletId().getId());
		newWavelet.getRootBlip().append(gadget);
		robot.submit(newWavelet, rpcUrl);
		return newWavelet;
	}

}
