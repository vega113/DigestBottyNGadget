package com.aggfi.digest.client.utils;

import java.util.HashMap;
import java.util.Map;
import com.google.gwt.user.client.Window;


public class DigestUtils {
	static DigestUtils instance = null;
	private static Map<String,String> state = new HashMap<String, String>();
	private DigestUtils(){}
	
	
	public static DigestUtils getInstance(){
		if(instance == null){
			instance = new DigestUtils();
		}
		return instance;
	}

	
	public String getCurrentDigestId(){
		String id = state.get("digestId");
		return id != null? id : "";
	}
	public void setCurrentDigestId(String id){
		state.put("digestId", id);
	}
	
	
	public void showStaticMessage(String msg) {
	}	
	public void dismissStaticMessage() {
	}
	public void dismissAllStaticMessages() {
	}
	public void showSuccessMessage(String msg, int seconds) {
		Window.alert(msg);
	}
	public void showTimerMessage(String msg, int seconds){
		Window.alert(msg);
	}
	public void recordPageView(String typeOfrecord) {
	}
	
	public String retrUserId() {
		return "vega113@googlewave.com";
		
	}
	public String retrUserName() {
		return "Yuri Zelikov";
		
	}
	public void adjustHeight(){
	}
	public void alert(String msg) {
		Window.alert(msg);
	}	
	public void dismissAlert(){
	}
	
}
