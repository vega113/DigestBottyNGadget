package com.aggfi.digest.client.ui;

import com.aggfi.digest.client.constants.ConstantsImpl;
import com.aggfi.digest.client.constants.DigestConstants;
import com.aggfi.digest.client.constants.DigestMessages;
import com.aggfi.digest.client.constants.MessagesImpl;
import com.aggfi.digest.client.resources.GlobalResources;
import com.aggfi.digest.client.service.DigestService;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.vegalabs.general.client.utils.VegaUtils;

public class DigestTrackerWidget extends Composite implements RunnableOnTabSelect{

	private static DigestTrackerWidgetUiBinder uiBinder = GWT
			.create(DigestTrackerWidgetUiBinder.class);

	interface DigestTrackerWidgetUiBinder extends
			UiBinder<Widget, DigestTrackerWidget> {
	}
	
	@UiField
	CheckBox enableViewsTrackingCheckBox;
	@UiField
	CheckBox syncViewsCheckBox;
	@UiField
	Button updateTrackViewsBtn;
	
	@UiField
	Button createViewsCounterInstallerBtn;
	@UiField
	CaptionPanel syncCptnPnl;
	
	
	DigestService digestService;
	DigestMessages messages;
	DigestConstants constants;
	MessagesImpl messagesAdSense;
	ConstantsImpl constantsAdSense;
	GlobalResources resources;
	private VegaUtils utils;
	protected Runnable onProjectsLoadCallback;
	private Runnable runOnTabSelect;

	@Inject
	public DigestTrackerWidget(final DigestMessages messages, final DigestConstants constants,final MessagesImpl messagesAdSense,
			final GlobalResources resources, final DigestService digestService, final VegaUtils utils) {
		initWidget(uiBinder.createAndBindUi(this));
		
		resources.globalCSS().ensureInjected();
		this.digestService = digestService;
		this.messagesAdSense = messagesAdSense;
		this.messages = messages;
		this.constants = constants;
		this.resources = resources;
		this.utils = utils;
		
		img2.setVisible(false);
		img4.setVisible(false);
		
		syncCptnPnl.setVisible(false);//FIXME - think how to enable sync option on views tracking auto enable
		
		runOnTabSelect = new Runnable() {
			
			@Override
			public void run() {
				utils.reportPageview("/trackerTab/");
			}
		};
		
		
		
		updateTrackViewsBtn.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				boolean isUpdateViewsTracking = enableViewsTrackingCheckBox.getValue();
				boolean isSync = syncViewsCheckBox.getValue();
				try {
					img2.setVisible(true);
					digestService.updateViewsTracking(getProjectId(), isUpdateViewsTracking, isSync, new AsyncCallback<JSONValue>() {
						
						@Override
						public void onSuccess(JSONValue result) {
							img2.setVisible(false);
							utils.showSuccessMessage(constants.successStr(), 3);
						}
						
						@Override
						public void onFailure(Throwable caught) {
							img2.setVisible(false);
							utils.alert(caught.getMessage());
							Log.error("", caught);
							
						}
					});
				} catch (RequestException e) {
					utils.dismissStaticMessage();
					Log.error("", e);
				}
			}
		});
		
		
		createViewsCounterInstallerBtn.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				img4.setVisible(true);
				try {
					AsyncCallback<JSONValue> asyncCallback = new AsyncCallback<JSONValue>() {
						
						@Override
						public void onSuccess(JSONValue result) {
							img4.setVisible(false);
						}
						
						@Override
						public void onFailure(Throwable caught) {
							img4.setVisible(false);
							utils.alert(caught.getMessage());
							Log.error("", caught);
						}
					}; 
					digestService.addAdSenseInstaller(utils.retrUserId(), asyncCallback );
				} catch (RequestException e) {
					Log.error("", e);
				}
			}
		});
		
		initImageHandler();
	}
	
	
	AsyncCallback<JSONValue> digestTrackerCallback =  new AsyncCallback<JSONValue>() {

		@Override
		public void onSuccess(JSONValue result) {
			clearAll();
			boolean isUpdateViewsTracking = result.isObject().get("isViewsTrackingEnabled").isBoolean().booleanValue();
			enableViewsTrackingCheckBox.setValue(isUpdateViewsTracking);
			utils.adjustHeight();
		}

		@Override
		public void onFailure(Throwable caught) {
			utils.adjustHeight();
		}
	};
	
	private void clearAll() {
		enableViewsTrackingCheckBox.setValue(false);
		syncViewsCheckBox.setValue(false);
		
	}

	@Override
	public Runnable getRunOnTabSelect() {
		return runOnTabSelect;
	}

	@Override
	public String getName() {
		return "tracker";
	}
	
	ProjectSelectWidget projectSelectWidget;
	protected String getProjectId() {
		String projectId = projectSelectWidget.getPrjList().getValue(projectSelectWidget.getPrjList().getSelectedIndex() > -1 ? projectSelectWidget.getPrjList().getSelectedIndex() : 0);
		return projectId;
	}
	
	public ProjectSelectWidget getProjectSelectWidget() {
		return projectSelectWidget;
	}

	public void setProjectSelectWidget(ProjectSelectWidget projectSelectWidget) {
		this.projectSelectWidget = projectSelectWidget;
	}
	
	
	@UiField
	Image img4;
	@UiField
	Image img3;
	@UiField
	Image img2;
	@UiField
	Image img1;
	@UiField
	Image img0;
	private void initImageHandler(){
		MouseDownHandler mouseDownHandler = new DigestMouseDownHandler(utils);
		Image[] images = {img0,img1,img2,img3,img4};
		for(Image image : images){
			image.addMouseDownHandler(mouseDownHandler);
		}
	}

	public AsyncCallback<JSONValue> getDigestTrackerCallback() {
		return digestTrackerCallback;
	}

}
