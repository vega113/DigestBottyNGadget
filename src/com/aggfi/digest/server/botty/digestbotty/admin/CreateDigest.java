package com.aggfi.digest.server.botty.digestbotty.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.aggfi.digest.server.botty.digestbotty.dao.ExtDigestDao;
import com.aggfi.digest.server.botty.digestbotty.model.ExtDigest;
import com.aggfi.digest.server.botty.digestbotty.utils.StringBuilderAnnotater;
import com.aggfi.digest.server.botty.google.forumbotty.ForumBotty;
import com.aggfi.digest.server.botty.google.forumbotty.Util;
import com.aggfi.digest.server.botty.google.forumbotty.admin.Command;
import com.aggfi.digest.server.botty.google.forumbotty.dao.AdminConfigDao;
import com.aggfi.digest.server.botty.google.forumbotty.model.AdminConfig;
import com.google.gson.annotations.Expose;
import com.google.gwt.http.client.URL;
import com.google.inject.Inject;
import com.google.wave.api.Blip;
import com.google.wave.api.BlipContentRefs;
import com.google.wave.api.Gadget;
import com.google.wave.api.Installer;
import com.google.wave.api.JsonRpcResponse;
import com.google.wave.api.Participants;
import com.google.wave.api.Wavelet;
import com.google.wave.api.JsonRpcConstant.ParamsProperty;
import com.google.wave.api.impl.DocumentModifyAction.BundledAnnotation;

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
	    	
	    	
	    	boolean isAdsEnabled = Boolean.parseBoolean(getParam("isAdsEnabled"));
			AdminConfig adminConfig = adminConfigDao.getAdminConfig(projectId);
			adminConfig.setAdsEnabled(isAdsEnabled);
			boolean isPublicOnCreate = Boolean.parseBoolean(this.getParam("publicOnCreate"));
			adminConfig.setAtomFeedPublic(isPublicOnCreate);
			adminConfigDao.save(adminConfig);
	    	
	    	String senderId = this.getParam("senderId");
	        if (senderId != null && !senderId.equals(extDigest.getOwnerId())) {
	        	LOG.severe(extDigest.getOwnerId()  + " and " + senderId + " do not match!");
	        	throw new RuntimeException(extDigest.getOwnerId()  + " and " + senderId + " do not match!" );
	        }
	        
	    	if(extDigestDao.retrDigestsByProjectId(projectId).size() > 0 ){
	    		throw new IllegalArgumentException("Project with id: " + projectId + " already exists! Please choose another project id."); 
	    	}
	    	int numOfOwnerDigests = extDigestDao.retrDigestsByOwnerId(ownerId).size();
	    	if(!ownerId.equals(System.getProperty("BOTTY_OWNER_WAVE_ID")) && numOfOwnerDigests > Integer.parseInt(System.getProperty("MAX_DIGESTS"))){
	    		throw new IllegalArgumentException("Max number of Digests per owner is: " + System.getProperty("MAX_DIGESTS")); 
	    	}
	    	Wavelet wavelet = null;
	    	try{
	    		wavelet = createDigestWave(digestWaveDomain,ownerId,projectId,projectName,googlegroups, isPublicOnCreate);
	    	}catch(IOException ioe){
	    		if(ioe.getMessage().indexOf("Timeout") > -1){
	    			//------------------------
	    			wavelet = createDigestWave(digestWaveDomain,ownerId,projectId,projectName,googlegroups, isPublicOnCreate);
	    			//----------------------------
	    		}else{
	    			throw ioe;
	    		}
	    	}
	    	String digestWaveId = wavelet.getWaveId().getId();
	    	extDigest.setWaveId(digestWaveId);
	    	if(extDigest.getForumSiteUrl() == null || "".equals(extDigest.getForumSiteUrl())){
	    		extDigest.setForumSiteUrl("https://wave.google.com/wave/waveref/googlewave.com/" + digestWaveId);
	    	}
	    	if(extDigest.getInstallerIconUrl() == null || "".equals(extDigest.getInstallerIconUrl())){
	    		extDigest.setInstallerIconUrl(System.getProperty("WAVE_ICON_URL"));
	    	}
	    	if(extDigest.getInstallerThumbnailUrl() == null || "".equals(extDigest.getInstallerThumbnailUrl())){
	    		extDigest.setInstallerThumbnailUrl(System.getProperty("WAVE_ICON_URL"));
	    	}
	    	if(extDigest.getRobotThumbnailUrl() == null || "".equals(extDigest.getRobotThumbnailUrl())){
	    		if(extDigest.getInstallerThumbnailUrl() == null || System.getProperty("WAVE_ICON_URL").equals(extDigest.getInstallerThumbnailUrl())){
	    			extDigest.setRobotThumbnailUrl(System.getProperty("ROBOT_ICON_URL"));
	    		}else{
	    			extDigest.setRobotThumbnailUrl(extDigest.getInstallerThumbnailUrl());
	    		}
	    	}
	    	//-------------
			extDigestDao.save(extDigest);
			//-------------
			
			String robotAddress = System.getProperty("APP_DOMAIN") + "+" + projectId +  "@appspot.com";
			
			//now create FAQ wave for the newly created Digest
			createFAQ(extDigest,wavelet,isPublicOnCreate);
			
			String message = "Success! You have " + (numOfOwnerDigests + 1) + " forum(s). Maximum number of Forums per owner is: " + System.getProperty("MAX_DIGESTS") + 
			". Forum wave was created and you were added as participant.";
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
	private void createFAQ(ExtDigest extDigest, Wavelet digestWavelet, boolean isPublicOnCreate) throws IOException {
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
			participants.add(System.getProperty("PUBLIC_GROUP"));
		}
		participants.add(ownerId);
		LOG.info("IN createFAQ, robot name: " + robot.getRobotName() + ", " + robot.getRobotProfilePageUrl());
		Wavelet newWavelet = robot.newWave(domain, participants ,"NEW_FORUM_FAQ_CREATED_MSG","",rpcUrl);
		newWavelet.getParticipants().add(robotAddress);
		if(isPublicOnCreate){
			newWavelet.getParticipants().setParticipantRole(System.getProperty("PUBLIC_GROUP"), Participants.Role.READ_ONLY);
		}
		newWavelet.setTitle(projectName  + " [Installer and FAQ]");
		
		newWavelet.getRootBlip().append(new Installer(installerUrl)); 
		
		appendFaq2blip(projectName,projectId, digestWavelet.getWaveId().getDomain() + "!" + digestWavelet.getWaveId().getId() ,ownerId, newWavelet);
		
		newWavelet.getParticipants().setParticipantRole(System.getProperty("PUBLIC_GROUP"), Participants.Role.READ_ONLY);
	
		robot.submit(newWavelet, rpcUrl);
		//add this post to the digest
		robot.addOrUpdateDigestWave(projectId, newWavelet, null, null);
	}

	protected void appendFaq2blip(String projectName, String projectId,String digestWaveId,String ownerId,Wavelet newWavelet) {
		StringBuilderAnnotater sba = new StringBuilderAnnotater(newWavelet.getRootBlip());
		
		String styleFontWeight = "style/fontWeight";
		String styleFontStyle = "style/fontStyle";
		//-1
		String guideTitle = "Guide:\n";
		sba.append(guideTitle, "style/fontSize", "18pt");
		String guideStr = "Make sure to check the ";
		sba.append(guideStr, styleFontStyle, "italic");
		String guideLink = "Wave Guide";
		sba.append(guideLink, "link/manual", System.getProperty("WAVE_GUIDE_REF"));
		sba.append(".\n\n\n", "styleFontStyle", "italic");
		
		//0
		String faqStr = "FAQ:\n\n";
		sba.append(faqStr, "style/fontSize", "18pt");
		//1
		String q1 = "Q: How can I create posts in this Forum?\n";
		sba.append(q1, styleFontWeight, "bold");
		String a1 = "A: Use the installer above to install \"" + projectName + "\" Forum. " + 
			"After installation you will have a \"New " + projectName + " Post\" option in the \"New Wave\" menu, use it to create new posts.\n\n";
		sba.append(a1, styleFontStyle, "italic");
		//2
		String q4 = "Q: How can I be notified about new posts in this Forum?\n";
		sba.append(q4, styleFontWeight, "bold");
		String a4_1 = "A: You can go to the \"" + projectName + "\" ";
		sba.append(a4_1, styleFontStyle, "italic");
		String a4_2 = "Digest wave";
		sba.append(a4_2, "link/wave",  digestWaveId);
		String a4_3 = " and then \"Follow\" it.\n\n";
		sba.append(a4_3, styleFontStyle, "italic");
		//3
		String q6 = "Q: How can I subscribe to the ATOM feed for this forum?\n";
		sba.append(q6, styleFontWeight, "bold");
		String feedUrl = "http://" + System.getProperty("APP_DOMAIN") +  ".appspot.com/feeds/atom?id=" + projectId;
		String a6_1 = "A: You can add this ATOM feed URL to your favorite Feed Reader (like Google Reader): \"";
		sba.append(a6_1, styleFontStyle, "italic");
		String a6_2 = feedUrl;
		sba.append(a6_2, styleFontStyle, "italic");
		String a6_3 = "\" .\n\n";
		sba.append(a6_3, styleFontStyle, "italic");
		//4
		String q9 = "Q: Is there a perma link to the Forum, so I can link to it outside the Wave?\n";
		sba.append(q9, styleFontWeight, "bold");
		String embedUrl = "http://" + System.getProperty("APP_DOMAIN") +  ".appspot.com/showembedded?forumId=" + projectId;
		String a9_1 = "A: Yes, you can expose the Forum outside the Wave using the following link: \"";
		sba.append(a9_1, styleFontStyle, "italic");
		String a9_2 = embedUrl;
		sba.append(a9_2, styleFontStyle, "italic");
		String a9_3 = "\" (Wave access rules still aply).\n\n";
		sba.append(a9_3, styleFontStyle, "italic");
		//5
		String q5 = "Q: How can I import an existing wave into the Forum?\n";
		sba.append(q5, styleFontWeight, "bold");
		String robotAddress = System.getProperty("APP_DOMAIN") + "+" + projectId +  "@appspot.com";
		String a5_1 = "A: You can import an existing wave by adding your forum robot, i.e. \"" +robotAddress + "\" to the wave you want to import - either manually, or by clicking on the robot icon on the toolbar while in edit mode.\n\n";
		sba.append(a5_1, styleFontStyle, "italic");
		//6
		String q7 = "Q: How do I search for Forum waves?\n";
		sba.append(q7, styleFontWeight, "bold");
		String a7_1 = "A: You can use the \"Saved Search\" that was installed along with the Forum. It is located on the \"Navigation\" panel on the top left of the Wave Client under the \"Searches\" category. To remove it - hover with the mouse over the search and then choose the \"delete\" option.\n\n";
		sba.append(a7_1, styleFontStyle, "italic");
		//7
		String q8 = "Q: How do I locate the forum Digest wave?\n";
		sba.append(q8, styleFontWeight, "bold");
		String a8_1 = "A: You can scroll up to the top of the post and click on the \"Back to " + projectName + " digest wave\" link which is located at the bottom of the root blip.  Another option is to click on the icon of the forum robot - and then on the \"Website\" link - this will redirect you to the Digest wave.\n\n";
		sba.append(a8_1, styleFontStyle, "italic");
		//8
		String q2 = "Q: Who is the Forum owner?\n";
		sba.append(q2, styleFontWeight, "bold");
		String a2 = "A: This forum was created by: " + ownerId + " , \"wave\" this id for all questions regarding the \"" +projectName + "\" forum.\n\n";
		sba.append(a2, styleFontStyle, "italic");
		//9
		String q3 = "Q: I have more questions regarding using forums created by DigestBotty, where can I ask them?\n";
		sba.append(q3, styleFontWeight, "bold");
		String a3_1 = "A: Please visit the DigestBotty digest wave ";
		sba.append(a3_1, styleFontStyle, "italic");
		String a3_2 = "here";
		sba.append(a3_2, "link/wave", System.getProperty("DIGESTBOTTY_DIGEST_LINK"));
		String a3_3 = " and check if you can find your answer.";
		sba.append(a3_3, styleFontStyle, "italic");
		String a3_4 = " Or, install the DigestBotty Forum ";
		sba.append(a3_4, styleFontStyle, "italic");
		String a3_5 = "here";
		sba.append(a3_5, "link/wave", System.getProperty("DIGESTBOTTY_FORUM_FAQ_LINK"));
		String a3_6 = " and then create a new post with your question. You are also welcome to leave your feedback!\n\n";
		sba.append(a3_6, styleFontStyle, "italic");
		
		sba.flush2Blip();
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
		
		String description = createDigest.getParam("description").replace("\"", "'");
		String installerThumbnailUrl = createDigest.getParam("installerThumbnailUrl");
		String name = createDigest.getParam("name").replace("\"", "'");
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
		String author = createDigest.getParam("author").replace("\"", "'");
		int maxDigests = 1;
		if(System.getProperty("MAX_DIGESTS") != null){
			try{
				maxDigests = Integer.parseInt(System.getProperty("MAX_DIGESTS"));
			}catch(Exception e){
				LOG.log(Level.WARNING, "Cannot find system property: MAX_DIGESTS" );
				maxDigests = 2;
			}
		}
		ExtDigest digest = new ExtDigest(domain, "TBD_ID", projectId, description, name, installerThumbnailUrl, installerIconUrl, robotThumbnailUrl, forumSiteUrl, googlegroupsId, ownerId,author,maxDigests, ExtDigest.CURRENT_VERSION);
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
			newWavelet.getParticipants().add(System.getProperty("PUBLIC_GROUP"));
			adminConfigDao.addDefaultParticipant(projectId, System.getProperty("PUBLIC_GROUP"));
		}
		newWavelet.getParticipants().setParticipantRole(System.getProperty("PUBLIC_GROUP"), Participants.Role.READ_ONLY);
		
		String titleStr = projectName  + " digest wave";
		newWavelet.setTitle(titleStr);
		BlipContentRefs.range(newWavelet.getRootBlip(), 1, titleStr.length()+1).annotate("style/fontSize","2em");
		BlipContentRefs.range(newWavelet.getRootBlip(), 1, projectName.length()).annotate("style/fontWeight","bold");
		String gadgetUrl = System.getProperty("CLICK_GADGET_URL");
		Gadget gadget = new Gadget(gadgetUrl);
		gadget.setProperty("waveid", newWavelet.getWaveletId().getId());
		newWavelet.getRootBlip().append(gadget);
		newWavelet.getRootBlip().append("\n");
		Blip bottomBlip = newWavelet.reply("\n");
		if(adminConfigDao.getAdminConfig(projectId).isAdsEnabled()){
			String adGadgetUrl = "http://" + System.getProperty("APP_DOMAIN") +  ".appspot.com/serveAd?id="+projectId;
			Gadget adGadgetTop = new Gadget(adGadgetUrl);
			gadget.setProperty("projectId", projectId);
			newWavelet.getRootBlip().append(adGadgetTop);
			
			Gadget adGadgetBottom = new Gadget(adGadgetUrl);
			gadget.setProperty("projectId", projectId);
			bottomBlip.append(adGadgetBottom);
			
		}
		
		
		List<JsonRpcResponse> submitResponseList = robot.submit(newWavelet, rpcUrl);
		  for(JsonRpcResponse res : submitResponseList){
			  Map<ParamsProperty, Object> dataMap = res.getData();
			  if(dataMap != null && dataMap.get(ParamsProperty.NEW_BLIP_ID)  != null){
				  String blipId = String.valueOf(dataMap.get(ParamsProperty.NEW_BLIP_ID));
				  robot.addOrUpdateLinkToBottomOrTopForDigestWave(newWavelet.getRootBlip(), blipId, true);
			  }
		  }
		  robot.addOrUpdateLinkToBottomOrTopForDigestWave(bottomBlip, newWavelet.getRootBlipId(), false);
		return newWavelet;
	}

}
