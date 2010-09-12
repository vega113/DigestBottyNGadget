

package com.aggfi.digest.client.inject;

import com.aggfi.digest.client.ui.AdsenseWidget;
import com.aggfi.digest.client.ui.DigestAboutWidget;
import com.aggfi.digest.client.ui.DigestAdminGeneralWidget;
import com.aggfi.digest.client.ui.DigestAdminParticipantWidget;
import com.aggfi.digest.client.ui.DigestTabPanel;
import com.aggfi.digest.client.ui.DigestTrackerWidget;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

@GinModules(DigestGinModule.class)
public interface DigestGinjector extends Ginjector {

	DigestTabPanel getDigestCreatedTabPanel();
	DigestAboutWidget getDigestAboutWidget();
	
	DigestAdminGeneralWidget getDigestAdminGeneralWidget();
	DigestAdminParticipantWidget getDigestAdminParticipantWidget();
	AdsenseWidget getAdsenseWidget();
	DigestTrackerWidget getDigestTrackerWidget();
}
