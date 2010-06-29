package com.aggfi.digest.client.ui;
import com.aggfi.digest.client.constants.SimpleConstants;
import com.aggfi.digest.client.constants.SimpleMessages;
import com.aggfi.digest.client.resources.GlobalResources;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.inject.Inject;

public class DigestTabPanel extends DecoratedTabPanel {

	protected SimpleMessages messages;
	protected SimpleConstants constants;
	protected GlobalResources resources;
	protected DigestCreateWidget digestCreateWidget;
	
	
	@Inject
	public DigestTabPanel(final SimpleMessages messages, final SimpleConstants constants, final GlobalResources resources,
			DigestCreateWidget digestCreateWidget, DigestReportWidget digestReportWidget,
			DigestAdminWidget digestAdminWidget){
		this.messages = messages;
		this.constants = constants;
		this.resources = resources;
		this.digestCreateWidget = digestCreateWidget;
		
		this.setAnimationEnabled(true);
		this.add(digestCreateWidget, "Create");
		this.selectTab(0);
		this.setWidth("380px");
//		adminHTml.setSize(String.valueOf(digestCreateWidget.getOffsetWidth())+"px", String.valueOf(digestCreateWidget.getOffsetHeight()) + "px");
		this.add(digestAdminWidget, "Admin");
		this.add(digestReportWidget, "Report");
	}
}
