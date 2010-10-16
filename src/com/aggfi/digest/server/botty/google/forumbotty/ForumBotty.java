package com.aggfi.digest.server.botty.google.forumbotty;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.waveprotocol.wave.model.id.WaveId;
import org.waveprotocol.wave.model.id.WaveletId;
import com.aggfi.digest.server.botty.digestbotty.dao.BlipSubmitedDao;
import com.aggfi.digest.server.botty.digestbotty.dao.ExtDigestDao;
import com.aggfi.digest.server.botty.digestbotty.model.BlipSubmitted;
import com.aggfi.digest.server.botty.digestbotty.model.ExtDigest;
import com.aggfi.digest.server.botty.digestbotty.utils.StringBuilderAnnotater;
import com.aggfi.digest.server.botty.digestbotty.utils.WaveUtils;
import com.aggfi.digest.server.botty.google.forumbotty.dao.AdminConfigDao;
import com.aggfi.digest.server.botty.google.forumbotty.dao.ForumPostDao;
import com.aggfi.digest.server.botty.google.forumbotty.dao.UserNotificationDao;
import com.aggfi.digest.server.botty.google.forumbotty.model.AdminConfig;
import com.aggfi.digest.server.botty.google.forumbotty.model.ForumPost;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.servlet.RequestScoped;
import com.google.wave.api.AbstractRobot;
import com.google.wave.api.Annotation;
import com.google.wave.api.Annotations;
import com.google.wave.api.Blip;
import com.google.wave.api.BlipContentRefs;
import com.google.wave.api.Context;
import com.google.wave.api.ElementType;
import com.google.wave.api.Gadget;
import com.google.wave.api.Image;
import com.google.wave.api.JsonRpcResponse;
import com.google.wave.api.ParticipantProfile;
import com.google.wave.api.Participants;
import com.google.wave.api.Wavelet;
import com.google.wave.api.JsonRpcConstant.ParamsProperty;
import com.google.wave.api.event.AbstractEvent;
import com.google.wave.api.event.BlipSubmittedEvent;
import com.google.wave.api.event.GadgetStateChangedEvent;
import com.google.wave.api.event.OperationErrorEvent;
import com.google.wave.api.event.WaveletSelfAddedEvent;
import com.google.wave.api.event.WaveletSelfRemovedEvent;
import com.google.wave.api.impl.DocumentModifyAction.BundledAnnotation;
import com.vegalabs.general.server.command.Command;
import com.vegalabs.general.server.rpc.JsonRpcRequest;
import com.vegalabs.general.server.rpc.util.Util;
import com.aggfi.digest.server.botty.google.forumbotty.admin.CommandType;

@Singleton
public class ForumBotty extends AbstractRobot {
  private static final String USE_HOME_END_KEYBOARD_KEYS = "(You can use HOME/END keyboard keys to navigate to top/bottom)";
public static final String TO_BOTTOM = "Link to Bottom";
private static final String TO_TOP = "Link to Top";
private static final Logger LOG = Logger.getLogger(ForumBotty.class.getName());
  private static final boolean DEBUG_MODE = false;
  
  public static final String SANDBOX_DOMAIN = "wavesandbox.com";
  public static final String PREVIEW_DOMAIN = "googlewave.com";

  public static final String PREVIEW_RPC_URL = "http://gmodules.com/api/rpc";
  public static final String SANDBOX_RPC_URL = "http://sandbox.gmodules.com/api/rpc";

  protected  String OAUTH_TOKEN = null;
  protected  String OAUTH_KEY = null;
  protected  String OAUTH_SECRET = null;
  protected  String SECURITY_TOKEN = null;
  
  protected static String DIGEST_WAVE_DOMAIN = null;
  protected static String DIGEST_WAVE_ID = null;
 
  
  protected Injector injector = null;
  protected Util util = null;
  protected ForumPostDao forumPostDao = null;
  protected UserNotificationDao userNotificationDao = null;
  protected AdminConfigDao adminConfigDao = null;
  protected ExtDigestDao extDigestDao;

  private String domain = PREVIEW_DOMAIN;//SANDBOX_DOMAIN;
  
  private Cache cache;


  @Inject
  public ForumBotty(Injector injector, Util util, ForumPostDao forumPostDao,
      AdminConfigDao adminConfigDao, UserNotificationDao userNotificationDao, ExtDigestDao extDigestDao) {
    this.injector = injector;
    this.util = util;
    this.forumPostDao = forumPostDao;
    this.userNotificationDao = userNotificationDao;
    this.adminConfigDao = adminConfigDao;
    this.extDigestDao = extDigestDao;

    OAUTH_TOKEN = System.getProperty("OAUTH_TOKEN");
    OAUTH_KEY = System.getProperty("OAUTH_KEY");
    OAUTH_SECRET = System.getProperty("OAUTH_SECRET");
    SECURITY_TOKEN = System.getProperty("SECURITY_TOKEN");
    DIGEST_WAVE_DOMAIN = System.getProperty("DIGEST_WAVE_DOMAIN");
    DIGEST_WAVE_ID = System.getProperty("DIGEST_WAVE_ID");
    initOauth();
    try {
    	Map<String, Integer> props = new HashMap<String, Integer>();
        props.put(GCacheFactory.EXPIRATION_DELTA, 60*60*12);
        cache = CacheManager.getInstance().getCacheFactory().createCache(props);
    } catch (CacheException e) {
        LOG.log(Level.SEVERE,"cache init",e);
    }

  }
  
  public ForumBotty(){
	  
  }

  public void initAdminConfig() {
    String projectId = "wave-api";

    adminConfigDao.addDefaultTag(projectId, "wave-api");
    adminConfigDao.addAutoTagRegex(projectId, "gadget", "gadget");
    adminConfigDao.addAutoTagRegex(projectId, "robot", "robot");
    adminConfigDao.addAutoTagRegex(projectId, "embed", "embed");
    adminConfigDao.addAutoTagRegex(projectId, "java", "java");
    adminConfigDao.addAutoTagRegex(projectId, "python", "python");
  }

  public String getDomain() {
    return domain;
  }

  public void initOauth() {
    setupVerificationToken(OAUTH_TOKEN, SECURITY_TOKEN);

    if (this.domain.equals(SANDBOX_DOMAIN)) {
      setupOAuth(OAUTH_KEY, OAUTH_SECRET, SANDBOX_RPC_URL);
    }
    if (this.domain.equals(PREVIEW_DOMAIN)) {
      setupOAuth(OAUTH_KEY, OAUTH_SECRET, PREVIEW_RPC_URL);
    }

    setAllowUnsignedRequests(true);
  }

  public String getRpcServerUrl() {
    if (this.domain.equals(SANDBOX_DOMAIN)) {
      return SANDBOX_RPC_URL;
    }
    if (this.domain.equals(PREVIEW_DOMAIN)) {
      return PREVIEW_RPC_URL;
    }
    return null;
  }

  public String getCurrentProjectId(AbstractEvent event) {
    return event.getBundle().getProxyingFor();
  }

