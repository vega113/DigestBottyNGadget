package com.aggfi.digest.client.service;

import java.util.Date;
import java.util.Map;

import com.aggfi.digest.client.constants.DigestConstants;
import com.aggfi.digest.client.model.JsDigest;
import com.vegalabs.general.client.request.RequestService;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.inject.Inject;

public class DigestServiceImpl implements DigestService {
	
	private RequestService requestService;
	private String url;
	
	@Inject
	public DigestServiceImpl(RequestService requestService, DigestConstants constants){
		this.requestService = requestService;
		url = "/admin/jsonrpc" + "?cachebust=" + new Date().getTime();
	}

	

	@Override
	public void retrTagsDistributions(String projectId,AsyncCallback<JSONValue> asyncCallback)throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("GET_TAG_COUNTS"));
		
		makePreCall(postDataJson, asyncCallback);
	}

	@Override
	public void retrPrjectsPerUserId(String userId,
			AsyncCallback<JSONValue> asyncCallback) throws RequestException { 
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("userId", new JSONString(userId));
		postDataJson.put("params", paramsJson);
		postDataJson.put("senderId", new JSONString(userId));
		postDataJson.put("method", new JSONString("GET_PROJECTS_PER_USER"));
		
		makePreCall(postDataJson, asyncCallback);
	}
	@Override
	public void retrPostCounts(String projectId,
			AsyncCallback<JSONValue> asyncCallback) throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("GET_POST_COUNTS"));
		
		makePreCall(postDataJson, asyncCallback);
		
	}
	
	@Override
	public void removeDefaultParticipant(String projectId,String participantId, AsyncCallback<JSONValue> asyncCallback) throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		paramsJson.put("participantId", new JSONString(participantId));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("REMOVE_DEFAULT_PARTICIPANT"));
		
		makePreCall(postDataJson, asyncCallback);
		
	}
	

	@Override
	public void retrAdminConfig(String projectId,AsyncCallback<JSONValue> asyncCallback)throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("GET_ADMIN_CONFIG"));
		
		makePreCall(postDataJson, asyncCallback);
	}

	@Override
	public void removeAutoTag(String projectId, String tag, String sync,
			AsyncCallback<JSONValue> asyncCallback) throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		paramsJson.put("tag", new JSONString(tag));
		paramsJson.put("sync", new JSONString(sync));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("REMOVE_AUTO_TAG"));
		
		makePreCall(postDataJson, asyncCallback);
		
	}

	@Override
	public void removeDefaultTag(String projectId, String tag,
			AsyncCallback<JSONValue> asyncCallback) throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		paramsJson.put("tag", new JSONString(tag));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("REMOVE_DEFAULT_TAG"));
		
		makePreCall(postDataJson, asyncCallback);
		
	}
	
	@Override
	public void removeDigestManager(String projectId, String managerId,
			AsyncCallback<JSONValue> asyncCallback) throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		paramsJson.put("managerId", new JSONString(managerId));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("REMOVE_DIGEST_MANAGER"));
		
		makePreCall(postDataJson, asyncCallback);
		
	}
	
	@Override
	public void addDigestManager(String projectId,String managerId, AsyncCallback<JSONValue> asyncCallback) throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		paramsJson.put("managerId", new JSONString(managerId));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("ADD_DIGEST_MANAGER"));
		
		makePreCall(postDataJson, asyncCallback);
		
	}

	@Override
	public void addDefaultParticipant(String projectId,String participantId, AsyncCallback<JSONValue> asyncCallback) throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		paramsJson.put("participantId", new JSONString(participantId));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("ADD_DEFAULT_PARTICIPANT"));
		
		makePreCall(postDataJson, asyncCallback);
		
	}
	
	@Override
	public void addDefaultTag(String projectId,String tag, AsyncCallback<JSONValue> asyncCallback) throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		paramsJson.put("tag", new JSONString(tag));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("ADD_DEFAULT_TAG"));
		
		makePreCall(postDataJson, asyncCallback);
		
	}


	@Override
	public void createDigest(JsDigest newDigest, AsyncCallback<JSONValue> asyncCallback) throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject(newDigest);
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("CREATE_DIGEST"));
		
		makePreCall(postDataJson, asyncCallback);
	}



	@Override
	public void addAutoTag(String projectId, String tag, String regex, String isSync,
			AsyncCallback<JSONValue> asyncCallback) throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		paramsJson.put("tag", new JSONString(tag));
		paramsJson.put("regex", new JSONString(regex));
		paramsJson.put("sync", new JSONString(isSync));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("ADD_AUTO_TAG"));
		
		JavaScriptObject params = postDataJson.getJavaScriptObject();
		requestService.makeRequest(url,asyncCallback,params);
		
	}



	@Override
	public void addWavesParticipant(String projectId, String participantId, String tag,
			AsyncCallback<JSONValue> asyncCallback) throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		paramsJson.put("participantId", new JSONString(participantId));
		paramsJson.put("tag", new JSONString(tag));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("ADD_WAVES_PARTICIPANT"));
		
		JavaScriptObject params = postDataJson.getJavaScriptObject();
		requestService.makeRequest(url,asyncCallback,params);
		
	}
	
	@Override
	public void retrBlipsCounts(String projectId,AsyncCallback<JSONValue> asyncCallback) throws RequestException{
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("GET_BLIPS_COUNT"));
		
		makePreCall(postDataJson, asyncCallback);
	}


	@Override
	public void retrBlipsPerContributor(String projectId,AsyncCallback<JSONValue> asyncCallback) throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("GET_BLIPS_PER_CONTRIBUTOR"));
		
		makePreCall(postDataJson, asyncCallback);
	}


	@Override
	public void retrContributorsPerInfluence(String projectId,
			AsyncCallback<JSONValue> asyncCallback) throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("GET_CONTRIBUTORS_PER_INFLUENCE"));
		
		makePreCall(postDataJson, asyncCallback);
	}



	@Override
	public void retrPostsByActivity(String projectId,
			AsyncCallback<JSONValue> asyncCallback) throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("GET_POST_BY_ACTIVITY"));
		
		makePreCall(postDataJson, asyncCallback);
	}



	@Override
	public void updateAtomFeedPublic(String projectId,
			boolean isMakeAtomFeedPublic, AsyncCallback<JSONValue> asyncCallback)
			throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		paramsJson.put("isMakeAtomFeedPublic", new JSONString(String.valueOf(isMakeAtomFeedPublic)));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("UPDATE_ATOM_FEED_PUBLIC"));
		
		makePreCall(postDataJson, asyncCallback);
	}
	
	@Override
	public void addSecurePostGadget(String projectId, String userId, AsyncCallback<JSONValue> asyncCallback)
			throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		paramsJson.put("userId", new JSONString(userId));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("ADD_SECURE_POST_GADGET"));
		
		makePreCall(postDataJson, asyncCallback);
	}



	@Override
	public void updateSocialBtnsSettings(String projectId,
			boolean isDiggEnabled, boolean isBuzzEnabled,
			boolean isTweetEnabled, boolean isFaceEnabled,
			AsyncCallback<JSONValue> asyncCallback) throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		paramsJson.put("isDiggEnabled", new JSONString(String.valueOf(isDiggEnabled)));
		paramsJson.put("isBuzzEnabled", new JSONString(String.valueOf(isBuzzEnabled)));
		paramsJson.put("isTweetEnabled", new JSONString(String.valueOf(isTweetEnabled)));
		paramsJson.put("isFaceEnabled", new JSONString(String.valueOf(isFaceEnabled)));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("UPDATE_SOCIAL_BTNS_SETTINGS"));
		
		makePreCall(postDataJson, asyncCallback);
	}
	
	@Override
	public void addAdSenseCode(String projectId, String userId, String adSenseCode, String userName, String userThumbnailUrl, boolean isAdSenseUpdate4User,  AsyncCallback<JSONValue> asyncCallback)
			throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		paramsJson.put("userId", new JSONString(userId));
		paramsJson.put("adSenseCode", new JSONString(adSenseCode));
		paramsJson.put("userName", new JSONString(userName));
		paramsJson.put("userThumbnailUrl", new JSONString(userThumbnailUrl));
		paramsJson.put("isAdSenseUpdate4User", new JSONString(String.valueOf(isAdSenseUpdate4User)));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("ADD_ADSENSE_CODE"));
		
		makePreCall(postDataJson, asyncCallback);
	}
	
	@Override
	public void updateViewsTracking(String projectId, boolean isEnableViewsTracking, boolean isSync,  AsyncCallback<JSONValue> asyncCallback)
			throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		paramsJson.put("isEnableViewsTracking", new JSONString(String.valueOf(isEnableViewsTracking)));
		paramsJson.put("isSync", new JSONString(String.valueOf(isSync)));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("UPDATE_VIEWS_TRACKING"));
		
		makePreCall(postDataJson, asyncCallback);
	}
	

	@Override
	public void getAdSenseCode(String projectId, String userId, boolean isAdSenseUpdate4User,  AsyncCallback<JSONValue> asyncCallback)
			throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		paramsJson.put("userId", new JSONString(userId));
		paramsJson.put("isAdSenseUpdate4User", new JSONString(String.valueOf(isAdSenseUpdate4User)));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("GET_ADSENSE_CODE"));
		
		makePreCall(postDataJson, asyncCallback);
	}
	
	@Override
	public void retrViewsCounts(String projectId,AsyncCallback<JSONValue> asyncCallback) throws RequestException{
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("GET_VIEWS_COUNT"));
		
		makePreCall(postDataJson, asyncCallback);
	}
	
	

	@Override
	public void retrPostsByViews(String projectId,
			AsyncCallback<JSONValue> asyncCallback) throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("GET_POSTS_BY_VIEWS"));
		
		makePreCall(postDataJson, asyncCallback);
	}
	
	@Override
	public void addAdSenseInstaller(String projectId, String userId,  AsyncCallback<JSONValue> asyncCallback)
			throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		paramsJson.put("userId", new JSONString(userId));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("ADD_ADSENSE_INSTALLER"));
		
		makePreCall(postDataJson, asyncCallback);
	}
	
	@Override
	public void addAdSenseInstaller(String userId,  AsyncCallback<JSONValue> asyncCallback)
			throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("userId", new JSONString(userId));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("ADD_VIEWS_COUNTER_INSTALLER"));
		
		makePreCall(postDataJson, asyncCallback);
	}



	@Override
	public void retrDigestInfo(String projectId,
			AsyncCallback<JSONValue> asyncCallback) throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("GET_DIGEST_INFO"));
		
		makePreCall(postDataJson, asyncCallback);
	}



	@Override
	public void updateDigestInfo(String projectId, String authorName,
			String forumName, String description, String installerThumbnailUrl,
			String forumSiteUrl, AsyncCallback<JSONValue> asyncCallback)
			throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		paramsJson.put("authorName", new JSONString(authorName));
		paramsJson.put("forumName", new JSONString(forumName));
		paramsJson.put("description", new JSONString(description));
		paramsJson.put("installerThumbnailUrl", new JSONString(installerThumbnailUrl));
		paramsJson.put("forumSiteUrl", new JSONString(forumSiteUrl));
		
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("UPDATE_DIGEST_INFO"));
		
		makePreCall(postDataJson, asyncCallback);
//		requestService.makeRequest(url,asyncCallback,params);
	}
	
	private void makePreCall(final com.google.gwt.json.client.JSONObject paramsMap, final AsyncCallback<JSONValue> asyncCallback) throws RequestException{
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		String projectId = "none";
		paramsJson.put("projectId", new JSONString(projectId));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("GET_VIEWS_COUNT"));
		
		JavaScriptObject params = postDataJson.getJavaScriptObject();//make a call to ensure that the JVM is on
		requestService.makeRequest(url,new AsyncCallback<JSONValue>() {
			
			@Override
			public void onSuccess(JSONValue result) {
				makeActualCall(paramsMap, asyncCallback);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				makeActualCall(paramsMap, asyncCallback);
			}

			private void makeActualCall(final com.google.gwt.json.client.JSONObject postDataJson,
					final AsyncCallback<JSONValue> asyncCallback) {
				
				JavaScriptObject params = postDataJson.getJavaScriptObject();
				try {
					requestService.makeRequest(url,asyncCallback,params);
				} catch (RequestException e) {
					asyncCallback.onFailure(e);
				}
			}
		},params);
	}


	
}
