package com.aggfi.digest.client;


import com.aggfi.digest.client.inject.DigestGinjector;
import com.aggfi.digest.client.ui.DigestTabPanel;
import com.allen_sauer.gwt.log.client.DivLogger;
import com.allen_sauer.gwt.log.client.Log;
import org.cobogw.gwt.waveapi.gadget.client.WaveFeature;
import org.cobogw.gwt.waveapi.gadget.client.WaveGadget;
import com.vegalabs.features.client.feature.minimessages.MiniMessagesFeature;
import com.vegalabs.features.client.feature.minimessages.NeedsMiniMessages;
import com.vegalabs.features.client.feature.views.NeedsViews;
import com.vegalabs.features.client.feature.views.ViewsFeature;
import com.vegalabs.general.client.objects.GoogleAnalyticsId;
import com.google.gwt.gadgets.client.DynamicHeightFeature;
import com.google.gwt.gadgets.client.GoogleAnalyticsFeature;
import com.google.gwt.gadgets.client.NeedsDynamicHeight;
import com.google.gwt.gadgets.client.NeedsGoogleAnalytics;
import com.google.gwt.gadgets.client.UserPreferences;
import com.google.gwt.gadgets.client.Gadget.ModulePrefs;
import com.google.gwt.core.client.GWT;
import com.google.gwt.gadgets.client.Gadget.AllowHtmlQuirksMode;
import com.google.gwt.gadgets.client.Gadget.UseLongManifestName;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.inject.Provider;


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
			waveFeature = getWave();
			DigestGinjector ginjector = GWT.create(DigestGinjector.class);
			DigestTabPanel widget = ginjector.getDigestCreatedTabPanel();
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


static WaveFeature waveFeature;
	
	static DynamicHeightFeature dhFeature;
	@Override
	public void initializeFeature(DynamicHeightFeature feature) {
		DigestBottyGadget.dhFeature = feature;
		
	}
	
	static private MiniMessagesFeature mmFeature;
	@Override
	public void initializeFeature(MiniMessagesFeature feature) {
		DigestBottyGadget.mmFeature = feature;
	}
	
	static private ViewsFeature viewsFeature;
	@Override
	public void initializeFeature(ViewsFeature feature) {
		DigestBottyGadget.viewsFeature = feature;
	}
	
	static private GoogleAnalyticsFeature analyticsFeature;
	@Override
	public void initializeFeature(GoogleAnalyticsFeature analyticsFeature) {
		DigestBottyGadget.analyticsFeature = analyticsFeature;
	}
	
	public static class AnalyticsFeatureProvider implements Provider<GoogleAnalyticsFeature>{
		@Override
		public GoogleAnalyticsFeature get() {
			Log.info("Providing AnalyticsFeature");
			return DigestBottyGadget.analyticsFeature;
		}
	}
	
	public static class MiniMessagesFeatureProvider implements Provider<MiniMessagesFeature>{
		@Override
		public MiniMessagesFeature get() {
			Log.info("Providing MiniMessagesFeature");
			return DigestBottyGadget.mmFeature;
		}
	}
	
	public static class DynamicHeightFeatureProvider implements Provider<DynamicHeightFeature>{
		@Override
		public DynamicHeightFeature get() {
			Log.info("Providing DynamicHeightFeature");
			return DigestBottyGadget.dhFeature;
		}
	}
	
	public static class WaveFeatureProvider implements Provider<WaveFeature>{
		@Override
		public WaveFeature get() {
			Log.info("Providing WaveFeature");
			return DigestBottyGadget.waveFeature;
		}
	}
	
	public static class ViewsFeatureProvider implements Provider<ViewsFeature>{
		@Override
		public ViewsFeature get() {
			Log.info("Providing ViewsFeature");
			return DigestBottyGadget.viewsFeature;
		}
	}
	
	public static class AnalyticsIdFeatureProvider implements Provider<GoogleAnalyticsId>{
		@Override
		public GoogleAnalyticsId get() {
			Log.info("Providing GoogleAnalyticsId");
			return new GoogleAnalyticsId("UA-13269470-3");
		}
	}


	
	
}
