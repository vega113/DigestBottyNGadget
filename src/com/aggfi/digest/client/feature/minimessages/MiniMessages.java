package com.aggfi.digest.client.feature.minimessages;

import com.google.gwt.core.client.JavaScriptObject;

public final class MiniMessages extends JavaScriptObject {

	protected MiniMessages(){}
	public native void createDismissibleMessage(String message) /*-{
		this.createDismissibleMessage(message);
	}-*/;
	
	public native void createStaticMessage(String message) /*-{
		$wnd.msgCounter = $wnd.msgCounter + 1;
		$wnd.gwtHtmlMsg[$wnd.msgCounter] = this.createStaticMessage(message);
	}-*/;
	
	public native void createTimerMessage(String message) /*-{
		this.createTimerMessage(message, seconds);
	}-*/;
	
	public native void dismissStaticMessage() /*-{
		if($wnd.msgCounter > 0){
			this.dismissMessage($wnd.gwtHtmlMsg[$wnd.msgCounter]);
			$wnd.msgCounter = $wnd.msgCounter - 1;
		}
	}-*/;
	
	public native void alert(String message) /*-{
		$wnd.msgAlertCounter = $wnd.msgAlertCounter + 1;
		$wnd.gwtAlertHtmlMsg[$wnd.msgAlertCounter] = this.createDismissibleMessage(message);
	  	msgElement.style.backgroundColor = "red";
	  	msgElement.style.color = "white";
	}-*/;
	
	public native void dismissAlert() /*-{
	if($wnd.msgAlertCounter > 0){
		this.dismissMessage($wnd.gwtAlertHtmlMsg[$wnd.msgAlertCounter]);
		$wnd.msgAlertCounter = $wnd.msgAlertCounter - 1;
	}
}-*/;
	
	public native void success(String message, int seconds) /*-{
		var msgElement = this.createTimerMessage(message, seconds);
	  	msgElement.style.color = "green";
	}-*/;
	
	public native void dismissAllStaticMessages() /*-{
		while($wnd.msgCounter > 0){
			this.dismissMessage($wnd.gwtHtmlMsg[$wnd.msgCounter]);
			$wnd.msgCounter = $wnd.msgCounter - 1;
		}
	}-*/;
	
}
