package com.aggfi.digest.client.ui;

import java.util.Set;

import com.aggfi.digest.client.constants.DigestConstants;
import com.aggfi.digest.client.constants.DigestMessages;
import com.aggfi.digest.client.resources.GlobalResources;
import com.aggfi.digest.client.service.DigestService;
import com.vegalabs.general.client.utils.VegaUtils;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class DigestAdminGeneralWidget extends Composite implements RunnableOnTabSelect {
	
	SimplePanel prjListContainer;
	
	@UiField
	CheckBox isAtomFeedPublicCheckBox;
	@UiField
	Button isAtomFeedPublicUpdateBtn;
	@UiField
	Button addSecurePostGadgetBtn;
	@UiField
	Button isSocialBtnsUpdateBtn;
	
	@UiField
	CheckBox isDiggEnabledCheckBox;
	@UiField
	CheckBox isBuzzEnabledCheckBox;
	@UiField
	CheckBox isTweetEnabledCheckBox;
	@UiField
	CheckBox isFaceEnabledCheckBox;
	ProjectSelectWidget projectSelectWidget;
	

	private static DigestAdminWidgetUiBinder uiBinder = GWT
			.create(DigestAdminWidgetUiBinder.class);
	protected Runnable onProjectsLoadCallback;

	interface DigestAdminWidgetUiBinder extends
			UiBinder<Widget, DigestAdminGeneralWidget> {
	}
	
	DigestService digestService;
	DigestMessages messages;
	DigestConstants constants;
	GlobalResources resources;
	private VegaUtils vegaUtils;
	
	public Runnable runOnTabSelect;
	
	AsyncCallback<JSONValue> digestAdminGeneralCallback = new AsyncCallback<JSONValue>() {

		@Override
		public void onSuccess(JSONValue result) {
			boolean isDiggEnabled = result.isObject().get("isDiggBtnEnabled").isBoolean().booleanValue();
			isDiggEnabledCheckBox.setValue(isDiggEnabled);
			boolean isBuzzEnabled = result.isObject().get("isBuzzBtnEnabled").isBoolean().booleanValue();
			isBuzzEnabledCheckBox.setValue(isBuzzEnabled);
			boolean isTweetEnabled = result.isObject().get("isTweetBtnEnabled").isBoolean().booleanValue();
			isTweetEnabledCheckBox.setValue(isTweetEnabled);
			boolean isFaceEnabled = result.isObject().get("isFaceBtnEnabled").isBoolean().booleanValue();
			isFaceEnabledCheckBox.setValue(isFaceEnabled);
			
		}

		@Override
		public void onFailure(Throwable caught) {
			vegaUtils.adjustHeight();
		}
	};
	
	@Inject
	public DigestAdminGeneralWidget(final DigestMessages messages, final DigestConstants constants, final GlobalResources resources, final DigestService digestService, final VegaUtils vegaUtils) {
		initWidget(uiBinder.createAndBindUi(this));
		hideAll();
		resources.globalCSS().ensureInjected();
		this.digestService = digestService;
		this.messages = messages;
		this.constants = constants;
		this.resources = resources;
		this.vegaUtils = vegaUtils;
		initImageHandler();






		runOnTabSelect = new Runnable() {

			@Override
			public void run() {
				initAdminWidget();
				vegaUtils.reportPageview("/adminTab/general");
				vegaUtils.adjustHeight();
			}
		};

	}

	private void initAdminWidget() {
		clearAll();
	}

	
	protected void clearAll(){
		isDiggEnabledCheckBox.setValue(false);
		isBuzzEnabledCheckBox.setValue(false);
		isTweetEnabledCheckBox.setValue(false);
		isFaceEnabledCheckBox.setValue(false);
	}
	protected void hideAll(){
	}
	
	
	@UiHandler("isAtomFeedPublicUpdateBtn")
	protected void atomFeedPublicUpdate(ClickEvent event){
		if(getProjectId().equals("")){
			vegaUtils.alert(constants.noForumSelectedWarning());
			return;
		}
		boolean isMakeAtomFeedPublic = isAtomFeedPublicCheckBox.getValue();
		try{
			try {
				String isPublicStr = isMakeAtomFeedPublic ? constants.publicStr() : constants.privateStr();
				vegaUtils.showStaticMessage(messages.sentRequest2UpdateAtomFeedPublic(isPublicStr));
				digestService.updateAtomFeedPublic(getProjectId(), isMakeAtomFeedPublic, new AsyncCallback<JSONValue>() {

					@Override
					public void onSuccess(JSONValue result) {
						vegaUtils.dismissStaticMessage();
						vegaUtils.showSuccessMessage(constants.successStr(), 3);
						Log.info(result.toString());
					}

					@Override
					public void onFailure(Throwable caught) {
						vegaUtils.dismissStaticMessage();
						vegaUtils.alert(caught.getMessage());

					}
				});
			} catch (RequestException e) {
				Log.error("", e);
			}
		}catch(IllegalArgumentException e){
			digestAlert(e);
		}
		try{
			vegaUtils.reportEvent("/admin/update","atomFeedPublicUpdate", getProjectId(), 1);
		}catch (Exception e) {
			Log.error("", e);
		}
	}
	
	@UiHandler("isSocialBtnsUpdateBtn")
	protected void socialBtnsUpdate(ClickEvent event){
		if(getProjectId().equals("")){
			vegaUtils.alert(constants.noForumSelectedWarning());
			return;
		}
		Boolean isDiggEnabled = isDiggEnabledCheckBox.getValue();
		Boolean isBuzzEnabled = isBuzzEnabledCheckBox.getValue();
		Boolean isTweetEnabled = isTweetEnabledCheckBox.getValue();
		Boolean isFaceEnabled = isFaceEnabledCheckBox.getValue();
		
		
		try{
			try {
				vegaUtils.showStaticMessage(messages.sentRequest2UpdateSocialBtns(isDiggEnabled.toString(),isBuzzEnabled.toString(),isTweetEnabled.toString(),isFaceEnabled.toString()));
				digestService.updateSocialBtnsSettings(getProjectId(), isDiggEnabled, isBuzzEnabled, isTweetEnabled, isFaceEnabled, new AsyncCallback<JSONValue>() {

					@Override
					public void onSuccess(JSONValue result) {
						vegaUtils.dismissStaticMessage();
						vegaUtils.showSuccessMessage(constants.successStr(), 3);
						Log.info(result.toString());
					}

					@Override
					public void onFailure(Throwable caught) {
						vegaUtils.dismissStaticMessage();
						vegaUtils.alert(caught.getMessage());

					}
				});
			} catch (RequestException e) {
				vegaUtils.dismissStaticMessage();
				Log.error("", e);
			}
		}catch(IllegalArgumentException e){
			digestAlert(e);
		}
		try{
			vegaUtils.reportEvent("/admin/update","atomFeedPublicUpdate", getProjectId(), 1);
		}catch (Exception e) {
			Log.error("", e);
		}
	}
	
	
	
	
	@UiHandler("addSecurePostGadgetBtn")
	protected void addSecurePostGadget(ClickEvent event){
		if(getProjectId().equals("")){
			vegaUtils.alert(constants.noForumSelectedWarning());
			return;
		}
		String projectId = getProjectId();
		String userId = vegaUtils.retrUserId();
		String msg = messages.sentRequest2Add1(constants.secureGadgetStr());
		vegaUtils.showStaticMessage(msg);
		try {
			digestService.addSecurePostGadget(projectId, userId, new AsyncCallback<JSONValue>() {
				
				@Override
				public void onSuccess(JSONValue result) {
					vegaUtils.dismissStaticMessage();
					vegaUtils.showSuccessMessage(constants.successStr(), 3);
					Log.debug(result.toString());
				}
				
				@Override
				public void onFailure(Throwable caught) {
					vegaUtils.dismissAllStaticMessages();
					vegaUtils.alert(caught.getMessage());
					Log.error("", caught);
				}
			});
		} catch (RequestException e) {
		}
	}
	
	
	
	private void initJsonMapModule(JSONValue result,String jsonFieldName, ComplexPanel panel,RemoveHandler removeHandler) {
		HorizontalPanel pat = new HorizontalPanel();
		panel.add(pat);
		JSONObject jsonMap = result.isObject().get(jsonFieldName).isObject();
		Set<String> keys = jsonMap.keySet();
		int i = 0;
		for(String key : keys){
			if(i != 0 && i % 2 == 0){
				pat = new HorizontalPanel();
				panel.add(pat);
			}
			String value = jsonMap.get(key).isString().stringValue();
			Composite c = new AddRemDefLabel(removeHandler, constants.tagStr(), key, constants.regexStr(), value);
			pat.add(c);
			if(i == keys.size() -1 && i % 2 == 1){
				panel.add(pat);
			}
			i++;
		}
		if(keys.size() > 0){
			panel.getParent().setVisible(true);
			panel.setVisible(true);
		}
		vegaUtils.adjustHeight();
	}
	
	private void initJsonArrayModule(JSONValue result, String jsonFieldName, ComplexPanel panel, RemoveHandler removeHandler) {
		HorizontalPanel p = new HorizontalPanel();
		panel.add(p);
		JSONArray jsonArray = result.isObject().get(jsonFieldName).isArray();
		for(int i = 0; i< jsonArray.size(); i++){
			if(i != 0 && i % 2 == 0){
				p = new HorizontalPanel();
				panel.add(p);
			}
			String jsonStrValue = jsonArray.get(i).isString().stringValue();
			Composite c = new AddRemDefLabel(removeHandler, null, jsonStrValue, null, null);
			p.add(c);
			if(i == jsonArray.size() -1 && i % 2 == 1){
				panel.add(p);
			}
		}
		if(jsonArray.size() > 0){
			panel.getParent().setVisible(true);
			panel.setVisible(true);
		}
		vegaUtils.adjustHeight();
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
	
	@UiField
	Image img9;
	@UiField
	Image img10;
	@UiField
	Image img11;
	private void initImageHandler(){
		MouseDownHandler mouseDownHandler = new DigestMouseDownHandler(vegaUtils);
		Image[] images = {img9,img10,img11};
		for(Image image : images){
			image.addMouseDownHandler(mouseDownHandler);
		}
	}
	
	@Override
	public String getName(){
		return "admin";
	}

	public AsyncCallback<JSONValue> getDigestAdminGeneralCallback() {
		return digestAdminGeneralCallback;
	}

	public ProjectSelectWidget getProjectSelectWidget() {
		return projectSelectWidget;
	}

	public void setProjectSelectWidget(ProjectSelectWidget projectSelectWidget) {
		this.projectSelectWidget = projectSelectWidget;
	}

	public Runnable getOnProjectsLoadCallback() {
		return onProjectsLoadCallback;
	}

	public void setOnProjectsLoadCallback(Runnable onProjectsLoadCallback) {
		this.onProjectsLoadCallback = onProjectsLoadCallback;
	}
	
}
