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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

public class ProjectSelectWidget extends Composite {
	@UiField
	ListBox prjList;
	@UiField
	CaptionPanel outerCptnPnl;
	
	@UiField
	HorizontalPanel refresBtnhPnl;
	private PushButton refreshBtn;
	
	@UiField
	HorizontalPanel goBtnhPnl;
	private Button goBtn;
	

	DigestService digestService;
	DigestMessages messages;
	DigestConstants constants;
	GlobalResources resources;
	VegaUtils utils;
	
	String digestWaveId = null;

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
		
		prjList.setWidth("380px");
		
		refreshBtn = new PushButton(new Image(resources.refresh()));
		//refreshBtn.setHeight("21px");
		refreshBtn.setTitle(constants.refreshPrjsList());
		refresBtnhPnl.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		refresBtnhPnl.add(refreshBtn);
		refreshBtn.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				refrshPrjList(messages, digestService, onProjectsRetrCallback, utils);
			}
		});
		
		
		goBtn = new Button("<span style='color: #105eb2; font-weight: bold;'>" + constants.goStr() + "</span>");
		//goBtn.setHeight("21px");
		goBtn.setTitle(constants.goToWaveStr());
		goBtnhPnl.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		goBtnhPnl.add(goBtn);
		goBtn.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				String digestId = utils.retrFromPrivateSate("CurrentDigestId");
				String digestWaveId = utils.retrFromPrivateSate("digestWaveId#" +digestId);
				String navigateTo = "#restored:wave:" + digestWaveId;
				utils.requestNavigateTo(navigateTo, null);
			}
		});

		refrshPrjList(messages, digestService, onProjectsRetrCallback, utils);

	}


	private void refrshPrjList(final DigestMessages messages,
			final DigestService digestService,
			final Runnable onProjectsRetrCallback, final VegaUtils utils) {
		final String userId = utils.retrUserId();
		try {
			
			String msg = messages.loadingForumsListMsg(userId);
			Log.debug(msg);
			utils.showStaticMessage(msg);
			digestService.retrPrjectsPerUserId(userId, new AsyncCallback<JSONValue>() {

				@Override
				public void onFailure(Throwable caught) {
					Log.error("", caught);
					utils.dismissAllStaticMessages();
					utils.alert(caught.getMessage());
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
								
								String prjName = resultJson.get(key).isObject().get("prjName").isString().stringValue();
								String digestWaveId = resultJson.get(key).isObject().get("digestWaveId").isString().stringValue();
								
								utils.putToPrivateSate("digestWaveId#" + key , digestWaveId);
								
								if(prjName.length() > 57){
									prjName = prjName.substring(60) + "...";
								}
								if(prjName == null || "".equals(prjName)){
									prjName = key;
								}
								prjList.addItem(prjName,key);
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
			Log.error("Error message: " + e.getMessage() + ".", e);
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


	public CaptionPanel getOuterCptnPnl() {
		return outerCptnPnl;
	}
}
