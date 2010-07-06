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
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
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
				String projectId = getProjectId();
				try {
					digestService.retrAdminConfig(projectId, new AsyncCallback<JSONValue>() {
						
						@Override
						public void onSuccess(JSONValue result) {
							initJsonArrayModule(result,"defaultParticipants",defaultParticipantsPanel,removeDefaultParticipantHandler);
							initJsonArrayModule(result,"defaultTags",defaultTagsPanel,removeDefaultTagHandler);
							initJsonMapModule(result,"autoTagRegexMap",autoTagsPanel,removeAutoTagHandler);
							initJsonArrayModule(result,"managers",managersPanel,removeManagerHandler);
						}
						
						@Override
						public void onFailure(Throwable caught) {
							Log.error("", caught);
							digestUtils.adjustHeight();
							digestUtils.dismissMessage();
						}
					});
				} catch (RequestException e) {
					digestUtils.dismissMessage();
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
		projectSelectWidget.getPrjList().addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				handleOnSelectPrjList(event);
			}
		});
	}

	protected void handleOnSelectPrjList(ChangeEvent event) {
		clearAll();
		digestUtils.setCurrentDigestId(getProjectId());//need to be done in order to save current digest id, so it will be consistent in all tabs
		onProjectsLoadCallback.run();
		
	}
	
	protected void clearAll(){
		defaultParticipantsPanel.clear();
		defaultTagsPanel.clear();
		autoTagsPanel.clear();
		managersPanel.clear();
	}
	
	/*
	 * used in the onSucess methods in callbacks below
	 */
	private void onAddSuccess(JSONValue result,String name1, String val1, String name2, String val2,ComplexPanel panel, TextBox textBox1,TextBox textBox2, RemoveHandler removeHandler){
		if(result != null && result.isString().stringValue().equals("true")){
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
		final String userId = addManagerBox.getText().trim();
		try{
			FieldVerifier.verifyWaveId(userId, messages, constants.managerWaveIdFieldName());
			try {
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
		final String tag = addDefaultTagBox.getText().trim();
		try{
			try {
				digestService.addDefaultTag(getProjectId(), tag, new AsyncCallback<JSONValue>() {
					
					@Override
					public void onSuccess(JSONValue result) {
						onAddSuccess(result,null,tag,null,null,defaultTagsPanel,addDefaultTagBox,null,removeDefaultTagHandler);
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
		final String participantId = addDefaultParticipantBox.getText().trim();
		try{
			FieldVerifier.verifyWaveId(participantId, messages, constants.defaultParticipantWaveIdFieldName());
			try {
				digestService.addDefaultParticipant(getProjectId(), participantId, new AsyncCallback<JSONValue>() {
					
					@Override
					public void onSuccess(JSONValue result) {
						onAddSuccess(result,null,participantId,null,null,defaultParticipantsPanel,addDefaultParticipantBox,null,removeDefaultParticipantHandler);
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
		final String tag = addAutoTagNameBox.getText().trim();
		final String regex = addAutoTagValBox.getText().trim();
		try{
			try {
				digestService.addAutoTag(getProjectId(), tag, regex, new AsyncCallback<JSONValue>() {
					
					@Override
					public void onSuccess(JSONValue result) {
						onAddSuccess(result,constants.tagStr(),tag,constants.regexStr(),regex,autoTagsPanel,addAutoTagNameBox,addAutoTagValBox,removeAutoTagHandler);
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
				panel.add(pat);
				pat = new HorizontalPanel();
			}
			String value = jsonMap.get(key).isString().stringValue();
			Composite c = new AddRemDefLabel(removeHandler, constants.tagStr(), key, constants.regexStr(), value);
			pat.add(c);
			if(i == keys.size() -1 && i % 2 == 1){
				panel.add(pat);
			}
			i++;
		}
		digestUtils.adjustHeight();
	}
	
	private void initJsonArrayModule(JSONValue result, String jsonFieldName, ComplexPanel panel, RemoveHandler removeHandler) {
		HorizontalPanel p = new HorizontalPanel();
		panel.add(p);
		JSONArray jsonArray = result.isObject().get(jsonFieldName).isArray();
		for(int i = 0; i< jsonArray.size(); i++){
			if(i != 0 && i % 2 == 0){
				panel.add(p);
				p = new HorizontalPanel();
			}
			String jsonStrValue = jsonArray.get(i).isString().stringValue();
			Composite c = new AddRemDefLabel(removeHandler, null, jsonStrValue, null, null);
			p.add(c);
			if(i == jsonArray.size() -1 && i % 2 == 1){
				panel.add(p);
			}
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

	public Runnable getRunOnTabSelect() {
		return runOnTabSelect;
	}

}
