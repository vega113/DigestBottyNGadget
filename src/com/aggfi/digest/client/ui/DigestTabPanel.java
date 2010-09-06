package com.aggfi.digest.client.ui;
import com.aggfi.digest.client.constants.DigestConstants;
import com.aggfi.digest.client.constants.DigestMessages;
import com.aggfi.digest.client.resources.GlobalResources;
import com.aggfi.digest.client.service.DigestService;
import com.allen_sauer.gwt.log.client.Log;
import com.vegalabs.general.client.utils.VegaUtils;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
			final DigestAdminWidget digestAdminWidget, final DigestAboutWidget digestAboutWidget, final PersonalTabWidget personalTabWidget, final VegaUtils utils, DigestService service){
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
		this.add(personalTabWidget, constants.personalTabStr());
		
		//let's make a dummy request - to make sure the appengine jvm is up
		try {
			service.getAdSenseCode("vega113@googlewave.com", "vega113@googlewave.com", true, new AsyncCallback<JSONValue>() {
				@Override
				public void onSuccess(JSONValue result) {
				}
				@Override
				public void onFailure(Throwable caught) {
				}
			});
		} catch (RequestException e1) {
			//just heating up
		}
		
		this.addSelectionHandler(new SelectionHandler<Integer>() {
			
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
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
