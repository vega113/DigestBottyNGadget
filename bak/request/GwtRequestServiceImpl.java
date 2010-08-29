package com.aggfi.digest.client.request;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GwtRequestServiceImpl implements RequestService {

	@Override
	public void makeRequest(String url, final AsyncCallback<JSONValue> callback, JavaScriptObject params) throws RequestException {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, url);

		@SuppressWarnings("unused")
		Request request = null;
		String paramsStr = (new JSONObject(params)).toString();
		request = builder.sendRequest(paramsStr, new DigestRequestCallback(callback));
	}
}
