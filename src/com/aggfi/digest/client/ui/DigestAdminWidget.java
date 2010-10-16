package com.aggfi.digest.client.ui;


import com.aggfi.digest.client.constants.DigestConstants;
import com.aggfi.digest.client.constants.DigestMessages;
import com.aggfi.digest.client.inject.DigestGinjector;
import com.aggfi.digest.client.resources.GlobalResources;
import com.aggfi.digest.client.service.DigestService;
import com.aggfi.digest.client.ui.DigestCreateWidget.ClearWarningClickHandler;
import com.vegalabs.general.client.utils.VegaUtils;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.inject.Inject;

public class DigestAdminWidget extends Composite implements RunnableOnTabSelect{
	
	@UiField
	DigestAdminSimplePanel prjListContainer;
	@UiField
	TabPanel adminSettingsTabPnl;
	
	
	ProjectSelectWidget projectSelectWidget;
	

	private static DigestAdminWidgetUiBinder uiBinder = GWT
			.create(DigestAdminWidgetUiBinder.class);
	protected Runnable onProjectsLoadCallback;

	interface DigestAdminWidgetUiBinder extends
			UiBinder<Widget, DigestAdminWidget> {
	}
	
	DigestService digestService;
	DigestMessages messages;
	DigestConstants constants;
	GlobalResources resources;
	private VegaUtils vegaUtils;
	
	public Runnable runOnTabSelect;