  @Capability(contexts = {Context.ALL})
  @Override
  public void onWaveletSelfAdded(WaveletSelfAddedEvent event) {
	  LOG.log(Level.INFO, "onWaveletSelfAdded:" + event.toString());

	  Set<String> projectIds = findProjectIdsFromPaticipants(event.getWavelet().getParticipants());
	  for(String projectId : projectIds){
		  String proxyFor = event.getBundle().getProxyingFor();
		  Wavelet wavelet = event.getWavelet();
		  Blip blip = event.getBlip();

		  try {
			actOnBottyAdded(projectId, proxyFor, wavelet, blip,null);
		} catch (IOException e) {
			LOG.log(Level.SEVERE,"",e);
		}
		  
		  if(!isDigestAdmin(proxyFor) && wavelet.getRootBlip() != null && wavelet.getRootBlip().getContent().length() > 2){
			  AdminConfig adminConfig = adminConfigDao.getAdminConfig(projectId);
			  if( adminConfig.isAdsEnabled()){
				  appendAd2Blip(wavelet.getRootBlip(),wavelet.getRootBlip().getBlipId(), projectId, false);
			  }
			  ForumPost entry = forumPostDao.getForumPost(wavelet.getWaveId().getDomain(), wavelet.getWaveId().getId());
			  addBack2Digest2RootBlip(projectId, wavelet.getRootBlip(), entry);
		  }
	  }
	  
  }

	public void appendAd2Blip(Blip blip, String blipId, String projectId, boolean isAdReplyBlip) {
		String gadgetUrl = "http://" + System.getProperty("APP_DOMAIN") + ".appspot.com/serveAd?id=" + projectId;
		//check if blip already contains the add gadget. 
		Gadget gadget = null;
		BlipContentRefs gadgetRef = blip.first(ElementType.GADGET,Gadget.restrictByUrl(gadgetUrl));
		if(gadgetRef != null){
			gadget = Gadget.class.cast(gadgetRef.value());
			if(gadget == null){
				gadget = new Gadget(gadgetUrl);
				if(isAdReplyBlip){
					blip = blip.reply(); //TODO should be continueThread instead of reply - Pamela said should work next week 26.08.2010
				}else{
					blip.append("\n");
				}
				if(!blip.isRoot()){
					blip.append("\n");
				}
				blip.at(blip.getContent().length()).insert(gadget);
				Map<String,String> out = new HashMap<String, String>();
			    out.put("projectId", projectId);
			    out.put("domain", "http://" + System.getProperty("APP_DOMAIN") + ".appspot.com");
			    out.put("eventValue", blipId);
			   blip.first(ElementType.GADGET,Gadget.restrictByUrl(gadget.getUrl())).updateElement(out);
			}
		}
		
		
		
	}


protected void actOnBottyAdded(String projectId,
		String proxyFor, Wavelet wavelet, Blip blip, String modifiedBy) throws IOException {
	if (projectId == null) {
		  LOG.log(Level.SEVERE, "actOnBottyAdded:Missing proxy-for project id");
		  return;
	  }else{
		  LOG.log(Level.INFO, "actOnBottyAdded: project id: " + projectId);
	  }
	  
	  // If this is from the "*-digest" proxy, skip processing.
	  if (isDigestWave(proxyFor)) {
		  return;
	  }     

	  if (isDigestAdmin(proxyFor)) {
		  wavelet.setTitle("DigestBotty [Admin Gadget]");
		  submit(wavelet, PREVIEW_RPC_URL);
	  }else{
		  if (isWorthy(wavelet)) {
			// Add default participants
			  LOG.log(Level.INFO, "adding default participants: " + projectId);
			  List<String> participants = this.adminConfigDao.getAdminConfig(projectId).getDefaultParticipants();
			  if(participants.size() == 0){
				  LOG.warning("participants.size() == 0");
			  }
			  for (String participant : participants) {
				  if(!wavelet.getParticipants().contains(participant)){
					  wavelet.getParticipants().add(participant);
				  }
			  }
			  ForumPost entry = addOrUpdateDigestWave(projectId, wavelet,blip,modifiedBy);

			  for (String contributor : wavelet.getRootBlip().getContributors()) {
				  if (isNormalUser(contributor)) {
					  entry.addContributor(contributor);
				  }
			  }

			  // Apply default and auto tags to the wave
			  applyAutoTags(blip, projectId);
			  
			  AdminConfig adminConfig = adminConfigDao.getAdminConfig(projectId);
			  if(adminConfig.isViewsTrackingEnabled()){
				  appendViewsTrackingGadget(wavelet.getRootBlip(), projectId);
			  }
			  try{
				  submit(wavelet, PREVIEW_RPC_URL);
			  }catch(java.io.IOException timeoutException){
				  LOG.log(Level.WARNING, "first time", timeoutException);
//				  try{
//					  submit(wavelet, PREVIEW_RPC_URL);
//				  }catch(java.io.IOException timeoutException2){
//					  LOG.log(Level.WARNING, "second time", timeoutException);
//					  LOG.log(Level.WARNING, "removing ForumPost entry: " + entry.toString());
//					  forumPostDao.remove(entry);
//				  }
			  }
		  }
		 
	  }
	  
	  
}

protected void submitWavelet(Wavelet wavelet) {
	 LOG.log(Level.INFO, "Entering submitWavelet");
	try {
		  submit(wavelet, getRpcServerUrl());
	  } catch (IOException e) {
//		  try{
//			  LOG.log(Level.SEVERE, "Trying to resubmit",e);
//			  submit(wavelet, getRpcServerUrl());
//		  }catch (Exception e1) {
//			  LOG.log(Level.SEVERE, "Failed after resubmitting!!!",e1);
//		}
		  LOG.log(Level.WARNING, "Wavelet submition failed", e);
	  }
}

/**
 * Returns forum entry for the wavelet that contains the submitted blip - new or existing
 * @param projectId
 * @param wavelet
 * @param blip
 * @param modifiedBy
 * @return
 */
public ForumPost addOrUpdateDigestWave(String projectId, Wavelet wavelet, Blip blip, String modifiedBy) {
	LOG.log(Level.FINE, "adding to digest: project id: " + projectId);
	ForumPost entry = forumPostDao.getForumPost (wavelet.getDomain(), wavelet.getWaveId().getId(), projectId);
	// Update contributor list if this is not robot or agent

	if (entry != null && entry.getProjectId().equals(projectId)) {
		if (isNormalUser(modifiedBy)) {
			entry.addContributor(modifiedBy);
		}
		if(modifiedBy != null){
			entry.setUpdater(modifiedBy);
			entry.setBlipCount(entry.getBlipCount()+1);
			if(entry.getCreator() == null){
				entry.setCreator(modifiedBy);
			}
		}
		
		// Existing wavelet in datastore
//		entry.setTitle(wavelet.getTitle());
		entry.setLastUpdated(new Date());
		applyAutoTags(blip, projectId);
		entry = forumPostDao.syncTags(projectId, entry, wavelet);
//		updateEntryInDigestWave(entry); //TODO - maybe I need to create a different "hot and noisy" wave
		
		
		forumPostDao.save(entry);
	} else {
		LOG.fine( " event.proxy: " + projectId);
		entry = new ForumPost(wavelet.getDomain(), wavelet);
		entry.setProjectId(projectId);
//		entry = forumPostDao.syncTags(projectId, entry, wavelet);
		entry.setBlipCount(wavelet.getBlips().size());
		applyAutoTags(blip, projectId);
		entry = forumPostDao.syncTags(projectId, entry, wavelet);
		//add to digest wave-------------------
		addEntry2DigestWave(entry);
		//-------------------------
		
		
		forumPostDao.save(entry);
		//check if the wave was imported. if so - import the blips
		Map<String,Blip> blips2Import = wavelet.getBlips();
		Set<String> keys = blips2Import.keySet();
		for(String key : keys){
			Blip blip2Import = blips2Import.get(key);
			//save all blips
			saveBlipSubmitted(blip2Import.getCreator(), blip2Import, projectId, entry.isDispayAtom());
		}
	}
	
//	forumPostDao.save(entry);
	return entry;
}

