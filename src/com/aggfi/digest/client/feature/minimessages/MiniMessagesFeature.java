package com.aggfi.digest.client.feature.minimessages;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.gadgets.client.GadgetFeature;


public class MiniMessagesFeature implements GadgetFeature {
	MiniMessages messages;

	private native MiniMessages initMessages() /*-{
		return new gadgets.MiniMessage("digestbottygadget");
	}-*/;

	public void initMiniMessagesFeature(){
		try{
			messages = initMessages();
		}catch (Exception e) {
			Log.error("", e);
		}
	}


	public  void createDismissibleMessage(String message) {
		try{
			messages.createDismissibleMessage(message);
		}catch (Exception e) {
			Log.error("", e);
		}
	}

	public void createStaticMessage(String message){
		try{
			messages.createStaticMessage(message);
		}catch (Exception e) {
			Log.error("", e);
		}
	}

	public  void createTimerMessage(String message) {
		try{
			messages.createTimerMessage(message);
		}catch (Exception e) {
			Log.error("", e);
		}
	}

	public  void dismissMessage() {
		try{
			messages.dismissMessage();
		}catch (Exception e) {
			Log.error("", e);
		}
	}
	
	public  void alert(String message) {
		try{
			messages.alert(message);
		}catch (Exception e) {
			Log.error("", e);
		}
	}
}