	private DigestGinjector ginjector;
	DigestAdminGeneralWidget digestAdminGeneralWidget = null;;
	DigestAdminParticipantWidget digestAdminParticipantWidget = null;
	AdsenseWidget adSenseWidget = null;
	DigestTrackerWidget trackerWidget = null;
	ForumUpdateWidget forumUpdateWidget = null;
	
	
	@Inject
	public DigestAdminWidget(final DigestMessages messages, final DigestConstants constants, final GlobalResources resources, final DigestService digestService, final VegaUtils vegaUtils, DigestGinjector ginjector) {
		initWidget(uiBinder.createAndBindUi(this));
		hideAll();
		resources.globalCSS().ensureInjected();
		this.digestService = digestService;
		this.messages = messages;
		this.constants = constants;
		this.resources = resources;
		this.vegaUtils = vegaUtils;
		this.ginjector = ginjector;
		initImageHandler();

		onProjectsLoadCallback = new Runnable() { //will be run after projectIds list is loaded

			@Override
			public void run() {
				DeferredCommand.addCommand(new Command() {
					
					@Override
					public void execute() {

						Log.debug("DigestAdminWidget::DigestReportWidget Running");
						String projectId = getProjectId();
						String projectName = getProjectName();
						if(projectId == null || "".equals(projectId) || "none".equals(projectId)){
							hideAll();
							return;
						}
						addTabs();
						adSenseWidget.setIsUserOrForumMode(false, projectName,projectId);
						try {
							clearAll();
							hideAll();
							String msg = messages.loadingForumsMsg(constants.adminTabStr(), getProjectName());
							Log.debug(msg);
							vegaUtils.showStaticMessage(msg);
							digestService.retrAdminConfig(projectId, new AsyncCallback<JSONValue>() {

								@Override
								public void onSuccess(JSONValue result) {
									digestAdminParticipantWidget.getDigestAdminParticipantCallback().onSuccess(result);
									digestAdminGeneralWidget.getDigestAdminGeneralCallback().onSuccess(result);
									trackerWidget.getDigestTrackerCallback().onSuccess(result);
									adminSettingsTabPnl.selectTab(0);
									
									
									
									boolean isAdsEnabled = result.isObject().get("isAdsEnabled").isBoolean().booleanValue();
									if(isAdsEnabled && adminSettingsTabPnl.getWidgetIndex(adSenseWidget) < 0){
										adminSettingsTabPnl.add(adSenseWidget, constants.adsenseTabStr());
									}else if(!isAdsEnabled) {
										try{
											adminSettingsTabPnl.remove(adSenseWidget);//XXX there's something buggy. It seems like it adds several adsense widgets
										}catch(Exception e){
											
										}
									}
									Log.debug(result.toString());
									vegaUtils.dismissStaticMessage();
								}

								@Override
								public void onFailure(Throwable caught) {
									Log.error("", caught);
									vegaUtils.dismissAllStaticMessages();
									digestAdminGeneralWidget.getDigestAdminGeneralCallback().onFailure(caught);
									digestAdminParticipantWidget.getDigestAdminParticipantCallback().onFailure(caught);
									trackerWidget.getDigestTrackerCallback().onFailure(caught);
									vegaUtils.adjustHeight();
									vegaUtils.alert(caught.getMessage());
								}
							});
						} catch (RequestException e) {
							vegaUtils.dismissStaticMessage();
							Log.error("", e);
						}
					
						
					}
				});
			}
		};




		runOnTabSelect = new Runnable() {

			@Override
			public void run() {
				initAdminWidget();
				vegaUtils.adjustHeight();
			}
		};

	}

	
	
	
	private void initAdminWidget() {
//		clearAll();
		if(projectSelectWidget == null){
			this.projectSelectWidget = new ProjectSelectWidget(messages, constants, resources, digestService, onProjectsLoadCallback,vegaUtils);
			projectSelectWidget.getPrjList().addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					handleOnSelectPrjList(event);
				}
			});
		}
		prjListContainer.clear();
		prjListContainer.add(projectSelectWidget);
		
		
		adminSettingsTabPnl.addSelectionHandler(new SelectionHandler<Integer>() {
			
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				try{
					vegaUtils.dismissAllStaticMessages();
				}catch(Exception e){
					Log.warn("problem with messages? " + e.getMessage());
				}
				int selected = event.getSelectedItem();
				Object currentSelectedWidget = adminSettingsTabPnl.getWidget(selected);
				if(currentSelectedWidget != null && currentSelectedWidget instanceof RunnableOnTabSelect){
					RunnableOnTabSelect runnableOnTabSelect = ((RunnableOnTabSelect)currentSelectedWidget);
					if(runnableOnTabSelect.getRunOnTabSelect() != null){
						runnableOnTabSelect.getRunOnTabSelect().run();
					}
				}
				vegaUtils.reportPageview("/adminTab/");
				vegaUtils.adjustHeight();
			}
		});
		if(adminSettingsTabPnl.getWidgetCount() > 0 ){
			adminSettingsTabPnl.selectTab(0);
		}
		
	}




	private void addTabs() {
		if(digestAdminGeneralWidget == null || digestAdminParticipantWidget == null || adSenseWidget == null){
			digestAdminGeneralWidget = ginjector.getDigestAdminGeneralWidget();
			digestAdminParticipantWidget = ginjector.getDigestAdminParticipantWidget();
			adSenseWidget = ginjector.getAdsenseWidget();
			trackerWidget = ginjector.getDigestTrackerWidget();
			forumUpdateWidget = ginjector.getForumUpdateWidget();
			
			adminSettingsTabPnl.setAnimationEnabled(true);
			initAdminTabsOnPrjChange();
			adminSettingsTabPnl.add(digestAdminParticipantWidget, constants.participantSettingsStr());
			adminSettingsTabPnl.add(digestAdminGeneralWidget, constants.generalSettingsStr());
			
			adminSettingsTabPnl.add(trackerWidget, constants.trackerSettingsStr());
			adminSettingsTabPnl.add(forumUpdateWidget, constants.forumSettingsStr());
			
			adSenseWidget.setImgExplTitle(constants.forumAdSenseExpl());
			adminSettingsTabPnl.selectTab(0);
			digestAdminParticipantWidget.getRunOnTabSelect().run();
			vegaUtils.adjustHeight();
			
		}
		vegaUtils.adjustHeight();
	}

	public void initAdminTabsOnPrjChange() {
		digestAdminGeneralWidget.setOnProjectsLoadCallback(onProjectsLoadCallback);
		digestAdminGeneralWidget.setProjectSelectWidget(projectSelectWidget);
		digestAdminParticipantWidget.setOnProjectsLoadCallback(onProjectsLoadCallback);
		digestAdminParticipantWidget.setProjectSelectWidget(projectSelectWidget);
		trackerWidget.setProjectSelectWidget(projectSelectWidget);
		forumUpdateWidget.setProjectSelectWidget(projectSelectWidget);
		vegaUtils.adjustHeight();
	}

	protected void handleOnSelectPrjList(ChangeEvent event) {
		clearAll();
		hideAll();
		vegaUtils.putToPrivateSate("CurrentDigestId", getProjectId());//need to be done in order to save current digest id, so it will be consistent in all tabs
		onProjectsLoadCallback.run();
		initAdminTabsOnPrjChange();
		adSenseWidget.setIsUserOrForumMode(false,  getProjectName(), getProjectId());
		vegaUtils.adjustHeight();
		
	}
	
	protected void clearAll(){
		if(digestAdminGeneralWidget != null){
			digestAdminGeneralWidget.clearAll();
		}
		if(digestAdminParticipantWidget != null){
			digestAdminParticipantWidget.clearAll();
		}
		vegaUtils.adjustHeight();
		
	}
	protected void hideAll(){
//		int tabsCount = adminSettingsTabPnl.getWidgetCount();
//		for(int i = 0; i < tabsCount; i++){
//			Widget tab = adminSettingsTabPnl.getWidget(i);
//			tab.setVisible(false);
//		}
		
	}
	
	
	public void digestAlert(IllegalArgumentException e) {
		vegaUtils.alert(e.getMessage());
	}

	protected String getProjectId() {
		String projectId = projectSelectWidget.getPrjList().getValue(projectSelectWidget.getPrjList().getSelectedIndex() > -1 ? projectSelectWidget.getPrjList().getSelectedIndex() : 0);
		return projectId;
	}
	
	protected String getProjectName() {
		String projectName = projectSelectWidget.getPrjList().getItemText(projectSelectWidget.getPrjList().getSelectedIndex() > -1 ? projectSelectWidget.getPrjList().getSelectedIndex() : 0);
		return projectName;
	}

	public Runnable getRunOnTabSelect() {
		return runOnTabSelect;
	}
	
	private void initImageHandler(){
	}
	
	@Override
	public String getName(){
		return "admin";
	}
	
}