  private boolean isNormalUser(String userId) {
    return userId != null && !(userId.endsWith("appspot.com") || userId.endsWith("gwave.com"));
  }

  @Override
  @Capability(contexts = {Context.ROOT, Context.SELF})
  public void onBlipSubmitted(BlipSubmittedEvent event) {
	  LOG.fine("Entering onBlipSubmitted");
	  if(event.getBlip() == null || event.getBlip().getContent() == null || event.getBlip().getContent().length()  < 2){
		  LOG.log(Level.FINE, "The content is not worthy, slipping processing");
		  return;
	  }
    // If this is from the "*-notify" proxy, skip processing.
    if (isNotifyProxy(event.getBundle().getProxyingFor())) {
      return;
    }

    // If this is from the "*-digest" proxy, skip processing.
    if (isDigestWave(event.getBundle().getProxyingFor())) {
      return;
    } 
    //if it is a gadget event - skip
    if (isDigestAdmin(event.getBundle().getProxyingFor())) {
        return;
      } 
    
    
    
    // If this is unworthy wavelet (empty), skip processing.
    if (!isWorthy(event.getWavelet()) && event.getBlip().isRoot()) {
    	event.getWavelet().setTitle(event.getBlip().getContent().split(" ")[0].replace("\n", ""));
    }else  if (!isWorthy(event.getWavelet())){
    	return;
    }

    Wavelet wavelet = event.getWavelet();
   
    Set<String> projectIds = findProjectIdsFromPaticipants(event.getWavelet().getParticipants());
    
//    String projectId = getCurrentProjectId(event);
    if (projectIds.size() == 0) {
    	//cannot be - 
    	//or blip submitted in digest wave
    	
      LOG.log(Level.WARNING, "onBlipSubmitted:Missing (if not digest proxy) proxy-for project id" + ", waveId: " + event.getWavelet().getWaveId().toString() + ", proxyFor: " + event.getBundle().getProxyingFor());
      return;
    }
    
    for(String projectId : projectIds){
    	LOG.info("Working on projectId: " + projectId);
    	if(event.getBlip().isRoot()){
        	List<String> participants = this.adminConfigDao.getAdminConfig(projectId).getDefaultParticipants();
    		  if(participants.size() == 0){
    			  LOG.warning("participants.size() == 0");
    		  }
    		  for (String participant : participants) {
    			  if(!wavelet.getParticipants().contains(participant)){
    				  wavelet.getParticipants().add(participant);
    			  }
    		  }
    		  AdminConfig adminConfig = adminConfigDao.getAdminConfig(projectId);
    		  if(adminConfig.isViewsTrackingEnabled()){
    			  appendViewsTrackingGadget(wavelet.getRootBlip(), projectId);
    		  }
        }
        
        ForumPost entry = addOrUpdateDigestWave(projectId, wavelet, event.getBlip(),event.getModifiedBy());
        
        
        try{
        	if(!wavelet.getTitle().equals(entry.getTitle())){
            	//update entry title in the digest wave
            	List<ExtDigest> extList = extDigestDao.retrDigestsByProjectId(projectId);
            	ExtDigest extDigest = extList.get(0);
            	Wavelet digestWavelet = null;
            	String digestWaveDomain = extDigest.getDomain();
        	    String digestWaveId = extDigest.getWaveId();
            	try {
            		digestWavelet = fetchWavelet( new WaveId(digestWaveDomain, digestWaveId), new WaveletId(digestWaveDomain, "conv+root"), extDigest.getProjectId() + "-digest", getRpcServerUrl());
            	} catch (IOException e) {
            		LOG.log(Level.SEVERE,"",e);
            	}
            	Blip ablip = digestWavelet.getBlip(entry.getDigestBlipId());
            	WaveUtils.updateLinkTitleInDigestBlip(wavelet.getTitle(), entry.getTitle(), ablip, "link/wave");
            	submit(digestWavelet, getRpcServerUrl());
            	entry.setTitle(wavelet.getTitle());
            	forumPostDao.save(entry);
        	}
        }catch(RuntimeException re){
        	LOG.log(Level.SEVERE,"cannot update digest blip title from: " + entry.getTitle() + " to " + wavelet.getTitle(),re);
        } catch (IOException e) {
        	LOG.log(Level.SEVERE,"cannot update digest blip title from: " + entry.getTitle() + " to " + wavelet.getTitle(),e);
		}

      //here is the place to save blipSubmitted
        saveBlipSubmitted(event.getModifiedBy(), event.getBlip(), projectId, entry.isDispayAtom());
        
        //check if there's link to Digest Wave in the Root Blip, if one missing add it with annotation.
    	  if(wavelet.getRootBlip() != null){
    		  AdminConfig adminConfig = adminConfigDao.getAdminConfig(projectId);
    		  if( adminConfig.isAdsEnabled() && adminConfig.getAdsense() != null && !"".equals(adminConfig.getAdsense().getValue())){ //if forum not linked to adsense code - do not insert ad
    			  appendAd2Blip(wavelet.getRootBlip(),wavelet.getRootBlip().getBlipId(), projectId, false);
    			  if(entry.getRootBlipsWithoutAdCount() >= 5){ //XXX - should be configurable from admin gadget
    				  appendAd2Blip(event.getBlip(),event.getBlip().getBlipId(), projectId, true);
    				  entry.setRootBlipsWithoutAdCount(entry.getRootBlipsWithoutAdCount() - 5);
    			  }
    		  }
    		  if(event.getBlip().getParentBlipId() == null)
    			  entry.setRootBlipsWithoutAdCount(entry.getRootBlipsWithoutAdCount() +1);
    		  
    		  addBack2Digest2RootBlip(projectId, wavelet.getRootBlip(), entry);
    		  try{ 
    			  forumPostDao.save(entry);
    		  }catch(Exception e){
    			  LOG.log(Level.SEVERE,"",e);
    		  }
    	  }
    }
	  
   
  }

