package com.aggfi.digest.client.ui;

import java.util.Set;

import com.aggfi.digest.client.constants.SimpleConstants;
import com.aggfi.digest.client.constants.SimpleMessages;
import com.aggfi.digest.client.resources.GlobalResources;
import com.aggfi.digest.client.service.IDigestService;
import com.aggfi.digest.client.utils.IDigestUtils;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONObject;
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

	IDigestService digestService;
	SimpleMessages messages;
	SimpleConstants constants;
	GlobalResources resources;
	IDigestUtils digestUtils;

	private static ProjectSelectWidgetUiBinder uiBinder = GWT
	.create(ProjectSelectWidgetUiBinder.class);

	interface ProjectSelectWidgetUiBinder extends
	UiBinder<Widget, ProjectSelectWidget> {
	}


	public ProjectSelectWidget(final SimpleMessages messages, final SimpleConstants constants,
			final GlobalResources resources, final IDigestService digestService, final IDigestUtils digestUtils, final Runnable onProjectsRetrCallback ) {
		initWidget(uiBinder.createAndBindUi(this));

		resources.globalCSS().ensureInjected();
		this.digestService = digestService;
		this.messages = messages;
		this.constants = constants;
		this.resources = resources;
		this.digestUtils = digestUtils;

		final String userId = digestUtils.retrUserId();
		DeferredCommand.addCommand(new Command() {

			@Override
			public void execute() {
				
				
			}});
		try {
			digestService.retrPrjectsPerUserId(userId, new AsyncCallback<JSONValue>() {

				@Override
				public void onFailure(Throwable caught) {
					Log.error("", caught);
				}

				@Override
				public void onSuccess(JSONValue resultJsonValue) {
					JSONObject resultJson = (JSONObject)resultJsonValue;
					if(resultJson.containsKey("none")){
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
