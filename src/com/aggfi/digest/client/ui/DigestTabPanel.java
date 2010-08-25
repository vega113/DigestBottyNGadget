package com.aggfi.digest.client.ui;
import com.aggfi.digest.client.constants.DigestConstants;
import com.aggfi.digest.client.constants.DigestMessages;
import com.aggfi.digest.client.resources.GlobalResources;
import com.allen_sauer.gwt.log.client.Log;
import com.vegalabs.general.client.utils.VegaUtils;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.inject.Inject;

public class DigestTabPanel extends DecoratedTabPanel {

	protected DigestMessages messages;
	protected DigestConstants constants;
	protected GlobalResources resources;
	protected DigestCreateWidget digestCreateWidget;
	@Inject
	public DigestTabPanel(final DigestMessages messages, final DigestConstants constants, final GlobalResources resources,
			DigestCreateWidget digestCreateWidget, final DigestReportWidget digestReportWidget,
			final DigestAdminWidget digestAdminWidget, final DigestAboutWidget digestAboutWidget, final VegaUtils utils){
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
				if(utils == null){
					Log.warn("utils is null");
				}else{
					Log.info("utils is not null");
				}
				try{
					utils.dismissAllStaticMessages();
				}catch(Exception e){
					Log.warn("problem with messages? " + e.getMessage());
				}
				int selected = event.getSelectedItem();
				Object currentSelectedWidget = getWidget(selected);
				if(currentSelectedWidget != null && currentSelectedWidget instanceof RunnableOnTabSelect){
					RunnableOnTabSelect runnableOnTabSelect = ((RunnableOnTabSelect)currentSelectedWidget);
					if(runnableOnTabSelect.getRunOnTabSelect() != null){
						utils.reportPageview("/digestbotty/" + runnableOnTabSelect.getName());
						runnableOnTabSelect.getRunOnTabSelect().run();
					}
				}
			}
		});
	}
}