  private Set<String> findProjectIdsFromPaticipants(Participants participants) {
	  Set<String> projectIds = new LinkedHashSet<String>();
	  Set<String> blackListIds = new LinkedHashSet<String>();
	  String appDomain = System.getProperty("APP_DOMAIN");
	  for(String participant : participants){
		  if(participant.startsWith(appDomain + "+") && participant.endsWith("@appspot.com")){
			  if(participant.contains("-digest")){
				  String blackListId = participant.replace("-digest", "");
				  blackListIds.add(blackListId);
				  if(projectIds.contains(blackListId)){
					  projectIds.remove(blackListId);
				  }
				  continue;//skip
			  }
			  if(blackListIds.contains(participant)){
				  continue;
			  }
			  int start = participant.indexOf("+");
			  int end = participant.indexOf("@");
			  String projectId = participant.substring(start +1, end);
			  projectIds.add(projectId);
		  }
	  }
	return projectIds;
}

public void addBack2Digest2RootBlip(String projectId,
			Blip rootBlip, ForumPost entry) {
		Annotations annotations = rootBlip.getAnnotations();
		  String backtodigestAnnotationName = System.getProperty("APP_DOMAIN") + ".appspot.com/backtodigest#" + projectId;
		  String backtodigestAnnotationNameOld = System.getProperty("APP_DOMAIN") + ".appspot.com/backtodigest";
		  List<Annotation> annotationsList =  annotations.get(backtodigestAnnotationName);
		  List<Annotation> annotationsListOld =  annotations.get(backtodigestAnnotationNameOld);
		  if((annotationsList == null || annotationsList.size() == 0) && (annotationsListOld == null || annotationsListOld.size() == 0)){
			  List<ExtDigest> digestsList = extDigestDao.retrDigestsByProjectId(projectId);
			  if(digestsList != null && digestsList.size() > 0 ){
				  rootBlip.append("  ");
				  String forumName = digestsList.size() > 0 ? digestsList.get(0).getName() : "";
				  String back2digestWaveStr = makeBackStr(forumName);
				  if(rootBlip.getContent().indexOf(back2digestWaveStr) > 0){
					  return;
				  }
				  LOG.fine("content: " + rootBlip.getContent());
				  BlipContentRefs rootBlipRef = rootBlip.at(rootBlip.getContent().length());
				  String blipRef = "waveid://" + digestsList.get(0).getDomain() + "/" + digestsList.get(0).getWaveId() + "/~/conv+root/" + entry.getDigestBlipId();
				  List<BundledAnnotation> baList = BundledAnnotation.listOf("link/manual",blipRef,"style/fontSize", "8pt",backtodigestAnnotationName,"done");
				  rootBlipRef.insert(baList, back2digestWaveStr);
				  List<BundledAnnotation> ba2 = BundledAnnotation.listOf( "style/fontSize", "8pt");
				  String useKeysStr = " " + USE_HOME_END_KEYBOARD_KEYS;
				  rootBlip.at(rootBlip.getContent().length()).insert(ba2,useKeysStr);
				  LOG.fine("updated addBack2Digest2RootBlip " + rootBlip.getContent());
				  
				  //now add social buttons
				  AdminConfig adminConfig = adminConfigDao.getAdminConfig(entry.getProjectId());
				  LOG.info("Adding social buttons for: "  + adminConfig);
				  boolean isDiggButtonEnabled = adminConfig.isDiggBtnEnabled();
				  boolean isBuzzButtonEnabled = adminConfig.isBuzzBtnEnabled();
				  boolean isTweetButtonEnabled = adminConfig.isTweetBtnEnabled();
				  boolean isFaceButtonEnabled = adminConfig.isFaceBtnEnabled();
				  try {
					addSocialButtons(rootBlip,entry.getId(),entry.getTitle(),digestsList.get(0).getName(),
							  rootBlip.getContent(),digestsList.get(0).getInstallerThumbnailUrl(),  isDiggButtonEnabled,isBuzzButtonEnabled,isTweetButtonEnabled,isFaceButtonEnabled);
				} catch (UnsupportedEncodingException e) {
					LOG.severe(e.getMessage());
				}
			  }
			  
			 
			  
		  }
	}

public String makeBackStr(String forumName) {
	String back2digestWaveStr = "\nBack to \"" + forumName + "\" digest";
	return back2digestWaveStr;
}

private void saveBlipSubmitted(String modifier, Blip blip, String projectId, boolean isPublic) {
	try{
			
		    String creator = blip.getCreator();
			String replytoCreator = blip.getParentBlip() != null ? blip.getParentBlip().getCreator() : null;
			List<String> contributors = blip.getContributors();
			List<String> replytoContributors = blip.getParentBlip() != null ? blip.getParentBlip().getContributors() : null;
			long version = blip.getVersion();
			String blipId = blip.getBlipId();
			String parentBlipId = blip.getParentBlipId();
			String waveletId = blip.getWaveletId().getDomain()+"!"+blip.getWaveletId().getId();
			String waveId = blip.getWaveId().getDomain()+"!"+blip.getWaveId().getId();
			long blipLength = blip.getContent().length();
			long createdTime = blip.getLastModifiedTime();
			BlipSubmitedDao blipSubmitedDao = injector.getInstance(BlipSubmitedDao.class);
			BlipSubmitted blipSubmitted = null;
			blipSubmitted = new BlipSubmitted(creator, replytoCreator, modifier, contributors, replytoContributors, version, blipId, parentBlipId, waveletId, waveId, projectId, blipLength, createdTime, null, null);
			if(isPublic){
				blipSubmitted.setBlipContent(new Text(blip.getContent()));
			}
			blipSubmitedDao.save(blipSubmitted);
	   }catch(Exception e){
		   LOG.warning(e.getMessage());
	   }
}
  
  private boolean isDigestWave(String proxyingFor) {
    if (proxyingFor != null && proxyingFor.endsWith("-digest")) {
      return true;
    } else {
      return false;
    }
  }
  
  private boolean isDigestAdmin(String proxyingFor) {
	    if (proxyingFor != null && proxyingFor.equals("gadget")) {
	      return true;
	    } else {
	      return false;
	    }
	  }

  private void addEntry2DigestWave(ForumPost entry) {
	  LOG.entering(this.getClass().getName(), "addEntry2DigestWave", entry);
	  Wavelet digestWavelet = null;
	  ExtDigestDao extDigestDao = injector.getInstance(ExtDigestDao.class);
	  List<ExtDigest> entries =  extDigestDao.retrDigestsByProjectId(entry.getProjectId());
	  AdminConfig adminConfig = adminConfigDao.getAdminConfig(entry.getProjectId());
	  ExtDigest digest = null;
	  if(entries.size() > 0){
		  digest = entries.get(0);
		  String digestWaveDomain = digest.getDomain();
		  String digestWaveId = digest.getWaveId();
		  try{
			  digestWavelet = fetchWavelet( new WaveId(digestWaveDomain, digestWaveId), new WaveletId(digestWaveDomain, "conv+root"), entry.getProjectId() + "-digest",  getRpcServerUrl());
		  }catch (IOException e) {
			  LOG.log(Level.WARNING,"can happen if the robot was removed manually from the wave. proxyFor: " + entry.getProjectId() + "-digest",e);
			  LOG.log(Level.WARNING, "digestWaveDomain: " + digestWaveDomain + ", digestWaveId: " + digestWaveId);
			  return;
		  }
		  String entryTitle = entry.getTitle();
		  Blip blip = null;
		  //check the digest version
		  if(adminConfig.isAdsEnabled()){
			  blip = digestWavelet.getRootBlip().reply();// ads enabled
			  LOG.info("addEntry2DigestWave: added new digest blip - reply to rootblip: " + entry.getTitle());
		  }else{
			  blip = digestWavelet.reply("\n");// old  version
			  LOG.info("addEntry2DigestWave: added new digest blip - reply to wavelet: " + entry.getTitle());
		  }
		  
		  
		  StringBuilderAnnotater sba = new StringBuilderAnnotater(blip);
		  sba.append(entryTitle, StringBuilderAnnotater.LINK_WAVE, entry.getId());//append link to post
		  if(entry.getCreator() != null && !"".equals(entry.getCreator())){
			  sba.append("\n", null, null);
			  sba.append(" [", null, null);
			  sba.append("By: " + entry.getCreator(), StringBuilderAnnotater.STYLE_FONT_STYLE, StringBuilderAnnotater.STYLE_ITALIC);
			  sba.append("] ", null, null);
		  }
		  sba.flush2Blip();
		 
		  try {
			  LOG.log(Level.FINER, "trying to get newBlipIs from JsonRpcResponse");
			  String blipId = null;
			  List<JsonRpcResponse> submitResponseList = submit(digestWavelet, getRpcServerUrl());
			  for(JsonRpcResponse res : submitResponseList){
				  Map<ParamsProperty, Object> dataMap = res.getData();
				  if(dataMap != null && dataMap.get(ParamsProperty.NEW_BLIP_ID)  != null){
					  blipId = String.valueOf(dataMap.get(ParamsProperty.NEW_BLIP_ID));
					  entry.setDigestBlipId(blipId);
					  break;
				  }
			  }
			  if(blipId == null){
				  for(JsonRpcResponse res : submitResponseList){
					  Map<ParamsProperty, Object> dataMap = res.getData();
					  if(dataMap != null && dataMap.get(ParamsProperty.BLIP_ID)  != null){
						  blipId = String.valueOf(dataMap.get(ParamsProperty.BLIP_ID));
						  entry.setDigestBlipId(blipId);
						  break;
					  }
				  }
			  }
			  if(!adminConfig.isAdsEnabled()){
				  if(digest.getLastDigestBlipId() == null){
					  digest.setLastDigestBlipId(blip.getBlipId());
				  }
				  addOrUpdateLinkToBottomOrTopForDigestWave(digestWavelet.getRootBlip(), digest.getLastDigestBlipId(), true, true); 
			  }
			  extDigestDao.save(digest);
		  } catch (IOException e) {
			  LOG.log(Level.SEVERE, "",e);
		  }  
		  //now update digestWave forumPost
		  ForumPost digestForumPost = forumPostDao.getForumPost(digest.getDomain(),digest.getWaveId(),digest.getProjectId());
		  if(digestForumPost != null){
			  digestForumPost.setBlipCount(digestForumPost.getBlipCount() + 1);
		  }
	  }
  }
  
