package com.aggfi.digest.server.botty.digestbotty.admin;

import java.io.IOException;
import com.vegalabs.general.server.command.Command;
import com.vegalabs.general.server.command.CommandFetcher;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import com.aggfi.digest.server.botty.digestbotty.dao.ContributorDao;
import com.aggfi.digest.server.botty.digestbotty.dao.ExtDigestDao;
import com.aggfi.digest.server.botty.digestbotty.model.Contributor;
import com.aggfi.digest.server.botty.digestbotty.model.ExtDigest;
import com.aggfi.digest.server.botty.digestbotty.utils.StringBuilderAnnotater;
import com.aggfi.digest.server.botty.google.forumbotty.ForumBotty;
import com.vegalabs.general.server.rpc.util.Util;
import com.aggfi.digest.server.botty.google.forumbotty.admin.CommandType;
import com.aggfi.digest.server.botty.google.forumbotty.dao.AdminConfigDao;
import com.aggfi.digest.server.botty.google.forumbotty.dao.ForumPostDao;
import com.aggfi.digest.server.botty.google.forumbotty.model.AdminConfig;
import com.aggfi.digest.server.botty.google.forumbotty.model.ForumPost;
import com.google.gson.annotations.Expose;
import com.google.inject.Inject;
import com.google.inject.internal.ImmutableMap;
import com.google.wave.api.Blip;
import com.google.wave.api.Gadget;
import com.google.wave.api.Image;
import com.google.wave.api.Installer;
import com.google.wave.api.JsonRpcResponse;
import com.google.wave.api.Participants;
import com.google.wave.api.Wavelet;
import com.google.wave.api.JsonRpcConstant.ParamsProperty;
import com.google.wave.api.impl.DocumentModifyAction.BundledAnnotation;

public class CreateDigest extends Command {

	public static final String DIGEST_WAVE_STR = "digest wave";
	private static Logger LOG = Logger.getLogger(CreateDigest.class.getName());
	private Util util;
	private ExtDigestDao extDigestDao;
	private ForumBotty robot;
	private AdminConfigDao adminConfigDao;
	private ContributorDao contributorDao;
	private ForumPostDao forumPostDao;
	private CommandFetcher commandFetcher;

