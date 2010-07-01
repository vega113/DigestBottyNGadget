package com.aggfi.digest.client.service;


import com.aggfi.digest.client.model.JsDigest;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IDigestService {
	void createDigest(JsDigest digest, AsyncCallback<JSONValue> callback) throws RequestException;
	
	void retrTagsDistributions(String projectId, AsyncCallback<JSONValue> callback) throws RequestException;

	void retrPrjectsPerUserId(String userId, AsyncCallback<JSONValue> callback) throws RequestException;

	void retrPostCountsT(String value, AsyncCallback<JSONValue> asyncCallback) throws RequestException;

	void removeDefaultParticipant(String projectId, String participantId,
			AsyncCallback<JSONValue> asyncCallback) throws RequestException;
	
	void removeDefaultTag(String projectId, String tag,
			AsyncCallback<JSONValue> asyncCallback) throws RequestException;
	
	void removeAutoTag(String projectId, String tag,
			AsyncCallback<JSONValue> asyncCallback) throws RequestException;

	void retrAdminConfig(String projectId, AsyncCallback<JSONValue> callback)
			throws RequestException;

	void addDigestManager(String projectId, String managerId,
			AsyncCallback<JSONValue> asyncCallback) throws RequestException;

	void removeDigestManager(String projectId, String managerId,
			AsyncCallback<JSONValue> asyncCallback) throws RequestException;

	void addDefaultParticipant(String projectId, String participantId,
			AsyncCallback<JSONValue> asyncCallback) throws RequestException;

	void addDefaultTag(String projectId, String tag,
			AsyncCallback<JSONValue> asyncCallback) throws RequestException;

	void addAutoTag(String projectId, String tag, String regex,
			AsyncCallback<JSONValue> asyncCallback) throws RequestException;

}
