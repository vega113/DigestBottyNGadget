package com.aggfi.digest.client.request;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
//FIXME this implementation doesn't work :( - some bug I guess
public class GadgetRequestServiceImpl implements RequestService {
	AsyncCallback<String> callback;
	@SuppressWarnings("unchecked")
	@Override
	public void makeRequest(String url, final AsyncCallback callback,
			JavaScriptObject params) throws RequestException {
		Log.debug("Entering json1 makeRequest, url: " + url + ", params: " + (new JSONObject(params)).toString());
		this.callback = callback;
		try{
			makePostRequest(url,new AsyncCallback<Object>() {
				
				@Override
				public void onSuccess(Object responseStr) {
					Log.debug("Entering makePostRequest::onSuccess");
					String outMessage = null;
					try{
						JSONValue jsonVal = null;
						try{
							jsonVal = JSONParser.parse(String.valueOf(responseStr));
						}catch(Exception e){
							throw new Exception(String.valueOf(responseStr));
						}
						if(jsonVal.isObject().containsKey("error")){
							throw new Exception(jsonVal.isObject().get("error").isString().stringValue());
						}else{
							if(jsonVal.isObject().containsKey("result")){
								JSONObject resultJson = jsonVal.isObject().get("result").isObject();
								if(resultJson.isObject().containsKey("message")){
									outMessage = resultJson.isObject().get("message").isString().stringValue();
									callback.onSuccess(outMessage);
								}
							}else{
								throw new Exception(String.valueOf(responseStr));
							}
						}
					}catch(Exception e){
						callback.onFailure(e);
						Log.error("", e);
						return;
					}
					Log.debug("Exiting makePostRequest::onSuccess");
				}
				
				@Override
				public void onFailure(Throwable caught) {
					Log.error("onFailure: Error during makeRequest.", caught);
				}
			} , params);
		}catch(Exception e){
			Log.error("", e);
			throw new RequestException(e);
		}
		Log.debug("Exiting makeRequest");
	}

	/**
	 * Makes Post Request using gadgets.io.makeRequest for use with Google gadgets. The Response return type is JSON.
	 * @param url
	 * @param tAsyncCallback
	 * @param paramsIn - The JavaScriptObject representation of the input.
	 */
	private final native void makePostRequest(String url,AsyncCallback<Object> tAsyncCallback, JavaScriptObject paramsIn) /*-{
		var params = {};
		params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.POST;
		params[gadgets.io.RequestParameters.REFRESH_INTERVAL] = 1;
		params[gadgets.io.RequestParameters.CONTENT_TYPE] = gadgets.io.ContentType.JSON;
		params[gadgets.io.RequestParameters.POST_DATA]  = gadgets.io.encodeValues(gadgets.json.stringify(paramsIn));
		
		gadgets.io.makeRequest(url, function(resp) { 
          if(resp.errors && resp.errors.length > 0) {
            tAsyncCallback.@com.google.gwt.user.client.rpc.AsyncCallback::onSuccess(Ljava/lang/Object;)(resp.errors)
          }
          else {
          	tAsyncCallback.@com.google.gwt.user.client.rpc.AsyncCallback::onSuccess(Ljava/lang/Object;)(resp.text);
          }
      }, params);
	}-*/;
}
