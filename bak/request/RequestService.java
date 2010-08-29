package com.aggfi.digest.client.request;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RequestService {
	public void makeRequest(String url, AsyncCallback<JSONValue> callback, JavaScriptObject params) throws RequestException;
}
