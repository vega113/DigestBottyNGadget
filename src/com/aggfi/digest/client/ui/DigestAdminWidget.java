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
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
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
	VerticalPanel defaultParticipantsPanel;
	@UiField
	VerticalPanel defaultTagsPanel;
	@UiField
	VerticalPanel autoTagsPanel;
	@UiField
	SimplePanel prjListContainer;
	@UiField
	TextBox addManagerBox;
	@UiField
	VerticalPanel managersPanel;
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
	
	ProjectSelectWidget projectSelectWidget;
	ListBox prjList = null;
	
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
		
		resources.globalCSS().ensureInjected();
		this.digestService = digestService;
		this.messages = messages;
		this.constants = constants;
		this.resources = resources;
		this.digestUtils = DigestUtils.getInstance();
		
		//-------remove default participant
		removeDefaultParticipantHandler = new RemoveHandler(messages, defaultTagsPanel) {

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
		removeDefaultTagHandler = new RemoveHandler(messages, defaultTagsPanel) {

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
		removeManagerHandler = new RemoveHandler(messages, managersPanel) {

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
		removeAutoTagHandler = new RemoveHandler(messages, autoTagsPanel) {

			@Override
			public void onRemove(final AddRemDefLabel widget) {
				this.setWidget(widget); // must set the widget for use of nested callback 
				final String tag = widget.getFirstLblVal();
				widget.setFirstLblKey(messages.removingMsg(tag));
				widget.getFirstValLbl().setVisible(false);
				String projectId = getProjectId();
				try {
					digestService.removeAutoTag(projectId, tag, getAfterRemovalAsyncCallback());
				} catch (RequestException e) {
					Log.error("", e);
				}
			}
		};
		//end remove auto tag
		
		onProjectsLoadCallback = new Runnable() { //will be run after projectIds list is loaded
			
			@Override
			public void run() {
				Log.debug("DigestAdminWidget::DigestReportWidget Running");
				prjList = projectSelectWidget.getPrjList();
				String projectId = getProjectId();
				try {
					digestService.retrAdminConfig(projectId, new AsyncCallback<JSONValue>() {
						
						@Override
						public void onSuccess(JSONValue result) {
							JSONArray defaultParticipants = result.isObject().get("defaultParticipants").isArray();
							for(int i = 0; i< defaultParticipants.size(); i++){
								String participantId = defaultParticipants.get(i).isString().stringValue();
								defaultParticipantsPanel.add(new AddRemDefLabel(removeDefaultParticipantHandler, null, participantId, null, null));
							}
							
							JSONArray defaultTags = result.isObject().get("defaultTags").isArray();
							for(int i = 0; i< defaultTags.size(); i++){
								String defaultTag = defaultTags.get(i).isString().stringValue();
								defaultTagsPanel.add(new AddRemDefLabel(removeDefaultTagHandler, null, defaultTag, null, null)); 
							}
							
							JSONObject autoTagRegexMap = result.isObject().get("autoTagRegexMap").isObject();
							Set<String> keys = autoTagRegexMap.keySet();
							for(String tag : keys){
								String regex = autoTagRegexMap.get(tag).isString().stringValue();
								autoTagsPanel.add(new AddRemDefLabel(removeAutoTagHandler, constants.tagStr(), tag, constants.regexStr(), regex)); 
							}
							
							JSONArray managers = result.isObject().get("managers").isArray();
							for(int i = 0; i< managers.size(); i++){
								String managerId = managers.get(i).isString().stringValue();
								managersPanel.add(new AddRemDefLabel(removeManagerHandler, null, managerId, null, null)); 
							}
							digestUtils.adjustHeight();
						}
						
						@Override
						public void onFailure(Throwable caught) {
							Log.error("", caught);
							digestUtils.adjustHeight();
						}
					});
				} catch (RequestException e) {
					Log.error("", e);
				}
			}
		};
		
		runOnTabSelect = new Runnable() {
			
			@Override
			public void run() {
				initAdminWidget();
				
			}
		};
		
	}

	private void initAdminWidget() {
		clearAll();
		this.projectSelectWidget = new ProjectSelectWidget(messages, constants, resources, digestService, onProjectsLoadCallback);
		prjListContainer.clear();
		prjListContainer.add(projectSelectWidget);
		prjList = this.projectSelectWidget.getPrjList();
		prjList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				handleOnSelectPrjList(event);
			}
		});
	}

	protected void handleOnSelectPrjList(ChangeEvent event) {
		clearAll();
		onProjectsLoadCallback.run();
		
	}
	
	protected void clearAll(){
		defaultParticipantsPanel.clear();
		defaultTagsPanel.clear();
		autoTagsPanel.clear();
		managersPanel.clear();
	}
	
	@UiHandler("addManagergBtn")
	protected void addManagerBtnClick(ClickEvent event){
		final String userId = addManagerBox.getText();
		try{
			FieldVerifier.verifyWaveId(userId, messages, constants.defaultParticipantWaveIdFieldName());
			try {
				digestService.addDigestManager(getProjectId(), userId, new AsyncCallback<JSONValue>() {
					
					@Override
					public void onSuccess(JSONValue result) {
						if(result != null && result.isString().stringValue().equals("true")){
							managersPanel.add(new AddRemDefLabel(removeManagerHandler, null, userId, null, null)); 
							addManagerBox.setText("");
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
	
	@UiHandler("addDefaultTagBtn")
	protected void addDefaultTagBtnClick(ClickEvent event){
		final String tag = addDefaultTagBox.getText();
		try{
			try {
				digestService.addDefaultTag(getProjectId(), tag, new AsyncCallback<JSONValue>() {
					
					@Override
					public void onSuccess(JSONValue result) {
						if(result != null && result.isString().stringValue().equals("true")){
							defaultTagsPanel.add(new AddRemDefLabel(removeDefaultTagHandler, null, tag, null, null)); 
							addDefaultTagBox.setText("");
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
	
	@UiHandler("addDefaultParticipantBtn")
	protected void addDefaultParticipantBtnClick(ClickEvent event){
		final String participantId = addDefaultParticipantBox.getText();
		try{
			FieldVerifier.verifyWaveId(participantId, messages, constants.defaultParticipantWaveIdFieldName());
			try {
				digestService.addDefaultParticipant(getProjectId(), participantId, new AsyncCallback<JSONValue>() {
					
					@Override
					public void onSuccess(JSONValue result) {
						if(result != null && result.isString().stringValue().equals("true")){
							defaultParticipantsPanel.add(new AddRemDefLabel(removeDefaultParticipantHandler, null, participantId, null, null)); 
							addDefaultParticipantBox.setText("");
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
		final String tag = addAutoTagNameBox.getText();
		final String regex = addAutoTagValBox.getText();
		try{
			//TODO verify correctness of regex?
			try {
				digestService.addAutoTag(getProjectId(), tag, regex, new AsyncCallback<JSONValue>() {
					
					@Override
					public void onSuccess(JSONValue result) {
						if(result != null && result.isString().stringValue().equals("true")){
							autoTagsPanel.add(new AddRemDefLabel(removeDefaultParticipantHandler, constants.tagStr(), tag, constants.regexStr(), regex)); 
							addAutoTagNameBox.setText("");
							addAutoTagValBox.setText("");
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

	public void digestAlert(IllegalArgumentException e) {
		digestUtils.alert(e.getMessage());
	}

	protected String getProjectId() {
		String projectId = prjList.getValue(prjList.getSelectedIndex() > -1 ? prjList.getSelectedIndex() : 0);
		return projectId;
	}

	public Runnable getRunOnTabSelect() {
		return runOnTabSelect;
	}

}
