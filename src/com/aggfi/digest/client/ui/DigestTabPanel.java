package com.aggfi.digest.client.ui;
import com.aggfi.digest.client.constants.SimpleConstants;
import com.aggfi.digest.client.constants.SimpleMessages;
import com.aggfi.digest.client.resources.GlobalResources;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
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
			DigestCreateWidget digestCreateWidget, final DigestReportWidget digestReportWidget,
			final DigestAdminWidget digestAdminWidget, final DigestAboutWidget digestAboutWidget){
		this.messages = messages;
		this.constants = constants;
		this.resources = resources;
		this.digestCreateWidget = digestCreateWidget;
		
		this.setAnimationEnabled(true);
		
		this.add(digestAboutWidget, constants.aboutTabStr());
		this.selectTab(0);
		this.add(digestCreateWidget, constants.createTabStr());
		this.add(digestAdminWidget, constants.adminTabStr());
		this.add(digestReportWidget, constants.reportTabStr());
		
		this.addSelectionHandler(new SelectionHandler<Integer>() {
			
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				int selected = event.getSelectedItem();
				Object currentSelectedWidget = getWidget(selected);
				if(currentSelectedWidget != null && currentSelectedWidget instanceof RunnableOnTabSelect){
					RunnableOnTabSelect runnableOnTabSelect = ((RunnableOnTabSelect)currentSelectedWidget);
					if(runnableOnTabSelect.getRunOnTabSelect() != null){
						runnableOnTabSelect.getRunOnTabSelect().run();
					}
				}
			}
		});
	}
}
