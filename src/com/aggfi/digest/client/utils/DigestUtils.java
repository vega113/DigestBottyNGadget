package com.aggfi.digest.client.utils;

import org.cobogw.gwt.waveapi.gadget.client.WaveFeature;

import com.aggfi.digest.client.feature.minimessages.MiniMessagesFeature;
import com.google.gwt.gadgets.client.DynamicHeightFeature;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.Window;


public class DigestUtils {
	static DigestUtils instance = null;
	private WaveFeature wave;
	private DynamicHeightFeature height;
	private MiniMessagesFeature messages;
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
	
	public String retrUserId() {
		if(wave != null && wave.getViewer() != null){
			return wave.getViewer().getId();
		}else{
			return "";
		}
		
	}
	public void adjustHeight(){
		if(height != null){
			height.adjustHeight();
		}
	}
	
	public String retrUserName() {
		if(wave != null && wave.getViewer() != null){
			return wave.getViewer().getDisplayName();
		}else{
			return "";
		}
		
	}
	
	public void alert(String msg) {
		messages.alert(msg);
	}	
	public void showStaticMessage(String msg) {
		messages.createStaticMessage(msg);
	}	
	public void dismissMessage() {
		messages.dismissMessage();
	}
	public WaveFeature getWave() {
		return wave;
	}
	public void setWave(WaveFeature wave) {
		this.wave = wave;
	}
	public DynamicHeightFeature getHeight() {
		return height;
	}
	public void setHeight(DynamicHeightFeature height) {
		this.height = height;
	}


	public void setMiniMessages(MiniMessagesFeature mmFeature) {
		this.messages = mmFeature;
		
	}
	
	
	
	
	/*
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
	*/
	
	
}
