package com.aggfi.digest.client.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.aggfi.digest.client.constants.SimpleConstants;
import com.aggfi.digest.client.model.JsDigest;
import com.aggfi.digest.client.model.JsParams;
import com.aggfi.digest.client.request.RequestService;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class DigestServiceImpl implements IDigestService {
	
	private RequestService requestService;
	private String domain;
	private String url;
	
	@Inject
	public DigestServiceImpl(RequestService requestService, SimpleConstants constants){
		this.requestService = requestService;
		this.domain = constants.appDomain();
		
//		domain = "digestbotty.appengine.com";
		domain = "localhost:59304";
//		domain = "localhost:8888";
//		url = "http://" + domain + "/admin/jsonrpc" + "?cachebust=" + new Date().getTime();
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
	public void retrPostCountsT(String projectId,
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
	public void removeAutoTag(String projectId, String tag,
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
	public void removeDefaultTag(String projectId, String tag,
			AsyncCallback<JSONValue> asyncCallback) throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject();
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		
		paramsJson.put("projectId", new JSONString(projectId));
		paramsJson.put("tag", new JSONString(tag));
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("REMOVE_AUTO_TAG"));
		
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
	public void createDigest(JsDigest newDigest, AsyncCallback<JSONValue> callback) throws RequestException {
		com.google.gwt.json.client.JSONObject paramsJson = new JSONObject(newDigest);
		com.google.gwt.json.client.JSONObject postDataJson = new JSONObject();
		postDataJson.put("params", paramsJson);
		postDataJson.put("method", new JSONString("CREATE_DIGEST"));
		JsParams params = (JsParams) postDataJson.getJavaScriptObject();
		requestService.makeRequest(url,callback,params);
	}
}
