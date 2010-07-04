package com.aggfi.digest.server.botty.google.forumbotty;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
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

import javax.management.RuntimeErrorException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheManager;

import org.waveprotocol.wave.model.id.WaveId;
import org.waveprotocol.wave.model.id.WaveletId;

import com.aggfi.digest.server.botty.digestbotty.dao.ExtDigestDao;
import com.aggfi.digest.server.botty.digestbotty.model.ExtDigest;
import com.aggfi.digest.server.botty.google.forumbotty.admin.Command;
import com.aggfi.digest.server.botty.google.forumbotty.admin.CommandType;
import com.aggfi.digest.server.botty.google.forumbotty.admin.JsonRpcRequest;
import com.aggfi.digest.server.botty.google.forumbotty.dao.AdminConfigDao;
import com.aggfi.digest.server.botty.google.forumbotty.dao.ForumPostDao;
import com.aggfi.digest.server.botty.google.forumbotty.dao.UserNotificationDao;
import com.aggfi.digest.server.botty.google.forumbotty.model.ForumPost;
import com.aggfi.digest.server.botty.google.forumbotty.model.UserNotification;
import com.aggfi.digest.server.botty.google.forumbotty.model.UserNotification.NotificationType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.internal.ImmutableMap;
import com.google.inject.servlet.RequestScoped;
import com.google.wave.api.AbstractRobot;
import com.google.wave.api.Blip;
import com.google.wave.api.BlipContentRefs;
import com.google.wave.api.Context;
import com.google.wave.api.ElementType;
import com.google.wave.api.FormElement;
import com.google.wave.api.Gadget;
import com.google.wave.api.Line;
import com.google.wave.api.ParticipantProfile;
import com.google.wave.api.Tags;
import com.google.wave.api.Wavelet;
import com.google.wave.api.event.AbstractEvent;
import com.google.wave.api.event.BlipSubmittedEvent;
import com.google.wave.api.event.FormButtonClickedEvent;
import com.google.wave.api.event.GadgetStateChangedEvent;
import com.google.wave.api.event.OperationErrorEvent;
import com.google.wave.api.event.WaveletSelfAddedEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;


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
        cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());
    } catch (CacheException e) {
        // ...
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
    if (isNotifyProxy(event.getBundle().getProxyingFor())) {
      processNotifyProxy(event);
      return;
    }

    // If this is from the "*-digest" proxy, skip processing.
    if (isDigestWave(event.getBundle().getProxyingFor())) {
      return;
    }     
    
    
    String projectId = getCurrentProjectId(event);
    if (projectId == null) {
      LOG.log(Level.SEVERE, "Missing proxy-for project id");
      return;
    }

    Wavelet wavelet = event.getWavelet();

    // Add default participants
    for (String participant : this.adminConfigDao.getAdminConfig(projectId)
        .getDefaultParticipants()) {
      wavelet.getParticipants().add(participant);
    }

    if (isWorthy(wavelet)) {
      ForumPost entry = addPost2Digest(projectId, wavelet);

      for (String contributor : wavelet.getRootBlip().getContributors()) {
        if (isNormalUser(contributor)) {
          entry.addContributor(contributor);
        }
      }

      // Apply default and auto tags to the wave
      applyAutoTags(event.getBlip(), projectId);
      
     
    }
}

