package com.aggfi.digest.client.service;


import com.aggfi.digest.client.model.JsDigest;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TextBox;

public interface DigestService {
	void createDigest(JsDigest digest, AsyncCallback<JSONValue> callback) throws RequestException;
	
	void retrTagsDistributions(String projectId, AsyncCallback<JSONValue> callback) throws RequestException;

	void retrPrjectsPerUserId(String userId, AsyncCallback<JSONValue> callback) throws RequestException;

	void retrPostCounts(String value, AsyncCallback<JSONValue> asyncCallback) throws RequestException;

	void removeDefaultParticipant(String projectId, String participantId,
			AsyncCallback<JSONValue> asyncCallback) throws RequestException;
	
	void removeDefaultTag(String projectId, String tag,
			AsyncCallback<JSONValue> asyncCallback) throws RequestException;
	
	void removeAutoTag(String projectId, String tag,
			String sync, AsyncCallback<JSONValue> asyncCallback) throws RequestException;

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
			String isSync, AsyncCallback<JSONValue> asyncCallback) throws RequestException;

	void addWavesParticipant(String projectId, String participantId,
			String tagName, AsyncCallback<JSONValue> asyncCallback) throws RequestException;
	
	void retrBlipsCounts(String projectId,AsyncCallback<JSONValue> asyncCallback) throws RequestException;
	
	void retrBlipsPerContributor(String projectId,AsyncCallback<JSONValue> asyncCallback) throws RequestException;

	void retrContributorsPerInfluence(String projectId,AsyncCallback<JSONValue> asyncCallback) throws RequestException;
	void retrPostsByActivity(String projectId,AsyncCallback<JSONValue> asyncCallback) throws RequestException;

	void updateAtomFeedPublic(String projectId, boolean isMakeAtomFeedPublic,
			AsyncCallback<JSONValue> asyncCallback) throws RequestException;

	void addSecurePostGadget(String projectId, String userId,
			AsyncCallback<JSONValue> asyncCallback) throws RequestException;

	void updateSocialBtnsSettings(String projectId,
			boolean isDiggEnabled, boolean isBuzzEnabled,
			boolean isTweetEnabled, boolean isFaceEnabled,
			AsyncCallback<JSONValue> asyncCallback) throws RequestException;

	void addAdSenseCode(String projectId, String userId, String adSenseCode,
			String userName, String userThumbnailUrl,
			boolean isAdSenseUpdate4User, AsyncCallback<JSONValue> asyncCallback)
			throws RequestException;

	void getAdSenseCode(String projectId, String userId,
			boolean isAdSenseUpdate4User, AsyncCallback<JSONValue> asyncCallback)
			throws RequestException;

	void updateViewsTracking(String projectId, boolean isUpdateViewsTracking, boolean isSync,
			AsyncCallback<JSONValue> asyncCallback) throws RequestException;

	void retrViewsCounts(String projectId,
			AsyncCallback<JSONValue> asyncCallback) throws RequestException;

	void retrPostsByViews(String projectId,
			AsyncCallback<JSONValue> asyncCallback) throws RequestException;

	void addAdSenseInstaller(String projectId, String userId,
			AsyncCallback<JSONValue> asyncCallback) throws RequestException;

	void addAdSenseInstaller(String userId,
			AsyncCallback<JSONValue> asyncCallback) throws RequestException;
	
	void retrDigestInfo(String projectId,
			AsyncCallback<JSONValue> asyncCallback) throws RequestException;
	
	void updateDigestInfo(String projectId,
			String authorName, String forumName, String description, String installerThumbnailUrl, String forumSiteUrl, AsyncCallback<JSONValue> asyncCallback) throws RequestException;


}
