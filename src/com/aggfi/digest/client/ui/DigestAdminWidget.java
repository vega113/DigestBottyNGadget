package com.aggfi.digest.client.ui;

import java.util.Set;

import com.aggfi.digest.client.constants.SimpleConstants;
import com.aggfi.digest.client.constants.SimpleMessages;
import com.aggfi.digest.client.resources.GlobalResources;
import com.aggfi.digest.client.service.IDigestService;
import com.aggfi.digest.client.utils.IDigestUtils;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class DigestAdminWidget extends Composite{
	
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
	ProjectSelectWidget projectSelectWidget;
	ListBox prjList = null;
	
	private RemoveHandler removeDefaultParticipantHandler = null;
	private RemoveHandler removeDefaultTagHandler = null;
	private RemoveHandler removeAutoTagHandler = null;

	private static DigestAdminWidgetUiBinder uiBinder = GWT
			.create(DigestAdminWidgetUiBinder.class);
	protected Runnable onProjectsLoadCallback;

	interface DigestAdminWidgetUiBinder extends
			UiBinder<Widget, DigestAdminWidget> {
	}

	@Inject
	public DigestAdminWidget(final SimpleMessages messages, final SimpleConstants constants, final GlobalResources resources, final IDigestService digestService, final IDigestUtils digestUtils) {
		initWidget(uiBinder.createAndBindUi(this));
		
		
		//-------remove default participant
		removeDefaultParticipantHandler = new RemoveHandler(messages, defaultTagsPanel) {

			@Override
			public void onRemove(final AddRemDefLabel widget) {
				this.setWidget(widget); // must set the widget for use of nested callback 
				final String participantId = widget.getFirstLblVal();
				widget.setFirstLblKey(messages.removingMsg(participantId));
				widget.getFirstValLbl().setVisible(false);
				String projectId = prjList.getValue(prjList.getSelectedIndex());
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
				String projectId = prjList.getValue(prjList.getSelectedIndex());
				try {
					digestService.removeDefaultTag(projectId, tag, getAfterRemovalAsyncCallback());
				} catch (RequestException e) {
					Log.error("", e);
				}
			}
		};
		//end remove default tag
		
		//-------remove auto tag
		removeAutoTagHandler = new RemoveHandler(messages, autoTagsPanel) {

			@Override
			public void onRemove(final AddRemDefLabel widget) {
				this.setWidget(widget); // must set the widget for use of nested callback 
				final String tag = widget.getFirstLblVal();
				widget.setFirstLblKey(messages.removingMsg(tag));
				widget.getFirstValLbl().setVisible(false);
				String projectId = prjList.getValue(prjList.getSelectedIndex());
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
				String projectId = prjList.getValue(prjList.getSelectedIndex());
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
								autoTagsPanel.add(new AddRemDefLabel(removeAutoTagHandler, "Tag", tag, "Regex", regex)); 
							}
						}
						
						@Override
						public void onFailure(Throwable caught) {
							Log.error("", caught);
							
						}
					});
				} catch (RequestException e) {
					Log.error("", e);
				}
			}
		};
		this.projectSelectWidget = new ProjectSelectWidget(messages, constants, resources, digestService, digestUtils, onProjectsLoadCallback);
		prjListContainer.add(projectSelectWidget);
		prjList = this.projectSelectWidget.getPrjList();
		
		prjList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				handleOnSelectPrjList(event);
			}
		});
		
//		defaultParticipantsPanel.add(new AddRemDefLabel(removeDefaultParticipantHandler, null, "vega113@googlewave.com", null, null));
	}

	protected void handleOnSelectPrjList(ChangeEvent event) {
		clearAll();
		onProjectsLoadCallback.run();
		
	}
	
	protected void clearAll(){
		defaultParticipantsPanel.clear();
		defaultTagsPanel.clear();
		autoTagsPanel.clear();
	}

}
