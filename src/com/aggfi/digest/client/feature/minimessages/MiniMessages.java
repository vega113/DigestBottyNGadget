package com.aggfi.digest.client.feature.minimessages;

import com.google.gwt.core.client.JavaScriptObject;

public final class MiniMessages extends JavaScriptObject {

	protected MiniMessages(){}
	//TODO need some stack implementation to store msgs
	public native void createDismissibleMessage(String message) /*-{
		$wnd.gwtHtmlMsg = this.createDismissibleMessage(message);
	}-*/;
	
	public native void createStaticMessage(String message) /*-{
		$wnd.gwtHtmlMsg = this.createStaticMessage(message);
	}-*/;
	
	public native void createTimerMessage(String message) /*-{
		$wnd.gwtHtmlMsg = this.createTimerMessage(message, seconds)
	}-*/;
	
	public native void dismissMessage() /*-{
		this.dismissMessage($wnd.gwtHtmlMsg);
	}-*/;
	
	public native void alert(String message) /*-{
		$wnd.gwtHtmlMsg = this.createDismissibleMessage(message);
	  	statusMsg.style.backgroundColor = "red";
	  	statusMsg.style.color = "white";
	}-*/;
	
}
