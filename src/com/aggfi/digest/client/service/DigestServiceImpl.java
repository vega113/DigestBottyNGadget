package com.aggfi.digest.client.service;

import java.util.Date;
import com.aggfi.digest.client.constants.DigestConstants;
import com.aggfi.digest.client.model.JsDigest;
import com.vegalabs.general.client.request.RequestService;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
	public void retrTagsDistributions(String projectId,AsyncCallback<JSONValue> callback)throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("GET_TAG_COUNTS"));
		
		JavaScriptObject params = postDataJson.getJavaScriptObject();
		requestService.makeRequest(url,callback,params);
	}

	@Override
	public void retrPrjectsPerUserId(String userId,
			AsyncCallback<JSONValue> callback) throws RequestException { 
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("userId", new JSONString(userId));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("GET_PROJECTS_PER_USER"));
		
		JavaScriptObject params = postDataJson.getJavaScriptObject();
		requestService.makeRequest(url,callback,params);
	}
	@Override
	public void retrPostCounts(String projectId,
			AsyncCallback<JSONValue> asyncCallback) throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("GET_POST_COUNTS"));
		
		JavaScriptObject params = postDataJson.getJavaScriptObject();
		requestService.makeRequest(url,asyncCallback,params);
		
	}
	
	@Override
	public void removeDefaultParticipant(String projectId,String participantId, AsyncCallback<JSONValue> asyncCallback) throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		paramsJson.put("participantId", new JSONString(participantId));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("REMOVE_DEFAULT_PARTICIPANT"));
		
		JavaScriptObject params = postDataJson.getJavaScriptObject();
		requestService.makeRequest(url,asyncCallback,params);
		
	}
	

	@Override
	public void retrAdminConfig(String projectId,AsyncCallback<JSONValue> callback)throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("GET_ADMIN_CONFIG"));
		
		JavaScriptObject params = postDataJson.getJavaScriptObject();
		requestService.makeRequest(url,callback,params);
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
		
		JavaScriptObject params = postDataJson.getJavaScriptObject();
		requestService.makeRequest(url,asyncCallback,params);
		
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
		
		JavaScriptObject params = postDataJson.getJavaScriptObject();
		requestService.makeRequest(url,asyncCallback,params);
		
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
		
		JavaScriptObject params = postDataJson.getJavaScriptObject();
		requestService.makeRequest(url,asyncCallback,params);
		
	}
	
	@Override
	public void addDigestManager(String projectId,String managerId, AsyncCallback<JSONValue> asyncCallback) throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		paramsJson.put("managerId", new JSONString(managerId));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("ADD_DIGEST_MANAGER"));
		
		JavaScriptObject params = postDataJson.getJavaScriptObject();
		requestService.makeRequest(url,asyncCallback,params);
		
	}

	@Override
	public void addDefaultParticipant(String projectId,String participantId, AsyncCallback<JSONValue> asyncCallback) throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		paramsJson.put("participantId", new JSONString(participantId));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("ADD_DEFAULT_PARTICIPANT"));
		
		JavaScriptObject params = postDataJson.getJavaScriptObject();
		requestService.makeRequest(url,asyncCallback,params);
		
	}
	
	@Override
	public void addDefaultTag(String projectId,String tag, AsyncCallback<JSONValue> asyncCallback) throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		paramsJson.put("tag", new JSONString(tag));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("ADD_DEFAULT_TAG"));
		
		JavaScriptObject params = postDataJson.getJavaScriptObject();
		requestService.makeRequest(url,asyncCallback,params);
		
	}


	@Override
	public void createDigest(JsDigest newDigest, AsyncCallback<JSONValue> callback) throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject(newDigest);
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("CREATE_DIGEST"));
		JavaScriptObject params = postDataJson.getJavaScriptObject();
		requestService.makeRequest(url,callback,params);
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
		
		JavaScriptObject params = postDataJson.getJavaScriptObject();
		requestService.makeRequest(url,asyncCallback,params);
	}


	@Override
	public void retrBlipsPerContributor(String projectId,AsyncCallback<JSONValue> asyncCallback) throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("GET_BLIPS_PER_CONTRIBUTOR"));
		
		JavaScriptObject params = postDataJson.getJavaScriptObject();
		requestService.makeRequest(url,asyncCallback,params);
	}


	@Override
	public void retrContributorsPerInfluence(String projectId,
			AsyncCallback<JSONValue> asyncCallback) throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("GET_CONTRIBUTORS_PER_INFLUENCE"));
		
		JavaScriptObject params = postDataJson.getJavaScriptObject();
		requestService.makeRequest(url,asyncCallback,params);
	}



	@Override
	public void retrPostsByActivity(String projectId,
			AsyncCallback<JSONValue> asyncCallback) throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("GET_POST_BY_ACTIVITY"));
		
		JavaScriptObject params = postDataJson.getJavaScriptObject();
		requestService.makeRequest(url,asyncCallback,params);
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
		
		JavaScriptObject params = postDataJson.getJavaScriptObject();
		requestService.makeRequest(url,asyncCallback,params);
	}
}
