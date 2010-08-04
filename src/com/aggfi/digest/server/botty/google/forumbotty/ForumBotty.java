package com.aggfi.digest.server.botty.google.forumbotty;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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
import org.waveprotocol.wave.model.id.WaveId;
import org.waveprotocol.wave.model.id.WaveletId;

import com.aggfi.digest.server.botty.digestbotty.dao.BlipSubmitedDao;
import com.aggfi.digest.server.botty.digestbotty.dao.ExtDigestDao;
import com.aggfi.digest.server.botty.digestbotty.model.BlipSubmitted;
import com.aggfi.digest.server.botty.digestbotty.model.ExtDigest;
import com.aggfi.digest.server.botty.digestbotty.utils.StringBuilderAnnotater;
import com.aggfi.digest.server.botty.google.forumbotty.admin.Command;
import com.aggfi.digest.server.botty.google.forumbotty.admin.CommandType;
import com.aggfi.digest.server.botty.google.forumbotty.admin.JsonRpcRequest;
import com.aggfi.digest.server.botty.google.forumbotty.dao.AdminConfigDao;
import com.aggfi.digest.server.botty.google.forumbotty.dao.ForumPostDao;
import com.aggfi.digest.server.botty.google.forumbotty.dao.UserNotificationDao;
import com.aggfi.digest.server.botty.google.forumbotty.model.AdminConfig;
import com.aggfi.digest.server.botty.google.forumbotty.model.ForumPost;
import com.aggfi.digest.server.botty.google.forumbotty.model.UserNotification;
import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.servlet.RequestScoped;
import com.google.wave.api.AbstractRobot;
import com.google.wave.api.Blip;
import com.google.wave.api.BlipContentRefs;
import com.google.wave.api.Context;
import com.google.wave.api.ElementType;
import com.google.wave.api.FormElement;
import com.google.wave.api.Gadget;
import com.google.wave.api.JsonRpcResponse;
import com.google.wave.api.Line;
import com.google.wave.api.ParticipantProfile;
import com.google.wave.api.Wavelet;
import com.google.wave.api.JsonRpcConstant.ParamsProperty;
import com.google.wave.api.event.AbstractEvent;
import com.google.wave.api.event.BlipSubmittedEvent;
import com.google.wave.api.event.GadgetStateChangedEvent;
import com.google.wave.api.event.OperationErrorEvent;
import com.google.wave.api.event.WaveletParticipantsChangedEvent;
import com.google.wave.api.event.WaveletSelfAddedEvent;
import org.json.JSONException;
import org.json.JSONObject;


@Singleton
public class ForumBotty extends AbstractRobot {
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
  
  Cache cache;


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


    initOauth();
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

  @Override
  public void onWaveletSelfAdded(WaveletSelfAddedEvent event) {
	  LOG.log(Level.INFO, "onWaveletSelfAdded:" + event.toString());

	  String projectId = getCurrentProjectId(event);
	  String proxyFor = event.getBundle().getProxyingFor();
	  Wavelet wavelet = event.getWavelet();
	  Blip blip = event.getBlip();
	  String modifiedBy = event.getModifiedBy();

	  actOnBottyAdded(projectId, proxyFor, wavelet, blip,null);
}

protected void actOnBottyAdded(String projectId,
		String proxyFor, Wavelet wavelet, Blip blip, String modifiedBy) {
	if (projectId == null) {
		  LOG.log(Level.SEVERE, "actOnBottyAdded:Missing proxy-for project id");
		  return;
	  }
	  if (isNotifyProxy(proxyFor)) {
		  processNotifyProxy(wavelet,proxyFor,modifiedBy);
		  return;
	  }

	  // If this is from the "*-digest" proxy, skip processing.
	  if (isDigestWave(proxyFor)) {
		  return;
	  }     

	  if (isDigestAdmin(proxyFor)) {
		  wavelet.setTitle("DigestBotty [Admin Gadget]");
		  try {
			  submit(wavelet, getRpcServerUrl());
		  } catch (IOException e) {
			  LOG.log(Level.SEVERE, "",e);
		  }
		  return;
	  }     

	  // Add default participants
	  for (String participant : this.adminConfigDao.getAdminConfig(projectId)
			  .getDefaultParticipants()) {
		  if(!wavelet.getParticipants().contains(participant)){
			  wavelet.getParticipants().add(participant);
		  }
	  }

	  if (isWorthy(wavelet)) {
		  ForumPost entry = addOrUpdateDigestWave(projectId, wavelet,blip,modifiedBy);

		  for (String contributor : wavelet.getRootBlip().getContributors()) {
			  if (isNormalUser(contributor)) {
				  entry.addContributor(contributor);
			  }
		  }

		  // Apply default and auto tags to the wave
		  applyAutoTags(blip, projectId);
	  }
}

public ForumPost addOrUpdateDigestWave(String projectId, Wavelet wavelet, Blip blip, String modifiedBy) {
	ForumPost entry = forumPostDao.getForumPost(wavelet.getDomain(), 
			wavelet.getWaveId().getId());
	// Update contributor list if this is not robot or agent

	if (entry != null) {
		if (isNormalUser(modifiedBy)) {
			entry.addContributor(modifiedBy);
		}
		if(modifiedBy != null){
			entry.setUpdater(modifiedBy);
			entry.setBlipCount(entry.getBlipCount()+1);
		}
		
		// Existing wavelet in datastore
		entry.setTitle(wavelet.getTitle());
		entry.setLastUpdated(new Date());
		applyAutoTags(blip, projectId);
		entry = forumPostDao.syncTags(projectId, entry, wavelet);
//		updateEntryInDigestWave(entry); //TODO - maybe I need to create a different "hot and noisy" wave
		forumPostDao.save(entry);
	} else {
		entry = new ForumPost(wavelet.getDomain(), wavelet);
		entry.setProjectId(projectId);
		entry = forumPostDao.syncTags(projectId, entry, wavelet);
		entry.setBlipCount(wavelet.getBlips().size());
		applyAutoTags(blip, projectId);
		entry = forumPostDao.syncTags(projectId, entry, wavelet);
		addEntry2DigestWave(entry);
		forumPostDao.save(entry);

	}
	
//	forumPostDao.save(entry);
	return entry;
}