  private void addSocialButtons(Blip blip, String waveId,String title, String forumName, String message, String imageUrl,boolean isDiggButtonEnabled,
		  boolean isBuzzButtonEnabled,boolean isTweetButtonEnabled, boolean isFaceEnabled) throws UnsupportedEncodingException {
	  LOG.info("entering addSocialButtons");
	  String embeddedUrl = "http://" + System.getProperty("APP_DOMAIN") + ".appspot.com/showembedded?waveId=" + URLEncoder.encode(waveId, "UTF-8")  + "&title=" + URLEncoder.encode(title, "UTF-8");
	  if(!isDiggButtonEnabled && !isDiggButtonEnabled && !isTweetButtonEnabled && !isBuzzButtonEnabled && !isFaceEnabled){
		  LOG.info("exiting addSocialButtons by return: forumName: " + forumName);
		  return;
	  }else{
		  blip.append("\n");
	  }
	  String back2digestWaveStr = makeBackStr(forumName);
	  message = message.replaceAll(back2digestWaveStr, "");
	  message = message.replaceAll(USE_HOME_END_KEYBOARD_KEYS, "");
	  message = message.replaceFirst ("\\(", "");
	  message = message.replaceFirst ("\\)", "");
	  if(message.length() > 150){
		  message = message.substring(0, 147) + "...";
	  }
	  if(isDiggButtonEnabled){
		  String imgUrl = "http://widgets.digg.com/img/button/diggThisDigger.png";
		  if(!isButtonExists(imgUrl,blip)){
			  MessageFormat fmt = new MessageFormat("http://digg.com/submit?url={0}&amp;title={1}");
			  Object[] args = {URLEncoder.encode(embeddedUrl,  "UTF-8"),URLEncoder.encode(message, "UTF-8")};
			  Image diggImage = new Image(imgUrl, 16, 16, "digg it");
			  blip.append(diggImage);
			  List<BundledAnnotation> baList = BundledAnnotation.listOf("link/manual",fmt.format(args));
			  BlipContentRefs blifRef = blip.at(blip.getContent().length());
			  LOG.fine("inserting digg button");
			  blifRef.insert(baList, "digg");
			  blip.append(" ");
		  }
		 
	  }
	  if(isBuzzButtonEnabled){
		  String imgUrl = "http://code.google.com/apis/buzz/images/google-buzz-16x16.png";
		  if(!isButtonExists(imgUrl,blip)){
			  MessageFormat fmt = new MessageFormat("http://www.google.com/buzz/post?message={0}&url={1}&imageurl={2}");
			  Object[] args = {URLEncoder.encode(message, "UTF-8"),URLEncoder.encode(embeddedUrl, "UTF-8"), imageUrl};
			  Image buzzImage = new Image(imgUrl, 16, 16, "buzz it");
			  blip.append(buzzImage);
			  List<BundledAnnotation> baList = BundledAnnotation.listOf("link/manual",fmt.format(args));
			  BlipContentRefs blifRef = blip.at(blip.getContent().length());
			  LOG.fine("inserting buzz button");
			  blifRef.insert(baList, "buzz");
			  blip.append(" ");
		  }
		 
	  }
	  if(isTweetButtonEnabled){
		  String imgUrl = "http://twitter-badges.s3.amazonaws.com/t_mini-b.png";
		  if(!isButtonExists(imgUrl,blip)){
			  MessageFormat fmt = new MessageFormat("http://twitter.com/share?text={0}&url={1}");
			  String tweetMsg  = message.length() > 150 ? message.substring(0,147) + "..." : message;
			  Object[] args = {URLEncoder.encode(tweetMsg, "UTF-8"),URLEncoder.encode(embeddedUrl, "UTF-8")};
			  
			  Image tweetImage = new Image(imgUrl, 16, 16, "tweet it");
			  blip.append(tweetImage);
			  BlipContentRefs blifRef = blip.at(blip.getContent().length());
			  List<BundledAnnotation> baList = BundledAnnotation.listOf("link/manual",fmt.format(args));
			  LOG.fine("inserting tweet button");
			  blifRef.insert(baList, "tweet");
			  blip.append(" ");
		  }
		 
	  }
	  if(isFaceEnabled){
		  String imgUrl = "http://lh3.ggpht.com/_tsWs83xehHE/TH432yol8-I/AAAAAAAAFcg/MK-GOldQN4M/facebook_share.png";
		  if(!isButtonExists(imgUrl,blip)){
			  MessageFormat fmt = new MessageFormat("http://www.facebook.com/sharer.php?u={0}&t={1}");
			  Object[] args = {URLEncoder.encode(embeddedUrl, "UTF-8"), URLEncoder.encode(title, "UTF-8")};
			  
			  Image tweetImage = new Image(imgUrl, 16, 16, "tweet it");
			  blip.append(tweetImage);
			  BlipContentRefs blifRef = blip.at(blip.getContent().length());
			  List<BundledAnnotation> baList = BundledAnnotation.listOf("link/manual",fmt.format(args));
			  LOG.fine("inserting facebook button");
			  blifRef.insert(baList, "share");
			  blip.append(" ");
		  }
		 
	  }
	  LOG.info("exiting addSocialButtons");
}

private boolean isButtonExists(String imgUrl, Blip blip) {
	LOG.info("isButtonExists: " + imgUrl + ", blipId: " + blip.getBlipId());
	//check if blip already contains the add gadget. 
	BlipContentRefs imageRef = blip.first(ElementType.IMAGE ,Gadget.restrictByUrl(imgUrl));
	if(imageRef.value() == null){
		return false;
	}
	return true;
}

private void updateEntryInDigestWave(ForumPost entry) {
	  LOG.entering(this.getClass().getName(), "updateEntryInDigestWave", entry);
	  try{
		  Wavelet digestWavelet = null;
		  ExtDigestDao extDigestDao = injector.getInstance(ExtDigestDao.class);

		  ExtDigest digest = extDigestDao.retrDigestsByProjectId(entry.getProjectId()).get(0);
		  String digestWaveDomain = digest.getDomain();
		  String digestWaveId = digest.getWaveId();
		  LOG.log(Level.FINER, "digestWaveDomain: " + digestWaveDomain + ", digestWaveId: " + digestWaveId);
		  try{
			  digestWavelet = fetchWavelet( new WaveId(digestWaveDomain, digestWaveId), new WaveletId(digestWaveDomain, "conv+root"), entry.getProjectId() + "-digest",  getRpcServerUrl());
		  }catch (IOException e) {
			  LOG.log(Level.FINER,"can happen if the robot was removed manually from the wave.",e);
		  }
		  String entryTitle = entry.getTitle();
		  if(entry.getDigestBlipId() != null && digestWavelet.getBlips().get(entry.getDigestBlipId()) != null){
			  Blip blip = digestWavelet.getBlips().get(entry.getDigestBlipId());
			  blip.all().delete();
			  
			  StringBuilderAnnotater sba = new StringBuilderAnnotater(blip);
			  sba.append(entryTitle, StringBuilderAnnotater.LINK_WAVE, entry.getId());
			  sba.append(" [", null, null);
			  sba.append(" By: " + entry.getCreator() + ", ", StringBuilderAnnotater.STYLE_FONT_STYLE, StringBuilderAnnotater.STYLE_ITALIC);
			  sba.append("Last: " + entry.getUpdater() + ", ", StringBuilderAnnotater.STYLE_FONT_STYLE, StringBuilderAnnotater.STYLE_ITALIC);
			  sba.append("Blips: " + entry.getBlipCount(), StringBuilderAnnotater.STYLE_FONT_STYLE, StringBuilderAnnotater.STYLE_ITALIC);
			  sba.append(" ] ", null, null);
			  sba.flush2Blip();
			
			  try {
				  submit(digestWavelet, getRpcServerUrl());
			  } catch (IOException e) {
				  LOG.log(Level.SEVERE, "",e);
				  throw new RuntimeException(e);
			  }  
		  }else{
			  LOG.log(Level.SEVERE, "No digest blip id! entry.getDigestBlipId: " + entry.toString());
		  }
	  }catch (Exception e) {
		  LOG.log(Level.SEVERE, "",e);
	  }
	  LOG.log(Level.INFO, "Updated DIGEST_WAVE with: " + entry.getTitle());

  }
  
