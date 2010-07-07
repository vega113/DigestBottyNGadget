package com.aggfi.digest.client;


import com.aggfi.digest.client.inject.DigestGinjector;
import com.aggfi.digest.client.ui.DigestTabPanel;
import com.aggfi.digest.client.utils.DigestUtils;
import com.allen_sauer.gwt.log.client.DivLogger;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
//import com.google.gwt.gadgets.client.DynamicHeightFeature;
//import com.google.gwt.gadgets.client.NeedsDynamicHeight;
//import com.google.gwt.gadgets.client.UserPreferences;
//import com.google.gwt.gadgets.client.Gadget.ModulePrefs;
//import org.cobogw.gwt.waveapi.gadget.client.WaveGadget;
//import com.aggfi.digest.client.feature.minimessages.MiniMessagesFeature;
//import com.aggfi.digest.client.feature.minimessages.NeedsMiniMessages;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class DigestBottyGadget implements EntryPoint {
//@ModulePrefs(title = "DigestBotty Admn Gadget",author="Yuri Zelikov",author_email="vega113+digestbottygadget@gmail.com", width=540, height=400)
//public class DigestBottyGadget	extends WaveGadget<UserPreferences> implements NeedsDynamicHeight, NeedsMiniMessages{

	/*
	@Override
	protected void init(UserPreferences preferences) {
		try{
			mmFeature.initMiniMessagesFeature();
			DigestUtils.getInstance().setMiniMessages(mmFeature);
			DigestUtils.getInstance().setHeight(dhFeature);
			DigestUtils.getInstance().setWave(getWave());// should be set before UI components will issue requests
			DigestGinjector ginjector = GWT.create(DigestGinjector.class);
			DigestTabPanel widget = ginjector.getDigestCreatedTabPanel();
			dhFeature.getContentDiv().add(widget);
			dhFeature.adjustHeight();
			initRemoteLogger(RootPanel.get());
			dhFeature.adjustHeight();
		}catch(Exception e){
			initRemoteLogger(RootPanel.get());
			handleError(e);
		}
	}

	DynamicHeightFeature dhFeature;
	@Override
	public void initializeFeature(DynamicHeightFeature feature) {
		dhFeature = feature;
		
	}
	
	MiniMessagesFeature mmFeature;
	@Override
	public void initializeFeature(MiniMessagesFeature feature) {
		this.mmFeature = feature;
	}
	
	
	/**
	 * This is the entry point method.
	 */
	
	public void onModuleLoad() {
		DigestGinjector ginjector = GWT.create(DigestGinjector.class);
		DigestTabPanel widget = ginjector.getDigestCreatedTabPanel();
	    RootPanel.get("mainPanel").add(widget);
	    initRemoteLogger(RootPanel.get("logPanel"));
	}
	
	public void initRemoteLogger(AbsolutePanel panel){
		Log.setUncaughtExceptionHandler();
		if (panel != null) {
			panel.add (Log.getLogger(DivLogger.class).getWidget());
			Log.info("Logger initialized: " + Log.class.getName());
		}
	}

	private void handleError(Throwable error) {
		Log.error("", error);
	}

}
