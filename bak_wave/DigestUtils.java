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
	
	/**
	 * @deprecated - use adjustHeight
	 */
	public void adjustHeightDeferred(){
		height.adjustHeight();
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
	public void dismissStaticMessage() {
		messages.dismissStaticMessage();
	}
	public void dismissAllStaticMessages() {
		messages.dismissAllStaticMessages();
	}
	public void showSuccessMessage(String msg, int seconds) {
		messages.showSuccessMessage(msg, seconds);
	}
	public void dismissAlert(){
		messages.dismissAlert();
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


	public void showTimerMessage(String msg, int seconds) {
		messages.createTimerMessage(msg, seconds);
		
	}
	
	
	
	
	/*
	public void showStaticMessage(String msg) {
	}	
	public void dismissStaticMessage() {
	}
	public void dismissAllStaticMessages() {
	}
	public void showSuccessMessage(String msg, int seconds) {
		Window.alert(msg);
	}
	
	
	public String retrUserId() {
		return "vega114@googlewave.com";
		
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
	*/
	
}
