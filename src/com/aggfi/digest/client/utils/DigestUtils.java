package com.aggfi.digest.client.utils;

//import org.cobogw.gwt.waveapi.gadget.client.WaveFeature;
//import com.google.gwt.gadgets.client.DynamicHeightFeature;
import com.google.gwt.user.client.Window;


public class DigestUtils {
	static DigestUtils instance = null;
//	WaveFeature wave;
//	DynamicHeightFeature height;
	private DigestUtils(){}
//	public String retrUserId() {
//		return "vega113@googlewave.com";
//	}
	
	
	public static DigestUtils getInstance(){
		if(instance == null){
			instance = new DigestUtils();
		}
		return instance;
	}
	
	public void alert(String msg) {
	Window.alert(msg);
}	
	
//	public String retrUserId() {
//		if(wave != null){
//			return wave.getViewer().getId();
//		}else{
//			return "";
//		}
//		
//	}
//	public void adjustHeight(){
//		if(height != null){
//			height.adjustHeight();
//		}
//	}
//	
//	
//	
//	public WaveFeature getWave() {
//		return wave;
//	}
//	public void setWave(WaveFeature wave) {
//		this.wave = wave;
//	}
//	public DynamicHeightFeature getHeight() {
//		return height;
//	}
//	public void setHeight(DynamicHeightFeature height) {
//		this.height = height;
//	}
//	public String retrUserName() {
//		return wave.;
//		
//	}
	
	
	
	
	public String retrUserId() {
		return "vega113@googlewave.com";
		
	}
	public String retrUserName() {
		return "Yuri Zelikov";
		
	}
	public void adjustHeight(){
	}
	
	
	
	
}
