package com.aggfi.digest.client.model;


import com.google.gwt.core.client.JavaScriptObject;

public class JsParams extends JavaScriptObject {
	
	protected JsParams(){};

	public final native JsDigest getParams() /*-{
		return this.params;
	}-*/;
	
	public final native void setParams(JsDigest params) /*-{
		this.params = params;
	}-*/;
	
	public final native String getMethod() /*-{
		return this.method;
	}-*/;
	
	public final native void setMethod(String method) /*-{
		this.method = method;
	}-*/;
	
	
	
}