	@Inject
	public CreateDigest(Util util, ExtDigestDao extDigestDao, AdminConfigDao adminConfigDao, ContributorDao contributorDao, ForumBotty robot, ForumPostDao forumPostDao, CommandFetcher commandFetcher) {
		this.extDigestDao = extDigestDao;
		this.util = util;
		this.robot = robot;
		this.adminConfigDao = adminConfigDao;
		this.contributorDao = contributorDao;
		this.forumPostDao = forumPostDao;
		this.commandFetcher = commandFetcher;
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
	    	
	    	LOG.info("creating new digest - projectId: " + projectId);
	    	
	    	
	    	boolean isAdsEnabled = Boolean.parseBoolean(getParam("isAdsEnabled"));
	    	boolean isCopyAdSenseFromUser = Boolean.parseBoolean(getParam("isCopyAdSenseFromUser"));
			AdminConfig adminConfig = adminConfigDao.getAdminConfig(projectId);
			adminConfig.setAdsEnabled(isAdsEnabled);
			adminConfig.setViewsTrackingEnabled(true);
			boolean isPublicOnCreate = Boolean.parseBoolean(this.getParam("publicOnCreate"));
			adminConfig.setAtomFeedPublic(isPublicOnCreate);
			adminConfig.setDiggBtnEnabled(isPublicOnCreate);
			adminConfig.setBuzzBtnEnabled(isPublicOnCreate);
			adminConfig.setTweetBtnEnabled(isPublicOnCreate);
			adminConfig.setFaceBtnEnabled(isPublicOnCreate);
			
	    	
	    	String senderId = this.getParam("senderId");
	        if (senderId != null && !senderId.equals(extDigest.getOwnerId())) {
	        	LOG.severe(extDigest.getOwnerId()  + " and " + senderId + " do not match!");
	        	throw new RuntimeException(extDigest.getOwnerId()  + " and " + senderId + " do not match!" );
	        }
	        
	    	if(extDigestDao.retrDigestsByProjectId(projectId).size() > 0 ){
	    		throw new IllegalArgumentException("Project with id: " + projectId + " already exists! Please choose another project id."); 
	    	}
	    	
	    	Contributor contributor = contributorDao.get(ownerId);
	    	
	    	int numOfOwnerDigests = extDigestDao.retrDigestsByOwnerId(ownerId).size();
	    	if(!ownerId.equals(System.getProperty("BOTTY_OWNER_WAVE_ID")) && numOfOwnerDigests > Integer.parseInt(System.getProperty("MAX_DIGESTS")) 
	    			&& ( (numOfOwnerDigests - contributor.getCountAdSenseForums()) >= contributor.getMaxForumsAllowed()  ) ){ //adsense forums don't count in
	    		throw new IllegalArgumentException("Max number of Digests per owner is: " + System.getProperty("MAX_DIGESTS")); 
	    	}
	    	
	    	//--------------------------
			
			String message = "Success! You have " + (numOfOwnerDigests + 1) + " forum(s). Maximum number of Forums per owner is: " + System.getProperty("MAX_DIGESTS") + 
			". Forum wave was created and you were added as participant.";
			String warning = "";
			if(isAdsEnabled){
				//check if user has adsense code
				contributor.setCountAdSenseForums(contributor.getCountAdSenseForums() + 1);
				if(isCopyAdSenseFromUser){
					if(contributor.getGoogleAdsenseCode() == null || "".equals(contributor.getGoogleAdsenseCode().getValue())){ // should not happen!
						warning = "Warning! The forum is AdSense enabled, however forum owner: " + ownerId + ", has no AdSense code registered with DigestBotty. You can register your AdSense code with DigestBotty on the \"Personal\" tab." +
						" Or, you can provide AdSense code specific for this forum in the \"Forum Admin\" tab. Untill such code is provided, DigestBotty will use his own AdSense code. " ;
					}else{
						adminConfig.setAdsense(contributor.getGoogleAdsenseCode());
						message += " The forum was linked to the AdSense code that belongs to " + ownerId;
					}
				}else{
					if(contributor.getGoogleAdsenseCode() == null || "".equals(contributor.getGoogleAdsenseCode().getValue())){
						warning = "Warning! The forum is AdSense enabled, however, it doesn't have AdSence code linked to it.  You can link AdSense code to the forum in the Admin tab." ;
					}
				}
			}
			adminConfig.setAdsEnabled(isAdsEnabled);
			adminConfigDao.save(adminConfig);
	    	//-------------------------
	    	
	    	
	    	
	    	
	    	
	    	Wavelet digestWavelet = null;
	    	try{
	    		digestWavelet = createDigestWave(adminConfig, extDigest);
	    	}catch(IOException ioe){
	    		if(ioe.getMessage().indexOf("Timeout") > -1){
	    			//------------------------
	    			digestWavelet = createDigestWave(adminConfig, extDigest);
	    			//----------------------------
	    		}else{
	    			throw ioe;
	    		}
	    	}
	    	String digestWaveId = digestWavelet.getWaveId().getId();
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
			createFAQ(extDigest,digestWavelet,isPublicOnCreate);
			
			contributor.setCountForums(numOfOwnerDigests + 1);
			contributorDao.save(contributor);
			
			output = new Output(installerUrl +extDigest.getProjectId(), digestWaveId, projectName, projectId,robotAddress , message,warning );
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
		json.put("result", new JSONObject(util.toJson(output)));
		
		//add new post gadget
		  Command command = commandFetcher.fetchCommand("ADD_SECURE_POST_GADGET");
		  String userId = extDigest.getOwnerId();
		  Map<String,String> params = ImmutableMap.of("projectId", extDigest.getProjectId(), "userId", userId);
		  command.setParams(params);
		  JSONObject outJson = command.execute();
		  String postWaveId = outJson.getString("postWaveId");
		  extDigest.setPostWaveId(postWaveId);
		  extDigestDao.save(extDigest);
		  LOG.fine("Added new post gadget: " + outJson);
		
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
		LOG.fine("IN createFAQ, robot name: " + robot.getRobotName() + ", " + robot.getRobotProfilePageUrl());
		Wavelet newWavelet = robot.newWave(domain, participants ,"NEW_FORUM_FAQ_CREATED_MSG",projectId,rpcUrl);
		
		extDigest.setFaqWaveId(newWavelet.getWaveId().getId());
		extDigestDao.save(extDigest);
		
		newWavelet.getParticipants().add(robotAddress);
		if(isPublicOnCreate){
			newWavelet.getParticipants().setParticipantRole(System.getProperty("PUBLIC_GROUP"), Participants.Role.READ_ONLY);
		}
		
		
		
		AdminConfig adminConfig = adminConfigDao.getAdminConfig(projectId);
		
		appendFaq2blip(projectName,projectId, digestWavelet.getWaveId().getDomain() + "!" + digestWavelet.getWaveId().getId() ,ownerId, newWavelet,adminConfig, robot);
		
		newWavelet.getParticipants().setParticipantRole(System.getProperty("PUBLIC_GROUP"), Participants.Role.READ_ONLY);
	
		robot.submit(newWavelet, rpcUrl);
		//add this post to the digest
		robot.addOrUpdateDigestWave(projectId, newWavelet, null, null);
		
		ForumPost forumPost = new ForumPost(domain, newWavelet);
		forumPost.setProjectId(projectId);
		forumPost.setBlipCount(0);
		forumPost.setDispayAtom(false);
		forumPostDao.save(forumPost);
	}

