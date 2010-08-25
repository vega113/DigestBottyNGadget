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
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
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
	VegaUtils utils;

	private static ProjectSelectWidgetUiBinder uiBinder = GWT
	.create(ProjectSelectWidgetUiBinder.class);

	interface ProjectSelectWidgetUiBinder extends
	UiBinder<Widget, ProjectSelectWidget> {
	}


	public ProjectSelectWidget(final DigestMessages messages, final DigestConstants constants,
			final GlobalResources resources, final DigestService digestService, final Runnable onProjectsRetrCallback, final VegaUtils utils ) {
		initWidget(uiBinder.createAndBindUi(this));

		resources.globalCSS().ensureInjected();
		this.digestService = digestService;
		this.messages = messages;
		this.constants = constants;
		this.resources = resources;
		this.utils = utils;

		final String userId = utils.retrUserId();
		try {
			
			String msg = messages.loadingForumsListMsg(userId);
			Log.debug(msg);
			utils.showStaticMessage(msg);
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
							utils.dismissStaticMessage();
							//								reportPanel.add(new HTML("User " + userId + " doesn't have any digests"));
						}else{
							prjList.clear();
							Set<String> keys = resultJson.keySet();
							for(String key : keys){
								String item = resultJson.get(key).isString().stringValue();
								if(item.length() > 57){
									item = item.substring(60) + "...";
								}
								if(item == null || "".equals(item)){
									item = key;
								}
								prjList.addItem(item,key);
							}
							// Create a callback to be called when the visualization API
							// has been loaded.
							String digestId = utils.retrFromPrivateSate("CurrentDigestId");
							if(digestId != null && !"".equals(digestId)){
								int size = prjList.getItemCount();
								for(int i = 0; i < size; i++){
									if(prjList.getValue(i).equals(digestId)){
										prjList.setSelectedIndex(i);
									}
								}
							}
							utils.dismissStaticMessage();
							onProjectsRetrCallback.run();
						}
					}else{
						if(resultJsonValue.isString() != null){
							try{
								JSONObject tryjson = JSONParser.parse(resultJsonValue.isString().stringValue()).isObject();
							}catch (Exception e) {
								Log.error("after try to parse to obj", e);
								utils.alert(e.getMessage());
							}
						}
					}
				}
			});
		} catch (RequestException e) {
			Log.error("", e);
			utils.alert(e.getMessage());
		}

	}


	public ListBox getPrjList() {
		return prjList;
	}


	public void setPrjList(ListBox prjList) {
		this.prjList = prjList;
	}
	
	@UiHandler(value="prjList")
	public void onPrjIdSelectionChange(ChangeEvent event){
		String currId = prjList.getValue(prjList.getSelectedIndex());
		utils.putToPrivateSate("CurrentDigestId", currId);
	}
}
