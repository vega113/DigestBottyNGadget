package com.aggfi.digest.client.request;

import com.aggfi.digest.client.utils.DigestUtils;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DigestRequestCallback implements RequestCallback{
	
	AsyncCallback<JSONValue> callback = null; 
	
	public DigestRequestCallback(AsyncCallback<JSONValue> callback){
		this.callback = callback;
	}

	@Override
	public void onError(Request request, Throwable exception) {
		callback.onFailure(exception);
	}

	@Override
	public void onResponseReceived(Request request,
			Response response) {
		String responseStr = null;
		try{
			responseStr = response.getText();
			JSONValue jsonVal = JSONParser.parse(responseStr);
			if(jsonVal.isObject().containsKey("error")){
				String errorMsg = jsonVal.isObject().get("error").isString().stringValue();
				throw new Exception(errorMsg);
			}else if(jsonVal.isObject().containsKey("result")){
					callback.onSuccess(jsonVal.isObject().get("result"));
					
			}else if(jsonVal.isObject().containsKey("success")){
					callback.onSuccess(jsonVal.isObject().get("success"));
					
			}
		}catch(Exception e){
			callback.onFailure(e);
			return;
		}
	}
}