  private boolean isWorthy(Wavelet wavelet) {
    return !wavelet.getTitle().trim().equals("");
  }

  public void applyAutoTags(Blip blip, String projectId) {
	  if(blip == null) return;
	  Pattern pattern = null;;
	  String tag = null;
	  try{
		  String content = blip.getContent();
		  AdminConfig adminConfig = this.adminConfigDao.getAdminConfig(projectId);
		  if(adminConfig != null && adminConfig.getAutoTagRegexMap() != null){
			  for (Entry<String, Pattern> entry : adminConfig.getAutoTagRegexMap().entrySet()) {
				  tag = entry.getKey();
				  pattern = entry.getValue();

				  Matcher m = null;
				  m = pattern.matcher(content);
				  if (m.find()) {
					  blip.getWavelet().getTags().add(tag);
				  }
			  }
		  }
	  }catch (Exception e) {
		  LOG.log(Level.SEVERE, "tag: " + tag + ", pattern: " + pattern,e);
	  }
  }
  
  public boolean applyAutoTag(Blip blip, String projectId, String tagName) {
	  String content = blip.getContent();
	  boolean isTagApplied = false;
	  for (Entry<String, Pattern> entry : this.adminConfigDao.getAdminConfig(projectId)
			  .getAutoTagRegexMap().entrySet()) {
		  String tag = entry.getKey();
		  if(tag.equals(tagName)){
			  Pattern pattern = entry.getValue();

			  Matcher m = null;
			  m = pattern.matcher(content);
			  if (m.find() && !blip.getWavelet().getTags().contains(tag)) {
				  blip.getWavelet().getTags().add(tag);
				  isTagApplied = true;
			  }
		  }
	  }
	  return isTagApplied;
  }

  private boolean isNotifyProxy(String proxyForString) {
    if (proxyForString != null && proxyForString.endsWith("-notify")) {
      return true;
    } else {
    	 LOG.log(Level.INFO, "proxyForString: " + proxyForString + " in isNotifyProxy");
      return false;
    }
  }


  private String getServerName() {
    return System.getProperty("APP_DOMAIN");
  }

  public String getRobotAvatarUrl() {
    return System.getProperty("DIGESTBOTTY_ICON_URL");
  }

  public String getRobotProfilePageUrl() {
	  String profileUrl = System.getProperty("PROFILE_URL");
	  return profileUrl;
  }

  @Override
  public String getRobotName() {
	  String baseName = getServerName().replace(".appspot.com", "");
    return baseName.replace("digestbotty", "DigestBotty");
  }
  
  @Override
  protected ParticipantProfile getCustomProfile(String name) {
	  LOG.fine("requested profile for: " + name);
	  List<ExtDigest> digests = null;
	  ParticipantProfile profile = null;
	  Object o = cache.get(name);
	  profile = o != null ? ((SeriallizableParticipantProfile)o).getProfile() : null;
	  if(profile != null)
		  return profile;
	  LOG.fine("Not found profile for: " + name);
	  try {
		  digests =  extDigestDao.retrDigestsByProjectId(name);
	} catch (NullPointerException e) {
		LOG.fine("In getCustomProfile,  extDigestDao: " + extDigestDao + ", name: " + name );
	}
      if (digests != null && digests.size() > 0) {
    	  String digestWaveUrl = "https://wave.google.com/wave/waveref/googlewave.com/" + digests.get(0).getWaveId();
//    	  String digestWaveUrl = digests.get(0).getForumSiteUrl();
    	  profile = new ParticipantProfile(digests.get(0).getName(),
	    		  digests.get(0).getRobotThumbnailUrl() != null ? digests.get(0).getRobotThumbnailUrl() : getRobotAvatarUrl(),
	    				  digestWaveUrl);
    	  
    	  // Put the value into the cache.
	      cache.put(name, new SeriallizableParticipantProfile(profile.getImageUrl(),profile.getName(), profile.getProfileUrl()));
	      return profile; 
      }else{
    	  LOG.fine("Not found even DB profile for: " + name);
    	  return new ParticipantProfile(getRobotName(),
    			  getRobotAvatarUrl(),
    			  getRobotProfilePageUrl());
      }
  }
  
  public void clearCustomProfileFromCache(String proxyFor){
	  cache.put(proxyFor, null);
  }

  @RequestScoped
  private static class ServletHelper {
    private HttpServletRequest request = null;
    private HttpServletResponse response = null;

    @Inject
    public ServletHelper(HttpServletRequest request, HttpServletResponse response) {
      this.request = request;
      this.response = response;
    }

    public HttpServletRequest getRequest() {
      return this.request;
    }

    public HttpServletResponse getResponse() {
      return response;
    }
  }


