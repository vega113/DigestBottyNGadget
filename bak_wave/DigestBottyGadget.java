package com.aggfi.digest.client;


import com.aggfi.digest.client.inject.DigestGinjector; 
import com.aggfi.digest.client.ui.DigestTabPanel;
import com.aggfi.digest.client.utils.DigestUtils;
import com.allen_sauer.gwt.log.client.DivLogger;
import com.allen_sauer.gwt.log.client.Log;
//import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.gadgets.client.AnalyticsFeature;
import com.google.gwt.gadgets.client.DynamicHeightFeature;
import com.google.gwt.gadgets.client.GoogleAnalyticsFeature;
import com.google.gwt.gadgets.client.NeedsAnalytics;
import com.google.gwt.gadgets.client.NeedsDynamicHeight;
import com.google.gwt.gadgets.client.NeedsGoogleAnalytics;
import com.google.gwt.gadgets.client.UserPreferences;
import com.google.gwt.gadgets.client.Gadget.AllowHtmlQuirksMode;
import com.google.gwt.gadgets.client.Gadget.ModulePrefs;
import com.google.gwt.gadgets.client.Gadget.UseLongManifestName;

import org.cobogw.gwt.waveapi.gadget.client.WaveGadget;
import com.aggfi.digest.client.feature.minimessages.MiniMessagesFeature;
import com.aggfi.digest.client.feature.minimessages.NeedsMiniMessages;
import com.aggfi.digest.client.feature.views.NeedsViews;
import com.aggfi.digest.client.feature.views.ViewsFeature;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
//public class DigestBottyGadget implements EntryPoint {
@AllowHtmlQuirksMode
@UseLongManifestName
@ModulePrefs(title = "DigestBotty Admn Gadget",author="Yuri Zelikov",author_email="vega113+digestbottygadget@gmail.com", width=600, height=600)
public class DigestBottyGadget	extends WaveGadget<UserPreferences> implements NeedsDynamicHeight, NeedsMiniMessages, NeedsGoogleAnalytics, NeedsViews{

	
	@Override
	protected void init(UserPreferences preferences) {
		try{
			mmFeature.initMiniMessagesFeature();
			DigestUtils.getInstance().setMiniMessages(mmFeature);
			DigestUtils.getInstance().setHeight(dhFeature);
			DigestUtils.getInstance().setAnalytics(analyticsFeature);
			DigestUtils.getInstance().setViewsFeature(viewsFeature);
			
			DigestUtils.getInstance().setWave(getWave());// should be set before UI components will issue requests
			DigestGinjector ginjector = GWT.create(DigestGinjector.class);
			DigestTabPanel widget = ginjector.getDigestCreatedTabPanel();
			RootPanel.get().add(new HTML("."));
			dhFeature.getContentDiv().add(widget);
			initRemoteLogger(RootPanel.get());
		}catch(Exception e){
			initRemoteLogger(RootPanel.get());
			handleError(e);
		}
		Timer timer = new Timer() {
			
			@Override
			public void run() {
				dhFeature.adjustHeight();
			}
		};
		timer.scheduleRepeating(800);
	}

	DynamicHeightFeature dhFeature;
	@Override
	public void initializeFeature(DynamicHeightFeature feature) {
		dhFeature = feature;
		
	}
	
	private MiniMessagesFeature mmFeature;
	@Override
	public void initializeFeature(MiniMessagesFeature feature) {
		this.mmFeature = feature;
	}
	
	private GoogleAnalyticsFeature analyticsFeature;
	@Override
	public void initializeFeature(GoogleAnalyticsFeature analyticsFeature) {
		this.analyticsFeature = analyticsFeature;
	}
	
	private ViewsFeature viewsFeature;
	@Override
	public void initializeFeature(ViewsFeature feature) {
		this.viewsFeature = feature;
	}
	
	
	/**
	 * This is the entry point method.
	 */
	/*
	public void onModuleLoad() {
		DigestGinjector ginjector = GWT.create(DigestGinjector.class);
		DigestTabPanel widget = ginjector.getDigestCreatedTabPanel();
	    RootPanel.get("mainPanel").add(widget);
	    initRemoteLogger(RootPanel.get("logPanel"));
	}
	*/
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
