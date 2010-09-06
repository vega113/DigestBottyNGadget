package com.aggfi.digest.client;


import com.aggfi.digest.client.inject.DigestGinjector;
import com.aggfi.digest.client.ui.DigestTabPanel;
import com.allen_sauer.gwt.log.client.DivLogger;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.gadgets.client.Gadget.AllowHtmlQuirksMode;
import com.google.gwt.gadgets.client.Gadget.UseLongManifestName;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
//public class DigestBottyGadget implements EntryPoint {
@AllowHtmlQuirksMode
@UseLongManifestName
public class DigestBottyGadget implements EntryPoint {
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
}