	public static  void appendFaq2blip(String projectName, String projectId,String digestWaveId,String ownerId,Wavelet newWavelet, AdminConfig adminConfig, ForumBotty robot) {
		int startPos = newWavelet.getRootBlip().getContent().length();
		String titleStr = projectName  + " [Forum installer and FAQ]";
		newWavelet.setTitle(titleStr);
		newWavelet.getRootBlip().range(startPos,titleStr.length()+1).annotate("style/fontSize","2em").annotate("style/fontWeight","bold");
		
		String installerUrl = "http://" + System.getProperty("APP_DOMAIN") + "." + "appspot.com" + "/installNew?id=" + projectId;
		newWavelet.getRootBlip().append(new Installer(installerUrl)); 
		
		StringBuilderAnnotater sba = new StringBuilderAnnotater(newWavelet.getRootBlip());
		
		String styleFontWeight = "style/fontWeight";
		String styleFontStyle = "style/fontStyle";
		//-1
		String guideTitle = "Guide:\n";
		sba.append(guideTitle, "style/fontSize", "16pt");
		String guideStr = "Make sure to check the ";
		sba.append(guideStr, styleFontStyle, "italic");
		String guideLink = "Wave Guide";
		sba.append(guideLink, "link/manual", System.getProperty("WAVE_GUIDE_REF"));
		sba.append(".\n\n", "styleFontStyle", "italic");
		
		//0
		String faqStr = "FAQ:\n";
		sba.append(faqStr, "style/fontSize", "16pt");
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
		String q11 = "Q: How can I remove a wave from the Forum digest?\n";
		sba.append(q11, styleFontWeight, "bold");
		String robotAddress_11 = System.getProperty("APP_DOMAIN") + "+" + projectId +  "@appspot.com";
		String a11_1 = "A: You can remove a wave from the forum digest by removing the forum robot, i.e. \"" +robotAddress_11 + "\" from the wave you want to remove - by clicking on the robot icon on the wave participants panel and then on the \"Remove\" option .\n\n";
		sba.append(a11_1, styleFontStyle, "italic");
		//7
		String q7 = "Q: How do I search for Forum waves?\n";
		sba.append(q7, styleFontWeight, "bold");
		String a7_1 = "A: You can use the \"Saved Search\" that was installed along with the Forum. It is located on the \"Navigation\" panel on the top left of the Wave Client under the \"Searches\" category. To remove it - hover with the mouse over the search and then choose the \"delete\" option.\n\n";
		sba.append(a7_1, styleFontStyle, "italic");
		//8
		String q8 = "Q: How do I locate the forum Digest wave?\n";
		sba.append(q8, styleFontWeight, "bold");
		String a8_1 = "A: You can scroll up to the top of the post and click on the \"Back to " + projectName + " digest wave\" link which is located at the bottom of the root blip.  Another option is to click on the icon of the forum robot - and then on the \"Website\" link - this will redirect you to the Digest wave.\n\n";
		sba.append(a8_1, styleFontStyle, "italic");
		//9
		String q2 = "Q: Who is the Forum owner?\n";
		sba.append(q2, styleFontWeight, "bold");
		String a2 = "A: This forum was created by: " + ownerId + " , \"wave\" this id for all questions regarding the \"" +projectName + "\" forum.\n\n";
		sba.append(a2, styleFontStyle, "italic");
		//10
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
		
		 if(adminConfig.isViewsTrackingEnabled()){
			  robot.appendViewsTrackingGadget(newWavelet.getRootBlip(), projectId);
		  }
	}
	
	

