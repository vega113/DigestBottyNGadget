package com.aggfi.digest.client.ui;

import com.aggfi.digest.client.constants.DigestConstants;
import com.aggfi.digest.client.constants.DigestMessages;
import com.aggfi.digest.client.resources.GlobalResources;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.vegalabs.general.client.utils.VegaUtils;

public class PersonalTabWidget extends Composite implements RunnableOnTabSelect {

	private static PersonalTabWidgetUiBinder uiBinder = GWT
			.create(PersonalTabWidgetUiBinder.class);

	interface PersonalTabWidgetUiBinder extends
			UiBinder<Widget, PersonalTabWidget> {
	}
	
	@UiField
	Label helloUserLbl;
	@UiField
	TabPanel personalTabsTabPnl;
	
	protected DigestMessages messages;
	protected DigestConstants constants;
	protected GlobalResources resources;
	protected AdsenseWidget adsenseWidget;
	protected VegaUtils utils;
	
	
	@Inject
	public PersonalTabWidget( final DigestMessages messages, final DigestConstants constants, final GlobalResources resources,
			 final AdsenseWidget adsenseWidget, final VegaUtils utils) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.adsenseWidget = adsenseWidget;
		this.messages = messages;
		this.constants = constants;
		this.resources = resources;
		this.utils = utils;
		
		adsenseWidget.setIsUserOrForumMode(true, null, utils.retrUserId());
		personalTabsTabPnl.setAnimationEnabled(true);
		personalTabsTabPnl.add(adsenseWidget, createHeader(constants.adsenseTabStr(),resources.globalCSS().highlight()));
		personalTabsTabPnl.selectTab(0);
		adsenseWidget.setImgExplTitle(constants.personalAdSenseExpl());
		personalTabsTabPnl.setVisible(true);
		personalTabsTabPnl.setWidth(constants.basicWidthStr());
		
		
		personalTabsTabPnl.addSelectionHandler(new SelectionHandler<Integer>() {
			
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				try{
					utils.dismissAllStaticMessages();
				}catch(Exception e){
					Log.warn("problem with messages? " + e.getMessage());
				}
				int selected = event.getSelectedItem();
				Object currentSelectedWidget = personalTabsTabPnl.getWidget(selected);
				if(currentSelectedWidget != null && currentSelectedWidget instanceof RunnableOnTabSelect){
					RunnableOnTabSelect runnableOnTabSelect = ((RunnableOnTabSelect)currentSelectedWidget);
					if(runnableOnTabSelect.getRunOnTabSelect() != null){
						runnableOnTabSelect.getRunOnTabSelect().run();
					}
				}
			}
		});
		
		
		
	}

	
	private Widget createHeader(String adsenseTabStr, String highlight) {
		Label header = new Label(adsenseTabStr);
//		header.setStylePrimaryName(highlight);
		return header;
	}


	Runnable runOnTabSelect = new Runnable() {
		@Override
		public void run() {
			Log.debug("PersonalTabWidget: Running");
			initPersonalTab();
			adsenseWidget.getRunOnTabSelect().run();
			utils.reportPageview("/personalTab/");
			utils.adjustHeight();
		}
	};
	
	@Override
	public Runnable getRunOnTabSelect() {
		return runOnTabSelect;
	}

	protected void initPersonalTab() {
		String userName = utils.retrUserName();
		String helloMsg = messages.helloUserMsg(userName);
		helloUserLbl.setText(helloMsg);
	}

	@Override
	public String getName() {
		return null;
	}
	

}