  @Override
  public void onOperationError(OperationErrorEvent event) {
	  super.onOperationError(event);
	  LOG.severe(event.getMessage());
  }

//  @Override
//  @Capability(contexts = {Context.SELF})
//  public void onGadgetStateChanged(GadgetStateChangedEvent e) {
//	  LOG.log(Level.INFO, "entering OnGadgetStateChanged: ");
//	// If this is from the "*-digest" proxy, skip processing.
//	  boolean isDigestAdmin = isDigestAdmin(e.getBundle().getProxyingFor());
//	  String[] gadgetUrls = createGadgetUrlsArr();
//	  JSONObject json = new JSONObject();
//	  Blip blip = e.getBlip();
//	  Gadget gadget = null;
//	  
//	  
////	  if (!isDigestAdmin) {
////	      LOG.info("onGadgetStateChanged: "  + e.getBundle().getProxyingFor() + " return!");
////	      return; //only gadget proxy allowed to react
////	    }else{
////	    	LOG.info("onGadgetStateChanged: "  + e.getBundle().getProxyingFor() + " process!");
////	    }
//	  LOG.info("onGadgetStateChanged: "  + e.getBundle().getProxyingFor() + " process!");
//	  int i = 0;
//	  String gadgetUrl = null;
//	  while(gadget == null && i < gadgetUrls.length){
//		  gadgetUrl = gadgetUrls[i];
//		  gadget = Gadget.class.cast(blip.first(ElementType.GADGET,Gadget.restrictByUrl(gadgetUrl)).value());
//		  if(gadget!=null){
//			  handleGadgetRequest(e, json, blip, gadget);
//		  }
//		  i++;
//	  }
//	  
//  }

public void handleGadgetRequest(GadgetStateChangedEvent e, JSONObject json,
		Blip blip, Gadget gadget) {
	try{
		  if(gadget!=null)
		  {
			  LOG.log(Level.INFO, "entering handleGadgetRequest: " + gadget.getUrl());
			  Set<String> keys = new LinkedHashSet<String>( gadget.getProperties().keySet());
			  for(String key : keys){
				  String[] split = key.split("#");
				  String postBody = gadget.getProperty(key);
				  if(split != null && split.length == 3 && split[0].equalsIgnoreCase("request") && postBody != null){
					  String responseKey = "response#" + split[1] + "#" +  split[2];
					  LOG.info("Found request: " + key + ", body: " + postBody);
					  Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
					  JsonRpcRequest jsonRpcRequest = gson.fromJson(postBody, JsonRpcRequest.class);
					  if (jsonRpcRequest != null) {
						  String method = jsonRpcRequest.getMethod();
						  if (method != null) {
							  LOG.info("processing method " + method);
							  Class<? extends Command> commandClass = CommandType.valueOfIngoreCase(method).getClazz();
							  Command command = injector.getInstance(commandClass);
							  jsonRpcRequest.getParams().put("senderId", e.getModifiedBy());
							  String projectId = e.getBundle().getProxyingFor();
							  jsonRpcRequest.getParams().put("projectId", projectId);
							  LOG.info("sender is: " + e.getModifiedBy());
							  command.setParams(jsonRpcRequest.getParams());
							  try{
								  try {
									  json = command.execute();
								  } catch (JSONException e1) {
									  json.put("error", e1.getMessage());
								  } catch (IllegalArgumentException e2) {
									  json.put("error", e2.getMessage());
								  }
							  }catch(JSONException e3){
								  json.put("error", e3.getMessage());
								  LOG.severe(e3.getMessage());
							  }
							  Map<String,String> out = new HashMap<String, String>();
							  out.put(key, null);
							  if(!split[1].equals("none")){ //if none - no reply needed
								  out.put(responseKey, json.toString());
							  }
							  blip.first(ElementType.GADGET,Gadget.restrictByUrl(gadget.getUrl())).updateElement(out);
						  }
					  }
				  }
			  }
		  }else{
			  LOG.log(Level.WARNING, "\nGadget is null: ");
		  }
	  }catch(Exception e4){
		  
		  StringWriter sw = new StringWriter();
		  PrintWriter pw = new PrintWriter(sw);
		  e4.printStackTrace(pw);
		  e.getWavelet().reply("\n" + "EXCEPTION !!!" + sw.toString() + " : " + e4.getMessage());
		  LOG.severe(sw.toString());
	  }
}

protected String[] createGadgetUrlsArr() {
	String gadgetUrl1 = "http://" + System.getProperty("APP_DOMAIN") + ".appspot.com/digestbottygadget/com.aggfi.digest.client.DigestBottyGadget.gadget.xml";
	  String gadgetUrl2 = System.getProperty("READONLYPOST_GADGET_URL");
	  String gadgetUrl3 = "http://vegalabs.appspot.com/tracker.xml";
	  String[] gadgetUrls = {gadgetUrl1, gadgetUrl2,gadgetUrl3};
	return gadgetUrls;
}

  	/**
  	 * 
  	 * @param blipToUpdate
  	 * @param blipId
  	 * @param isBottom
  	 * @throws IOException
  	 */
	public void addOrUpdateLinkToBottomOrTopForDigestWave(Blip blipToUpdate,String blipId, boolean isBottom, boolean isSubmit) throws IOException {
		String toLocationStr = isBottom ? TO_BOTTOM : TO_TOP;
		String linkToAnnonationName = System.getProperty("APP_DOMAIN") + (isBottom ?  ".appspot.com/backtobottom" :  ".appspot.com/backtotop");
		if(blipToUpdate.getAnnotations().get(linkToAnnonationName) != null){
			//update
			List<BundledAnnotation> baList = createToAnnotation(
					blipToUpdate, blipId, linkToAnnonationName);
			  int endPos = blipToUpdate.getContent().indexOf(toLocationStr);
			  LOG.fine(toLocationStr + " annotation found, endPos: " + endPos);
			  blipToUpdate.range(endPos ,endPos + toLocationStr.length()).replace(baList, toLocationStr);
		}else{
			//add
			if(!isBottom){
				blipToUpdate.append("\n");
			}
			LOG.fine(toLocationStr +" annotation not found - inserting new!");
			  List<BundledAnnotation> baList = createToAnnotation(
					  blipToUpdate, blipId, linkToAnnonationName);
			  blipToUpdate.append("\n");
			  blipToUpdate.at(blipToUpdate.getContent().length()).insert(baList, toLocationStr);
			  blipToUpdate.append("\n");
				List<BundledAnnotation> ba = BundledAnnotation.listOf("style/fontStyle","italic","style/fontSize", "8pt");
				blipToUpdate.at(blipToUpdate.getContent().length()).insert(ba,USE_HOME_END_KEYBOARD_KEYS);
		}
		submit(blipToUpdate.getWavelet(), PREVIEW_RPC_URL);
	}

	public void appendPoweredBy(Blip blipToUpdate) {
		//append powered by
			String fontSize = "8pt";
			int startPos = blipToUpdate.getContent().length();
			List<BundledAnnotation> ba1 = BundledAnnotation.listOf("style/fontSize", fontSize,"style/backgroundColor", "rgb(91,155,226)", "style/color", "rgb(255,255,255)");
			blipToUpdate.at(blipToUpdate.getContent().length()).insert(ba1," [Forum powered by ");
			
			List<BundledAnnotation> ba2 = BundledAnnotation.listOf("style/fontSize", fontSize,"style/backgroundColor", "rgb(91,155,226)", "style/color", "rgb(255,255,255)", "link/manual","waveid://googlewave.com/w+KNw8wPWXA/~/conv+root/b+KNw8wPWXB");
			blipToUpdate.at(blipToUpdate.getContent().length()).insert(ba2,"DigestBotty");
			
			List<BundledAnnotation> ba3 = BundledAnnotation.listOf("style/fontSize", fontSize,"style/backgroundColor", "rgb(91,155,226)", "style/color", "rgb(255,255,255)");
			blipToUpdate.at(blipToUpdate.getContent().length()).insert(ba3,"] ");
			
			int endPos = blipToUpdate.getContent().length();
			blipToUpdate.range(startPos, endPos).annotate(System.getProperty("APP_DOMAIN") + ".appspot.com/poweredby", "done");
	}


	public List<BundledAnnotation> createToAnnotation(Blip blipToUpdate, String blipId, String linkToAnnonationName) {
		String blipRef = "waveid://" + blipToUpdate.getWaveId().getDomain() + "/" + blipToUpdate.getWaveId().getId() + "/~/conv+root/" + blipId;
		  List<BundledAnnotation> baList = BundledAnnotation.listOf("link/manual",blipRef,"style/fontSize", "8pt",linkToAnnonationName,blipId);
		return baList;
	}