	class Output implements Serializable{
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
		@Expose
		private String warning;
		
		public Output(String installerUrl, String digestWaveId,
				String projectName, String projectId, String robotId, String message, String warning) {
			super();
			this.installerUrl = installerUrl;
			this.digestWaveId = digestWaveId;
			this.projectName = projectName;
			this.projectId = projectId;
			this.robotId = robotId;
			this.message = message;
			this.warning = warning;
		}

		@Override
		public String toString() {
			return "Output [digestWaveId=" + digestWaveId + ", installerUrl="
					+ installerUrl + ", message=" + message + ", projectId="
					+ projectId + ", projectName=" + projectName + ", robotId="
					+ robotId + ", warning=" + warning + "]";
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
	
	private Wavelet createDigestWave(AdminConfig adminConf, ExtDigest extDigest) throws IOException {
		Set<String> participants = new HashSet<String>(); 
		participants.add(extDigest.getOwnerId());
		try{
			if(!adminConf.getDefaultParticipants().contains(extDigest.getOwnerId())){
				adminConfigDao.addDefaultParticipant(extDigest.getProjectId(), extDigest.getOwnerId());
			}
		}catch (Exception e) {
			LOG.log(Level.SEVERE,"",e);
		}
		if(!util.isNullOrEmpty(extDigest.getGooglegroupsId())){
			participants.add(extDigest.getGooglegroupsId());
			//add googlegroups to project default participants
			adminConfigDao.addDefaultParticipant(extDigest.getProjectId(), extDigest.getGooglegroupsId());
		}
		
		String rpcUrl = extDigest.getDomain().equals(ForumBotty.PREVIEW_DOMAIN) ? ForumBotty.PREVIEW_RPC_URL : ForumBotty.SANDBOX_RPC_URL;
		Wavelet newWavelet = robot.newWave(extDigest.getDomain(), participants ,"NEW_FORUM_CREATED_MSG",extDigest.getProjectId() + "-digest",rpcUrl);
		LOG.fine("newWavelet -  id: " + newWavelet.getWaveletId() + ", root blip id: " + newWavelet.getRootBlipId());
		if (adminConf.isAtomFeedPublic()) {
			newWavelet.getParticipants().add(System.getProperty("PUBLIC_GROUP"));
			adminConfigDao.addDefaultParticipant(extDigest.getProjectId(), System.getProperty("PUBLIC_GROUP"));
		}
		newWavelet.getParticipants().setParticipantRole(System.getProperty("PUBLIC_GROUP"), Participants.Role.READ_ONLY);

		appendDigestContent(adminConf, extDigest, extDigestDao, robot, newWavelet,true,true);
		ForumPost forumPost = new ForumPost(extDigest.getDomain(), newWavelet);
		forumPost.setProjectId(extDigest.getProjectId());
		forumPost.setBlipCount(0);
		forumPost.setDispayAtom(false);
		forumPostDao.save(forumPost);
		return newWavelet;
	}

	public static void appendDigestContent( AdminConfig adminConf,ExtDigest extDigest,ExtDigestDao extDigestDao, ForumBotty arobot, Wavelet newWavelet, boolean isSubmit, boolean isCreateBottomAdBlip)
			throws IOException {
		String titleStr = extDigest.getName()  + " " + DIGEST_WAVE_STR;
		Blip topBlip = newWavelet.getRootBlip();
		Blip bottomBlip = null;
		
		int startPos = topBlip.getContent().length();
		newWavelet.setTitle(titleStr);
		topBlip.range(startPos,titleStr.length()+1).annotate("style/fontSize","2em").annotate("style/fontWeight","bold");
		//---------------
		arobot.appendPoweredBy(topBlip);
		//-------------------------
		//append forum icon
		String defaultIconUrl = System.getProperty("WAVE_ICON_URL");
		if(extDigest.getInstallerThumbnailUrl() != null && !extDigest.getInstallerThumbnailUrl().equals(defaultIconUrl) && !"".equals(extDigest.getInstallerThumbnailUrl())){
			//appen forum icon
			topBlip.append("\n\n");
			Image forumIcon = new Image(extDigest.getInstallerThumbnailUrl(),80,80,extDigest.getName() + " icon.");
			topBlip.append(forumIcon);
		}
		//append forum description
		if(extDigest.getDescription() != null && !"".equals(extDigest.getDescription())){
			topBlip.append("\n");
			String fontSize = "12pt";
			int s1 = topBlip.getContent().length();
			List<BundledAnnotation> ba1 = BundledAnnotation.listOf("style/fontSize", fontSize,"style/fontWeight", "bold");
			topBlip.at(topBlip.getContent().length()).insert(ba1,  "Forum description: ");
			
			List<BundledAnnotation> ba2 = BundledAnnotation.listOf("style/fontSize", fontSize,"style/fontStyle", "italic");
			topBlip.at(topBlip.getContent().length()).insert(ba2, extDigest.getDescription() );
			
			int e1 = topBlip.getContent().length();
			topBlip.range(s1, e1).annotate(System.getProperty("APP_DOMAIN") + ".appspot.com/forumDescription", "done");
		}
		//append forum site 
		if(extDigest.getForumSiteUrl() != null && !"".equals(extDigest.getForumSiteUrl()) && !extDigest.getForumSiteUrl().startsWith("https://wave.google.com")){
			topBlip.append("\n");
			String fontSize = "12pt";
			int s1 = topBlip.getContent().length();
			List<BundledAnnotation> ba1 = BundledAnnotation.listOf("style/fontSize", fontSize, "link/manual", extDigest.getForumSiteUrl(), "style/fontStyle", "italic");
			topBlip.at(topBlip.getContent().length()).insert(ba1,  "Link to forum site");
			
//			List<BundledAnnotation> ba2 = BundledAnnotation.listOf("style/fontSize", fontSize);
//			topBlip.at(topBlip.getContent().length()).insert(ba2,  " to forum site.");
			
			int e1 = topBlip.getContent().length();
			topBlip.range(s1, e1).annotate(System.getProperty("APP_DOMAIN") + ".appspot.com/forumSite", "done");
		}
		
		if(adminConf.isAdsEnabled() && isCreateBottomAdBlip){
			topBlip.append("\n");
			if(adminConf.getAdsense() != null && !adminConf.getAdsense().getValue().equals("")){
				arobot.appendAd2Blip(topBlip,topBlip.getBlipId(),extDigest.getProjectId(),false);
			}
			bottomBlip = newWavelet.reply("\n");
		}
		boolean isSubmitLocal = false;
		
		 String blipId = null;
		List<JsonRpcResponse> submitResponseList = arobot.submit(newWavelet, arobot.getRpcServerUrl());
		 for(JsonRpcResponse res : submitResponseList){
			  Map<ParamsProperty, Object> dataMap = res.getData();
			  if(dataMap != null && dataMap.get(ParamsProperty.NEW_BLIP_ID)  != null){
				  blipId = String.valueOf(dataMap.get(ParamsProperty.NEW_BLIP_ID));//blip id of the bottom blip
				  LOG.info("blipId to bottom: " + blipId);
				  arobot.addOrUpdateLinkToBottomOrTopForDigestWave(newWavelet.getRootBlip(), blipId, true,false);
				  if(!isCreateBottomAdBlip){
					  extDigest.setLastDigestBlipId(blipId);
					  extDigestDao.save(extDigest);
				  }
				  isSubmitLocal = true;
			  }
		  }
		  if(adminConf.isAdsEnabled() && isCreateBottomAdBlip){//isCreateBottomAdBlip - in case adsense enabled, but still don't need to create bottom blip
			  arobot.appendAd2Blip(bottomBlip,blipId,System.getProperty("BOTTY_OWNER_WAVE_ID"),false );
			  arobot.addOrUpdateLinkToBottomOrTopForDigestWave(bottomBlip, newWavelet.getRootBlipId(), false,false);
			  isSubmitLocal = true;
		  }
		  if(adminConf.isViewsTrackingEnabled()){
			  topBlip.append("\n");
			  arobot.appendViewsCounterGadget(topBlip);
			  arobot.appendViewsTrackingGadget(topBlip, extDigest.getProjectId());
			  isSubmitLocal = true;
		  }else{
			  LOG.warning("views tracking not enabled for: " + extDigest.getProjectId());
		  }
		  
		 if(isSubmitLocal && isSubmit){
			 arobot.submit(newWavelet, arobot.getRpcServerUrl());
		 }
	}

}
