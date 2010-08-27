package com.aggfi.digest.client.feature.views;

import com.google.gwt.core.client.JavaScriptObject;

public final class Views extends JavaScriptObject {

	protected Views(){}
	

	public native void requestNavigateTo(String view, String optParams) /*-{
		this.requestNavigateTo(view,optParams);
	}-*/;
	
}
