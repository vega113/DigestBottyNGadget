package com.aggfi.digest.client.ui;

import java.util.Set;

import com.aggfi.digest.client.constants.DigestConstants;
import com.aggfi.digest.client.constants.DigestMessages;
import com.aggfi.digest.client.resources.GlobalResources;
import com.aggfi.digest.client.service.DigestService;
import com.aggfi.digest.client.utils.DigestUtils;
import com.aggfi.digest.shared.FieldVerifier;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class DigestAdminWidget extends Composite implements RunnableOnTabSelect {
	
	@UiField
	TextBox addDefaultParticipantBox;
	@UiField
	Button addDefaultParticipantBtn;
	@UiField
	Button addParticipantWavesBtn;
	@UiField
	TextBox addParticipantWavesBox;
	@UiField
	TextBox addParticipanTagtWavesBox;
	@UiField
	VerticalPanel defaultParticipantsPanel;
	@UiField
	VerticalPanel defaultTagsPanel;
	@UiField
	CaptionPanel defaultTagsCaptPnl;
	@UiField
	VerticalPanel autoTagsPanel;
	@UiField
	SimplePanel prjListContainer;
	@UiField
	TextBox addManagerBox;
	@UiField
	VerticalPanel managersPanel;
	@UiField
	VerticalPanel participantWavesPanel;
	@UiField
	TextBox addDefaultTagBox;
	@UiField
	Button addDefaultTagBtn;
	
	@UiField
	TextBox addAutoTagNameBox;
	@UiField
	TextBox addAutoTagValBox;
	@UiField
	Button addAutoTagBtn;
	@UiField
	CheckBox syncAutoTagCheckBox;
	
	@UiField
	CaptionPanel addParticipantWaveCaption;//FIXME remove
	
	ProjectSelectWidget projectSelectWidget;
	
	private RemoveHandler removeDefaultParticipantHandler = null;
	private RemoveHandler removeDefaultTagHandler = null;
	private RemoveHandler removeAutoTagHandler = null;
	private RemoveHandler removeManagerHandler = null;

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
	DigestUtils digestUtils = null;
	public Runnable runOnTabSelect;
	
	@Inject
	public DigestAdminWidget(final DigestMessages messages, final DigestConstants constants, final GlobalResources resources, final DigestService digestService) {
		initWidget(uiBinder.createAndBindUi(this));
		initImageHandler();
		hideAll();
		defaultTagsCaptPnl.setVisible(false);//TODO remove the caption panel entirely later
		resources.globalCSS().ensureInjected();
		this.digestService = digestService;
		this.messages = messages;
		this.constants = constants;
		this.resources = resources;
		this.digestUtils = DigestUtils.getInstance();


		onProjectsLoadCallback = new Runnable() { //will be run after projectIds list is loaded

			@Override
			public void run() {
				Log.debug("DigestAdminWidget::DigestReportWidget Running");
				String projectId = getProjectId();
				try {
					clearAll();
					hideAll();
					String msg = messages.loadingForumsMsg(constants.adminTabStr(), getProjectName());
					Log.debug(msg);
					digestUtils.showStaticMessage(msg);
					digestService.retrAdminConfig(projectId, new AsyncCallback<JSONValue>() {

						@Override
						public void onSuccess(JSONValue result) {
							initJsonArrayModule(result,"defaultParticipants",defaultParticipantsPanel,removeDefaultParticipantHandler);
							initJsonArrayModule(result,"defaultTags",defaultTagsPanel,removeDefaultTagHandler);
							initJsonMapModule(result,"autoTagRegexMap",autoTagsPanel,removeAutoTagHandler);
							initJsonArrayModule(result,"managers",managersPanel,removeManagerHandler);
							Log.debug("dismissMessage");
							digestUtils.dismissStaticMessage();
						}

						@Override
						public void onFailure(Throwable caught) {
							Log.error("", caught);
							digestUtils.adjustHeight();
							digestUtils.dismissStaticMessage();
							digestUtils.alert(caught.getMessage());
						}
					});
				} catch (RequestException e) {
					digestUtils.dismissStaticMessage();
					digestUtils.alert(e.getMessage());
					Log.error("", e);
				}
			}
		};

		//-------remove default participant
		removeDefaultParticipantHandler = new RemoveHandler(messages, defaultTagsPanel,onProjectsLoadCallback) {

			@Override
			public void onRemove(final AddRemDefLabel widget) {
				this.setWidget(widget); // must set the widget for use of nested callback 
				final String participantId = widget.getFirstLblVal();
				widget.setFirstLblKey(messages.removingMsg(participantId));
				widget.getFirstValLbl().setVisible(false);
				String projectId = getProjectId();
				try {
					digestService.removeDefaultParticipant(projectId, participantId, getAfterRemovalAsyncCallback());
				} catch (RequestException e) {
					Log.error("", e);
				}
			}
		};
		//end remove default participant

		//-------remove default tag
		removeDefaultTagHandler = new RemoveHandler(messages, defaultTagsPanel,onProjectsLoadCallback) {

			@Override
			public void onRemove(final AddRemDefLabel widget) {
				this.setWidget(widget); // must set the widget for use of nested callback 
				final String tag = widget.getFirstLblVal();
				widget.setFirstLblKey(messages.removingMsg(tag));
				widget.getFirstValLbl().setVisible(false);
				String projectId = getProjectId();
				try {
					digestService.removeDefaultTag(projectId, tag, getAfterRemovalAsyncCallback());
				} catch (RequestException e) {
					Log.error("", e);
				}
			}
		};
		//end remove default tag

		//-------remove manager
		removeManagerHandler = new RemoveHandler(messages, managersPanel,onProjectsLoadCallback) {

			@Override
			public void onRemove(final AddRemDefLabel widget) {
				this.setWidget(widget); // must set the widget for use of nested callback 
				final String managerId = widget.getFirstLblVal();
				widget.setFirstLblKey(messages.removingMsg(managerId));
				widget.getFirstValLbl().setVisible(false);
				String projectId = getProjectId();
				try {
					digestService.removeDigestManager(projectId, managerId, getAfterRemovalAsyncCallback());
				} catch (RequestException e) {
					Log.error("", e);
				}
			}
		};
		//end remove manager

		//-------remove auto tag
		removeAutoTagHandler = new RemoveHandler(messages, autoTagsPanel,onProjectsLoadCallback) {

			@Override
			public void onRemove(final AddRemDefLabel widget) {
				this.setWidget(widget); // must set the widget for use of nested callback 
				final String tag = widget.getFirstLblVal();
				widget.setFirstLblKey(messages.removingMsg(tag));
				widget.getFirstValLbl().setVisible(false);
				String projectId = getProjectId();
				String sync = String.valueOf(syncAutoTagCheckBox.getValue());
				try {
					String msg = messages.sentRequest2Remove(constants.autoTagging(), tag) + " " + constants.syncStr() + ": " + sync;
					digestUtils.showStaticMessage(msg);
					digestService.removeAutoTag(projectId, tag,sync, getAfterRemovalAsyncCallback());
				} catch (RequestException e) {
					Log.error("", e);
				}
			}
		};
		//end remove auto tag

		runOnTabSelect = new Runnable() {

			@Override
			public void run() {
				initAdminWidget();
				DeferredCommand.addCommand(new Command() {

					@Override
					public void execute() {
						DigestUtils.getInstance().adjustHeight();
					}
				});
			}
		};

	}

	private void initAdminWidget() {
		clearAll();
		this.projectSelectWidget = new ProjectSelectWidget(messages, constants, resources, digestService, onProjectsLoadCallback);
		prjListContainer.clear();
		prjListContainer.add(projectSelectWidget);
		projectSelectWidget.getPrjList().addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				handleOnSelectPrjList(event);
			}
		});
	}

	protected void handleOnSelectPrjList(ChangeEvent event) {
		clearAll();
		hideAll();
		digestUtils.setCurrentDigestId(getProjectId());//need to be done in order to save current digest id, so it will be consistent in all tabs
		onProjectsLoadCallback.run();
		
	}
	
	protected void clearAll(){
		defaultParticipantsPanel.clear();
		defaultTagsPanel.clear();
		autoTagsPanel.clear();
		managersPanel.clear();
		participantWavesPanel.clear();
	}
	protected void hideAll(){
		defaultParticipantsPanel.getParent().setVisible(false);
		defaultTagsPanel.getParent().setVisible(false);
		autoTagsPanel.getParent().setVisible(false);
		managersPanel.getParent().setVisible(false);
		participantWavesPanel.getParent().setVisible(false);
	}
	
	/*
	 * used in the onSucess methods in callbacks below
	 */
	private void onAddSuccess(JSONValue result,String name1, String val1, String name2, String val2,ComplexPanel panel, TextBox textBox1,TextBox textBox2, RemoveHandler removeHandler){
		if(result != null){
			Composite c = new AddRemDefLabel(removeHandler, name1, val1, name2, val2);
			int widgetCount = panel.getWidgetCount();
			if(widgetCount % 2 == 1){
				HorizontalPanel p = (HorizontalPanel)panel.getWidget(widgetCount - 1);
				p.add(c);
			}else{
				HorizontalPanel p = new HorizontalPanel();
				p.add(c);
				panel.add(p);
			}
			textBox1.setText("");
			if(textBox2 != null){
				textBox2.setText("");
			}
			clearAll();
			onProjectsLoadCallback.run();
		}
	}
	
	@UiHandler("addManagergBtn")
	protected void addManagerBtnClick(ClickEvent event){
		if(getProjectId().equals("")){
			digestUtils.alert(constants.noForumSelectedWarning());
			return;
		}
		final String userId = addManagerBox.getText().trim();
		try{
			FieldVerifier.verifyProjectId(getProjectId(),messages,constants);
			FieldVerifier.verifyWaveId(userId, messages, constants.managerWaveIdFieldName());
			try {
				digestUtils.showStaticMessage(messages.sentRequest2Add(constants.digestManagers(), userId));
				digestService.addDigestManager(getProjectId(), userId, new AsyncCallback<JSONValue>() {
					
					@Override
					public void onSuccess(JSONValue result) {
						onAddSuccess(result,null,userId,null,null,managersPanel,addManagerBox,null,removeManagerHandler);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						digestUtils.alert(caught.getMessage());
						
					}
				});
			} catch (RequestException e) {
				e.printStackTrace();
			}
		}catch(IllegalArgumentException e){
			digestAlert(e);
		}
	}
	
	
	@UiHandler("addDefaultTagBtn")
	protected void addDefaultTagBtnClick(ClickEvent event){
		if(getProjectId().equals("")){
			digestUtils.alert(constants.noForumSelectedWarning());
			return;
		}
		final String tag = addDefaultTagBox.getText().trim();
		try{
			try {
				FieldVerifier.verifyProjectId(getProjectId(),messages,constants);
				digestUtils.showStaticMessage(messages.sentRequest2Add(constants.defaultTagsStr(), tag));
				digestService.addDefaultTag(getProjectId(), tag, new AsyncCallback<JSONValue>() {
					
					@Override
					public void onSuccess(JSONValue result) {
						onAddSuccess(result,null,tag,null,null,defaultTagsPanel,addDefaultTagBox,null,removeDefaultTagHandler);
						digestUtils.dismissStaticMessage();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						digestUtils.alert(caught.getMessage());
						
					}
				});
			} catch (RequestException e) {
				e.printStackTrace();
			}
		}catch(IllegalArgumentException e){
			digestAlert(e);
		}
	}
	
	@UiHandler("addDefaultParticipantBtn")
	protected void addDefaultParticipantBtnClick(ClickEvent event){
		if(getProjectId().equals("")){
			digestUtils.alert(constants.noForumSelectedWarning());
			return;
		}
		final String participantId = addDefaultParticipantBox.getText().trim();
		try{
			FieldVerifier.verifyProjectId(getProjectId(),messages,constants);
			FieldVerifier.verifyWaveId(participantId, messages, constants.defaultParticipantWaveIdFieldName());
			try {
				digestUtils.showStaticMessage(messages.sentRequest2Add(constants.defaultParticipantsStr(), participantId));
				digestService.addDefaultParticipant(getProjectId(), participantId, new AsyncCallback<JSONValue>() {
					
					@Override
					public void onSuccess(JSONValue result) {
						onAddSuccess(result,null,participantId,null,null,defaultParticipantsPanel,addDefaultParticipantBox,null,removeDefaultParticipantHandler);
						digestUtils.dismissStaticMessage();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						digestUtils.alert(caught.getMessage());
						
					}
				});
			} catch (RequestException e) {
				digestUtils.alert(e.getMessage());
			}
		}catch(IllegalArgumentException e){
			digestUtils.alert(e.getMessage());
		}
	}
	@UiHandler("addParticipantWavesBtn")
	protected void addParticipantWavesBtnClick(ClickEvent event){
		if(getProjectId().equals("")){
			digestUtils.alert(constants.noForumSelectedWarning());
			return;
		}
		final String participantId = addParticipantWavesBox.getText().trim();
		final String tagName = addParticipanTagtWavesBox.getText().trim();
		try{
			FieldVerifier.verifyProjectId(getProjectId(),messages,constants);
			FieldVerifier.verifyWaveId(participantId, messages, constants.participantWaveIdFieldName());
			try {
				digestUtils.showStaticMessage(messages.sentRequest2Add(constants.setUpParticipantWaves(), participantId));
				digestService.addWavesParticipant(getProjectId(), participantId, tagName,new AsyncCallback<JSONValue>() { 
					
					@Override
					public void onSuccess(JSONValue result) {
						addParticipantWavesBox.setText("");
						addParticipanTagtWavesBox.setText("");
						digestUtils.dismissStaticMessage();
						int wavesNum = 0;
						if(result != null && result.isNumber() != null){
							wavesNum = (int)result.isNumber().doubleValue();
							digestUtils.showSuccessMessage(messages.add2WavesSuccessMsg(participantId,wavesNum), 8);
						}
						
					}
					
					@Override
					public void onFailure(Throwable caught) {
						digestUtils.alert(caught.getMessage());
						
					}
				});
			} catch (RequestException e) {
				e.printStackTrace();
			}
		}catch(IllegalArgumentException e){
			digestAlert(e);
		}
	}
	
	@UiHandler("addAutoTagBtn")
	protected void addAutoTagClick(ClickEvent event){
		if(getProjectId().equals("")){
			digestUtils.alert(constants.noForumSelectedWarning());
			return;
		}
		String isSync = String.valueOf(syncAutoTagCheckBox.getValue());
		final String tag = addAutoTagNameBox.getText().trim();
		final String regex1;
		if("".equals(addAutoTagValBox.getText().trim())){
			regex1 = ".*";
		}else{
			regex1 = addAutoTagValBox.getText().trim();
		}
		final String regex = regex1;
		FieldVerifier.verifyProjectId(getProjectId(),messages,constants);
		try{
			try {
				String msg = messages.sentRequest2Add(constants.autoTagging(), tag  +" : " + regex) + " " + constants.syncStr() + ": " + isSync;
				digestUtils.showStaticMessage(msg);
				digestService.addAutoTag(getProjectId(), tag, regex,isSync, new AsyncCallback<JSONValue>() {
					
					@Override
					public void onSuccess(JSONValue result) {
						onAddSuccess(result,constants.tagStr(),tag,constants.regexStr(),regex,autoTagsPanel,addAutoTagNameBox,addAutoTagValBox,removeAutoTagHandler);
						digestUtils.dismissStaticMessage();
						String successMsg = "";
						if(result.isNumber() != null){
							int appliedCount = (int)result.isNumber().doubleValue();
							successMsg = messages.add2WavesSuccessMsg(tag,appliedCount);
						}else if(result.isString() != null){
							successMsg = messages.addSuccessMsg(tag);
						}
						digestUtils.showSuccessMessage(successMsg, 8);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						digestUtils.alert(caught.getMessage());
						
					}
				});
			} catch (RequestException e) {
				e.printStackTrace();
			}
		}catch(IllegalArgumentException e){
			digestAlert(e);
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
		digestUtils.adjustHeight();
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
		digestUtils.adjustHeight();
	}
	
	public void digestAlert(IllegalArgumentException e) {
		digestUtils.alert(e.getMessage());
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
	Image img1;
	@UiField
	Image img2;
	@UiField
	Image img3;
	@UiField
	Image img4;
	@UiField
	Image img5;
	@UiField
	Image img6;
	@UiField
	Image img7;
	@UiField
	Image img8;
	private void initImageHandler(){
		MouseDownHandler mouseDownHandler = new DigestMouseDownHandler();
		Image[] images = {img1,img2,img3,img4,img5,img6,img7,img8};
		for(Image image : images){
			image.addMouseDownHandler(mouseDownHandler);
		}
	}

}