  private boolean isNormalUser(String userId) {
    return userId != null && !(userId.endsWith("appspot.com") || userId.endsWith("gwave.com"));
  }

  @Override
  @Capability(contexts = {Context.PARENT})
  public void onBlipSubmitted(BlipSubmittedEvent event) {
	  LOG.warning("Entering onBlipSubmitted");
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
    
    // If this is from the "*-gadget" proxy, skip processing.
    if (isDigestAdmin(event.getBundle().getProxyingFor())) {
      return;
    } 

    // If this is unworthy wavelet (empty), skip processing.
    if (!isWorthy(event.getWavelet()) && event.getBlip().isRoot()) {
    	event.getWavelet().setTitle(event.getBlip().getContent().split(" ")[0]);
    }else  if (!isWorthy(event.getWavelet())){
    	return;
    }

    Wavelet wavelet = event.getWavelet();

    String projectId = getCurrentProjectId(event);
    if (projectId == null) {
      LOG.log(Level.SEVERE, "onBlipSubmitted:Missing proxy-for project id");
      return;
    }

    ForumPost entry = addOrUpdateDigestWave(projectId, wavelet, event.getBlip(),event.getModifiedBy());

  //here is the place to save blipSubmitted
    saveBlipSubmitted(event, projectId);
   
  }

public void saveBlipSubmitted(BlipSubmittedEvent event, String projectId) {
	try{
		   Blip blip = event.getBlip();
		    String creator = blip.getCreator();
		    String modifier = event.getModifiedBy();
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
			BlipSubmitted blipSubmitted = new BlipSubmitted(creator, replytoCreator, modifier, contributors, replytoContributors, version, blipId, parentBlipId, waveletId, waveId, projectId, blipLength, createdTime, null, null);
			BlipSubmitedDao blipSubmitedDao = injector.getInstance(BlipSubmitedDao.class);
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
	  if(entries.size() > 0){
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

		  Blip blip = digestWavelet.reply("\n");
		  StringBuilderAnnotater sba = new StringBuilderAnnotater(blip);
		  sba.append(entryTitle, StringBuilderAnnotater.LINK_WAVE, entry.getId());
		  if(entry.getCreator() != null && !"".equals(entry.getCreator())){
			  sba.append(" [", null, null);
			  sba.append("By: " + entry.getCreator(), StringBuilderAnnotater.STYLE_FONT_STYLE, StringBuilderAnnotater.STYLE_ITALIC);
			  sba.append("] ", null, null);
		  }
		  sba.flush2Blip();
		  try {
			  LOG.log(Level.FINER, "trying to get newBlipIs fomr JsonRpcResponse");
			  List<JsonRpcResponse> submitResponseList = submit(digestWavelet, getRpcServerUrl());
			  for(JsonRpcResponse res : submitResponseList){
				  Map<ParamsProperty, Object> dataMap = res.getData();
				  if(dataMap != null && dataMap.get(ParamsProperty.NEW_BLIP_ID)  != null){
					  entry.setDigestBlipId(String.valueOf(dataMap.get(ParamsProperty.NEW_BLIP_ID)));
					  break;
				  }
			  }
		  } catch (IOException e) {
			  LOG.log(Level.SEVERE, "",e);
			  throw new RuntimeException(e);
		  }  
		  if(entry.getDigestBlipId() != null){
			  LOG.log(Level.INFO, "Created new entry in DIGEST_WAVE with: " + blip.getContent() + ", blipId: " + entry.getDigestBlipId());
		  }else{
			  LOG.log(Level.SEVERE, "Not Found new blip id: " + entry.toString() );
		  }
		  
	  }
  }
  
  private void updateEntryInDigestWave(ForumPost entry) {
//	  LOG.entering(this.getClass().getName(), "updateEntryInDigestWave", entry);
	  LOG.log(Level.INFO, "updateEntryInDigestWave", entry);
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

  private void processNotifyProxy(Wavelet wavelet,String projectId, String modifiedBy ) {

    projectId = projectId.replace("-notify", "");

    FormElement radioGroup = new FormElement(ElementType.RADIO_BUTTON_GROUP);
    String groupLabel = "frequency";
    radioGroup.setName(groupLabel);
    

    String userId = modifiedBy;
    UserNotification userNotification = userNotificationDao.getUserNotification(userId);
    if (userNotification != null) {
      radioGroup.setValue(userNotification.getNotificationType().toString());
    }

    FormElement radioNone = new FormElement(ElementType.RADIO_BUTTON);
    radioNone.setName("none");
    radioNone.setValue(groupLabel);

    FormElement radioDaily = new FormElement(ElementType.RADIO_BUTTON);
    radioDaily.setName("daily");
    radioDaily.setValue(groupLabel);

    FormElement radioWeekly = new FormElement(ElementType.RADIO_BUTTON);
    radioWeekly.setName("weekly");
    radioWeekly.setValue(groupLabel);

    wavelet.setTitle("How often would you like to receive digest from " + projectId + "?");
    Blip rootBlip = wavelet.getRootBlip();

    rootBlip.append(new Line());

    rootBlip.append(radioGroup);

    rootBlip.append(radioNone);
    rootBlip.append("none");
    rootBlip.append(new Line());

    rootBlip.append(radioDaily);
    rootBlip.append("daily");
    rootBlip.append(new Line());

    rootBlip.append(radioWeekly);
    rootBlip.append("weekly");
    rootBlip.append(new Line());
    rootBlip.append(new Line());

    FormElement frequencySubmit = new FormElement(ElementType.BUTTON);
    frequencySubmit.setName("frequencySubmit");
    frequencySubmit.setValue("submit");

    rootBlip.append(frequencySubmit);
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
	  //-----------------
	  //work around to get the profile updated //FIXME - remove
	  //----------------------------
	  List<ExtDigest> digests = null;
	  ParticipantProfile profile = null;
	  Object o = cache.get(name);
	  profile = o != null ? ((SeriallizableParticipantProfile)o).getProfile() : null;
	  if(profile != null)
		  return profile;
	  LOG.warning("Not found profile for: " + name);
	  try {
		  digests =  extDigestDao.retrDigestsByProjectId(name);
	} catch (NullPointerException e) {
		LOG.severe("In getCustomProfile,  extDigestDao: " + extDigestDao + ", name: " + name );
	}
      if (digests != null && digests.size() > 0) {
    	  profile = new ParticipantProfile(digests.get(0).getName(),
	    		  digests.get(0).getRobotThumbnailUrl() != null ? digests.get(0).getRobotThumbnailUrl() : getRobotAvatarUrl(),
	    		  digests.get(0).getForumSiteUrl() != null ? digests.get(0).getForumSiteUrl() : getRobotProfilePageUrl());
    	  
    	  // Put the value into the cache.
	      cache.put(name, new SeriallizableParticipantProfile(profile.getImageUrl(),profile.getName(), profile.getProfileUrl()));
	      return profile; 
      }else{
    	  LOG.warning("Not found even DB profile for: " + name);
    	  return new ParticipantProfile(getRobotName(),
    			  getRobotAvatarUrl(),
    			  getRobotProfilePageUrl());
      }
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

  @Override
  @Capability(contexts = {Context.SELF})
  public void onGadgetStateChanged(GadgetStateChangedEvent e) {
	  LOG.log(Level.WARNING, "OnGadgetStateChanged: ");
	// If this is from the "*-digest" proxy, skip processing.
	    if (!isDigestAdmin(e.getBundle().getProxyingFor())) {
	      LOG.info("onGadgetStateChanged: "  + e.getBundle().getProxyingFor() + " return!");
	      return; //only gadget proxy allowed to react
	    }else{
	    	LOG.info("onGadgetStateChanged: "  + e.getBundle().getProxyingFor() + " process!");
	    }
	  JSONObject json = new JSONObject();
	  Blip blip = e.getBlip();
	  String gadgetUrl = "http://" + System.getProperty("APP_DOMAIN") + ".appspot.com/digestbottygadget/com.aggfi.digest.client.DigestBottyGadget.gadget.xml";
	  Gadget gadget = Gadget.class.cast(blip.first(ElementType.GADGET,Gadget.restrictByUrl(gadgetUrl)).value());
	  try{
		  if(gadget!=null)
		  {
			  Set<String> keys = new LinkedHashSet<String>( gadget.getProperties().keySet());
			  for(String key : keys){
				  String[] split = key.split("#");
				  String postBody = gadget.getProperty(key);
				  if(split != null && split.length == 3 && split[0].equalsIgnoreCase("request") && postBody != null){
					  String responseKey = "response#" + split[1] + "#" +  split[2];
					  long currTime = System.currentTimeMillis();
					  long requestTime = Long.parseLong(split[2]);
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
							  out.put(responseKey, json.toString());
							  blip.first(ElementType.GADGET,Gadget.restrictByUrl(gadgetUrl)).updateElement(out);
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