public ForumPost addPost2Digest(String projectId, Wavelet wavelet) {
	ForumPost entry = new ForumPost(wavelet.getDomain(), wavelet);
      entry.setProjectId(projectId);
      applyDefaultTags(wavelet, projectId);
      entry = forumPostDao.syncTags(projectId, entry, wavelet);
      forumPostDao.save(entry);
      updateDigestWave(entry);
	return entry;
}

  private boolean isNormalUser(String userId) {
    return !(userId.endsWith("appspot.com") || userId.endsWith("gwave.com"));
  }

  @Override
  @Capability(contexts = {Context.SELF})
  public void onBlipSubmitted(BlipSubmittedEvent event) {
	  LOG.warning("Entering onBlipSubmitted");
    // If this is from the "*-notify" proxy, skip processing.
    if (isNotifyProxy(event.getBundle().getProxyingFor())) {
      return;
    }

    // If this is from the "*-digest" proxy, skip processing.
    if (isDigestWave(event.getBundle().getProxyingFor())) {
      return;
    } 

    // If this is unworthy wavelet (empty), skip processing.
    if (!isWorthy(event.getWavelet())) {
      return;
    }

    Wavelet wavelet = event.getWavelet();

    String projectId = getCurrentProjectId(event);
    if (projectId == null) {
      LOG.log(Level.SEVERE, "Missing proxy-for project id");
      return;
    }

    ForumPost entry = forumPostDao.getForumPost(wavelet.getDomain(), 
        wavelet.getWaveId().getId());
    if (entry != null) {
      // Existing wavelet in datastore
      entry.setTitle(wavelet.getTitle());
      entry.setLastUpdated(new Date());
      entry.setBlipCount(wavelet.getBlips().size());
    } else {
      // Brand new wavelet
      entry = new ForumPost(wavelet.getDomain(), wavelet);
      entry.setProjectId(projectId);
      updateDigestWave(entry);
    }

    // Update contributor list if this is not robot or agent
    if (isNormalUser(event.getModifiedBy())) {
      entry.addContributor(event.getModifiedBy());
    }

    // Apply default and auto tags to the wave
    applyDefaultTags(wavelet, projectId);
    applyAutoTags(event.getBlip(), projectId);

    entry = forumPostDao.syncTags(projectId, entry, wavelet);
    forumPostDao.save(entry);
  }
  
  private boolean isDigestWave(String proxyingFor) {
    if (proxyingFor != null && proxyingFor.endsWith("-digest")) {
      return true;
    } else {
      return false;
    }
  }

  private void updateDigestWave(ForumPost entry) {
	LOG.entering(this.getClass().getName(), "updateDigestWave", entry);
    Wavelet digestWavelet = null;
    ExtDigestDao extDigestDao = injector.getInstance(ExtDigestDao.class);
    try {
    	ExtDigest digest = extDigestDao.retrDigestsByProjectId(entry.getProjectId()).get(0);
    	String digestWaveDomain = digest.getDomain();
    	String digestWaveId = digest.getWaveId();
    	LOG.log(Level.FINER, "digestWaveDomain: " + digestWaveDomain + ", digestWaveId: " + digestWaveId);
    	digestWavelet = fetchWavelet( new WaveId(digestWaveDomain, digestWaveId), new WaveletId(digestWaveDomain, "conv+root"), entry.getProjectId() + "-digest",  getRpcServerUrl());
    	String entryTitle = entry.getTitle();

    	Blip blip = digestWavelet.reply("\n");
    	blip.append(entryTitle);
    	BlipContentRefs.range(blip, 0, blip.getContent().length()).
    	annotate("link/wave", entry.getId());
    	submit(digestWavelet, getRpcServerUrl());  
    	LOG.log(Level.INFO, "Updated DIGEST_WAVE with: " + entryTitle);
    } catch (IOException e) {
      LOG.log(Level.SEVERE, entry.getProjectId(), e);
    }
    LOG.exiting(this.getClass().getName(), "updateDigestWave");
  }
  
  private boolean isWorthy(Wavelet wavelet) {
    return !wavelet.getTitle().trim().equals("");
  }

  private void applyDefaultTags(Wavelet wavelet, String projectId) {
    Tags tags = wavelet.getTags();
    for (String tag : this.adminConfigDao.getAdminConfig(projectId).getDefaultTags()) {
      if (!tags.contains(tag)) {
        tags.add(tag);
      }
    }
  }

  private void removeDefaultTags(Wavelet wavelet, String projectId) {
    Tags tags = wavelet.getTags();
    for (String tag : this.adminConfigDao.getAdminConfig(projectId).getDefaultTags()) {
      if (tags.contains(tag)) {
        tags.remove(tag);
      }
    }
  }

  @Override
  public void onFormButtonClicked(FormButtonClickedEvent event) {
    if (isNotifyProxy(event.getBundle().getProxyingFor())) {
      Wavelet wavelet = event.getWavelet();
      String projectId = event.getBundle().getProxyingFor().replace("-notify", "");
      Blip blip = event.getBlip();
      if (event.getButtonName().equals("frequencySubmit")) {

        FormElement frequencyGroup = FormElement.class.cast(blip.first(
            ElementType.RADIO_BUTTON_GROUP).value());

        NotificationType notificationType = NotificationType.NONE;

        if (frequencyGroup.getValue().equals("none")) {
          notificationType = NotificationType.NONE;
        } else if (frequencyGroup.getValue().equals("daily")) {
          notificationType = NotificationType.DAILY;
        } else if (frequencyGroup.getValue().equals("weekly")) {
          notificationType = NotificationType.WEEKLY;
        }

        String userId = event.getModifiedBy();
        String notificationId = userId + projectId;
        UserNotification userNotification = userNotificationDao.getUserNotification(notificationId);
        if (userNotification == null) {
          userNotification = new UserNotification(userId, projectId);
        }
        userNotification.setNotificationType(notificationType);
        userNotificationDao.save(userNotification);

        wavelet
            .reply("\nYour notification frequency is now set to be " + frequencyGroup.getValue());
      }
    }
  }

  public void applyAutoTags(Blip blip, String projectId) {
    String content = blip.getContent();

    for (Entry<String, Pattern> entry : this.adminConfigDao.getAdminConfig(projectId)
        .getAutoTagRegexMap().entrySet()) {
      String tag = entry.getKey();
      Pattern pattern = entry.getValue();

      Matcher m = null;
      m = pattern.matcher(content);
      if (m.find()) {
        blip.getWavelet().getTags().add(tag);
      }
    }
  }

  private boolean isNotifyProxy(String proxyForString) {
    if (proxyForString != null && proxyForString.endsWith("-notify")) {
      return true;
    } else {
    	 LOG.log(Level.INFO, "proxyForString: " + proxyForString + " in isNotifyProxy");
      return false;
    }
  }

  private void processNotifyProxy(WaveletSelfAddedEvent event) {
    Wavelet wavelet = event.getWavelet();

    String projectId = event.getBundle().getProxyingFor().replace("-notify", "");

    FormElement radioGroup = new FormElement(ElementType.RADIO_BUTTON_GROUP);
    String groupLabel = "frequency";
    radioGroup.setName(groupLabel);

    String userId = event.getModifiedBy();
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
//    ServletHelper servletHelper = injector.getInstance(ServletHelper.class);
//    LOG.info("Server name: " +servletHelper.getRequest().getServerName() + ", appdomain: " + System.getProperty("APP_DOMAIN"));
    return System.getProperty("APP_DOMAIN");
  }

  public String getRobotAvatarUrl() {
    return "http://" + getServerName() + ".appspot.com/images/forumbotty_thumb_old_2.png";
  }

  public String getRobotProfilePageUrl() {
	  String profileUrl = System.getProperty("PROFILE_URL");
//    return "http://" + getServerName() + "/_wave/robot/profile"; 
	  return profileUrl;
  }

  @Override
  public String getRobotName() {
    return getServerName().replace(".appspot.com", "");
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
	  JSONObject json = new JSONObject();
	  Blip blip = e.getBlip();
	  String gadgetUrl = System.getProperty("ADMIN_GADGET_URL");
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
					  LOG.info("Found request: " + key + ", body: " + postBody);
					  Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
					  JsonRpcRequest jsonRpcRequest = gson.fromJson(postBody, JsonRpcRequest.class);
					  if (jsonRpcRequest != null) {
						  String method = jsonRpcRequest.getMethod();
						  if (method != null) {
							  LOG.info("processing method " + method);
							  Class<? extends Command> commandClass = CommandType.valueOfIngoreCase(method).getClazz();
							  Command command = injector.getInstance(commandClass);
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
								  throw new RuntimeException(e3.getMessage());
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
  
}