	public void appendViewsTrackingGadget(Blip blip, String projectId) {
		String gadgetUrl = "http://" + System.getProperty("APP_DOMAIN") + ".appspot.com/tracker.xml";
		LOG.info("appendViewsTrackingGadget: " + projectId + ", blipId: " + blip.getBlipId());
		//check if blip already contains the add gadget. 
		BlipContentRefs gadgetRef = blip.first(ElementType.GADGET,Gadget.restrictByUrl(gadgetUrl));
		Boolean isGadgetAlreadyAppended = (Boolean)cache.get("isTrackingGadgetAlreadyAppended#" + blip.getBlipId() + "#projectId#" + projectId);
		isGadgetAlreadyAppended = isGadgetAlreadyAppended== null ? false : isGadgetAlreadyAppended;
		if(!isGadgetAlreadyAppended && (gadgetRef == null || Gadget.class.cast(gadgetRef.value()) == null)){
			Gadget gadget = null;
			gadget = new Gadget(gadgetUrl);
			blip.at(blip.getContent().length()).insert(gadget);
			cache.put("isTrackingGadgetAlreadyAppended#" + blip.getBlipId() + "#projectId#" + projectId, Boolean.TRUE);
			Map<String,String> out = new HashMap<String, String>();
		    out.put("projectId", projectId);
		    out.put("domain", "http://" + System.getProperty("APP_DOMAIN") + ".appspot.com");
		    out.put("eventValue", blip.getBlipId());
		    blip.first(ElementType.GADGET,Gadget.restrictByUrl(gadget.getUrl())).updateElement(out);
		}
		
	}

	public void removeViewsTrackingGadget(Blip blip, String projectId) {
		LOG.info("removeViewsTrackingGadget: " + projectId + ", blipId: " + blip.getBlipId());
		String gadgetUrl = "http://" + System.getProperty("APP_DOMAIN") + ".appspot.com/tracker.xml";
		//check if blip already contains the add gadget. 
		Gadget gadget = null;
		BlipContentRefs gadgetRef = blip.first(ElementType.GADGET,Gadget.restrictByUrl(gadgetUrl));
		if(gadgetRef != null){
			gadget = Gadget.class.cast(gadgetRef.value());
			if(gadget != null){
			   blip.first(ElementType.GADGET,Gadget.restrictByUrl(gadget.getUrl())).delete();
			   gadgetRef = blip.first(ElementType.GADGET,Gadget.restrictByUrl(gadgetUrl));
			   if(gadgetRef != null){
				   gadget = Gadget.class.cast(gadgetRef.value());
				   if(gadget != null){
					   LOG.warning("Failed to remove gadget!!! blipId: " + blip + ", projectId: " + projectId );
				   }else{
					   LOG.info("Removed gadget!!! blipId: " + blip + ", projectId: " + projectId );
				   }
			   }else{
				   LOG.info("Removed gadget!!! blipId: " + blip + ", projectId: " + projectId );
			   }
			   
			 
			}
		}
		
	}

	public void appendViewsCounterGadget(Blip blip) {
		String gadgetUrl = "http://" + System.getProperty("APP_DOMAIN") + ".appspot.com/viewhits.xml";
		//check if blip already contains the add gadget. 
		Gadget gadget = null;
		BlipContentRefs gadgetRef = blip.first(ElementType.GADGET,Gadget.restrictByUrl(gadgetUrl));
		if(gadgetRef != null){
			gadget = Gadget.class.cast(gadgetRef.value());
			if(gadget == null){
				gadget = new Gadget(gadgetUrl);
				blip.at(blip.getContent().length()).insert(gadget);
			}
		}
		
	}
	
	public void updateGadgetState(Blip blip, String gadgetUrl,Map<String, String> out) {
		Gadget gadget = extractGadgetFromBlip(gadgetUrl,blip);
		gadget.getProperties().putAll(out);
		blip.first(ElementType.GADGET,Gadget.restrictByUrl(gadget.getUrl())).updateElement(gadget.getProperties());
	}
	
	private Gadget extractGadgetFromBlip(String gadgetUrl, Blip blip){
		BlipContentRefs gadgetRef = blip.first(ElementType.GADGET,Gadget.restrictByUrl(gadgetUrl));
		if(gadgetRef == null || Gadget.class.cast(gadgetRef.value()) == null){
			return null;
		}else{
			return Gadget.class.cast(gadgetRef.value());
		}
	}

	@Override
	public void onWaveletSelfRemoved(WaveletSelfRemovedEvent event) {
		//remove the link to this wave from the digest
		
		Wavelet wavelet = event.getWavelet();
		String projectId = event.getBundle().getProxyingFor();
		ForumPost entry = forumPostDao.getForumPost (wavelet.getDomain(), wavelet.getWaveId().getId(), projectId);
		LOG.info("Removed: " + projectId + " from: " + wavelet.getTitle());
	        
	        
	        try{

            	//update entry title in the digest wave
            	List<ExtDigest> extList = extDigestDao.retrDigestsByProjectId(projectId);
            	ExtDigest extDigest = extList.get(0);
            	Wavelet digestWavelet = null;
            	String digestWaveDomain = extDigest.getDomain();
        	    String digestWaveId = extDigest.getWaveId();
            	try {
            		digestWavelet = fetchWavelet( new WaveId(digestWaveDomain, digestWaveId), new WaveletId(digestWaveDomain, "conv+root"), extDigest.getProjectId() + "-digest", getRpcServerUrl());
            	} catch (IOException e) {
            		LOG.log(Level.SEVERE,"",e);
            	}
            	Blip ablip = digestWavelet.getBlip(entry.getDigestBlipId());
            	ablip.all().delete();
            	ablip.append("deleted.");
            	digestWavelet.delete(ablip);
            	
            	submit(digestWavelet, getRpcServerUrl());
            	entry.setActive(false);
            	entry.setLastUpdated(new Date());
            	entry.setUpdater(event.getModifiedBy());
            	forumPostDao.save(entry);
        	
	        }catch(RuntimeException re){
	        	LOG.log(Level.SEVERE,"cannot update digest blip title from: " + entry.getTitle() + " to " + wavelet.getTitle(),re);
	        } catch (IOException e) {
	        	LOG.log(Level.SEVERE,"cannot update digest blip title from: " + entry.getTitle() + " to " + wavelet.getTitle(),e);
			}
	}

  /*
  @Override
  public void onWaveletParticipantsChanged(WaveletParticipantsChangedEvent event) {
	  LOG.info("Entering onWaveletParticipantsChanged");
	  try{
		  String patternStr =  getServerName() + "\\+.*\\@appspot.com";
		  List<String> participants = event.getParticipantsAdded();
		  for(String participant : participants){
			  if(participant.matches(patternStr)){
				  //digestbotty added, need to act
				  int proxyForStart = participant.indexOf("+")+1;
				  int proxyForEnd = participant.indexOf("@");
				  String proxyFor = participant.substring(proxyForStart,proxyForEnd);
				  String projectId = proxyFor;
				  Wavelet wavelet = event.getWavelet();
				  Blip blip = event.getBlip();
				  String modifiedBy = event.getModifiedBy();
				  if(!proxyFor.equals(event.getBundle().getProxyingFor())){
					  actOnBottyAdded(projectId, proxyFor, wavelet, blip, modifiedBy);
				  }
			  }
		  }
	  }catch(Exception e){
		  LOG.log(Level.SEVERE, "",e);
	  }
	  LOG.info("Exiting onWaveletParticipantsChanged");
  }
  */
}
