package com.aggfi.digest.client.ui;

import java.util.Set;

import com.aggfi.digest.client.constants.DigestConstants;
import com.aggfi.digest.client.constants.DigestMessages;
import com.aggfi.digest.client.resources.GlobalResources;
import com.aggfi.digest.client.service.DigestService;
import com.aggfi.digest.client.utils.DigestUtils;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class ProjectSelectWidget extends Composite {
	@UiField
	ListBox prjList;

	DigestService digestService;
	DigestMessages messages;
	DigestConstants constants;
	GlobalResources resources;
	DigestUtils digestUtils;

	private static ProjectSelectWidgetUiBinder uiBinder = GWT
	.create(ProjectSelectWidgetUiBinder.class);

	interface ProjectSelectWidgetUiBinder extends
	UiBinder<Widget, ProjectSelectWidget> {
	}


	public ProjectSelectWidget(final DigestMessages messages, final DigestConstants constants,
			final GlobalResources resources, final DigestService digestService, final Runnable onProjectsRetrCallback ) {
		initWidget(uiBinder.createAndBindUi(this));

		resources.globalCSS().ensureInjected();
		this.digestService = digestService;
		this.messages = messages;
		this.constants = constants;
		this.resources = resources;
		this.digestUtils = DigestUtils.getInstance();

		final String userId = digestUtils.retrUserId();
		try {
			digestService.retrPrjectsPerUserId(userId, new AsyncCallback<JSONValue>() {

				@Override
				public void onFailure(Throwable caught) {
					Log.error("", caught);
				}

				@Override
				public void onSuccess(JSONValue resultJsonValue) {
					Log.debug("ProjectSelectWidget.onSuccess: " + resultJsonValue.toString());
					if(resultJsonValue.isObject() != null){
						JSONObject resultJson = resultJsonValue.isObject();
						if(resultJson.containsKey("none")){
							prjList.clear();
							Log.debug("no projects are found in response" );
							prjList.addItem("none","");
							prjList.setEnabled(false);
							//								reportPanel.add(new HTML("User " + userId + " doesn't have any digests"));
						}else{
							prjList.clear();
							Set<String> keys = resultJson.keySet();
							for(String key : keys){
								prjList.addItem(key + " - " + resultJson.get(key).isString().stringValue(),key);
							}
							// Create a callback to be called when the visualization API
							// has been loaded.
							onProjectsRetrCallback.run();
						}
					}else{
						Log.debug("resultJsonValue is not JSONObject" );
						if(resultJsonValue.isString() != null){
							Log.debug("resultJsonValue is a JSONString" );
							try{
								JSONObject tryjson = JSONParser.parse(resultJsonValue.isString().stringValue()).isObject();
								if(tryjson != null){
									Log.info("sucess in making object from json");
								}
							}catch (Exception e) {
								Log.error("after try to parse to obj", e);
							}
						}
					}
				}
					
			});
		} catch (RequestException e) {
			Log.error("", e);
		}

	}


	public ListBox getPrjList() {
		return prjList;
	}


	public void setPrjList(ListBox prjList) {
		this.prjList = prjList;
	}
}
