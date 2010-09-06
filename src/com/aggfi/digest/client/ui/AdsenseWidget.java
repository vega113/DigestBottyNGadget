package com.aggfi.digest.client.ui;

import com.aggfi.digest.client.constants.ConstantsImpl;
import com.aggfi.digest.client.constants.DigestConstants;
import com.aggfi.digest.client.constants.DigestMessages;
import com.aggfi.digest.client.constants.MessagesImpl;
import com.aggfi.digest.client.resources.GlobalResources;
import com.aggfi.digest.client.service.DigestService;
import com.aggfi.digest.shared.FieldVerifier;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.vegalabs.general.client.utils.VegaUtils;

public class AdsenseWidget extends Composite implements RunnableOnTabSelect{

	private static AdsenseWidgetUiBinder uiBinder = GWT
			.create(AdsenseWidgetUiBinder.class);

	interface AdsenseWidgetUiBinder extends UiBinder<Widget, AdsenseWidget> {
	}

	public AdsenseWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	
	
	@UiField
	protected VerticalPanel adsensePanel;
	@UiField
	TextArea adsenseCodeTxtArea;
	@UiField
	Button submitAdsenseCodeBtn;
	@UiField
	RadioButton radioBtnEditMode;
	@UiField
	RadioButton radioBtnViewMode;
	@UiField
	CaptionPanel adsenseTitle4Cptn;
	@UiField
	Button copyAdsenseBtn;
	@UiField
	CaptionPanel copyAdsenseCptnPanel;
	
	DigestService digestService;
	DigestMessages messages;
	DigestConstants constants;
	MessagesImpl messagesAdSense;
	ConstantsImpl constantsAdSense;
	GlobalResources resources;
	private VegaUtils utils;
	protected Runnable onProjectsLoadCallback;
	private Runnable runOnTabSelect;
	
	boolean isInViewMode = true;
	String defaultAdSenseAreaStyleName = "";
	
	private String adsenseTxt = null;
	private boolean isAdSenseUpdate4User;
	private String adsSenseCode4UserInCaseOfProjStr = "";
	
	String projectId = null;
	String projectName = null;
	String userId = null;

	@Inject
	public AdsenseWidget(final DigestMessages messages, final DigestConstants constants,final MessagesImpl messagesAdSense, final ConstantsImpl constantsAdSense,
				final GlobalResources resources, final DigestService digestService, final VegaUtils utils) {
		initWidget(uiBinder.createAndBindUi(this));
//		hideAll();
		resources.globalCSS().ensureInjected();
		this.digestService = digestService;
		this.messagesAdSense = messagesAdSense;
		this.constantsAdSense = constantsAdSense;
		this.messages = messages;
		this.constants = constants;
		this.resources = resources;
		this.utils = utils;
		defaultAdSenseAreaStyleName = adsenseCodeTxtArea.getStyleName();
		
		submitAdsenseCodeBtn.setEnabled(false);
		img1.setVisible(false);
		
		disableAdSenseCodeArea();
		radioBtnViewMode.setValue(true);
		copyAdsenseCptnPanel.setVisible(false);
		
		onProjectsLoadCallback = new Runnable() {
			
			@Override
			public void run() {
//				if(isInViewMode){
//					getAdSenseCode();
//				}
				radioBtnViewMode.setValue(true);
				Log.debug("DigestAdSenseWidget::DigestAdSenseWidget Running");
			}
		};
		
		runOnTabSelect = new Runnable() {
			
			@Override
			public void run() {
				adsenseCodeTxtArea.setText("");
				initAdSenseWidget();
				if(isPrjModeNull()){
					radioBtnEditMode.setEnabled(false);
				}
				utils.adjustHeight();
			}
		};
		
		
		
		
		radioBtnEditMode.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				adsenseTxt = adsenseCodeTxtArea.getText();
				enableAdSenseCodeArea();
				submitAdsenseCodeBtn.setEnabled(true);
			}
		});
		
		radioBtnViewMode.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				btnViewOnClick();
			}
		});
		
		submitAdsenseCodeBtn.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				adsenseTxt = adsenseCodeTxtArea.getText();
				FieldVerifier.verifyAdSenseCode(adsenseTxt);
				String userName = utils.retrUserName();
				userId = utils.retrUserId();
				String userThumbnailUrl = utils.retrUserThumbnailUrl();
				try {
					String userOrForumStr = isAdSenseUpdate4User ? constants.userStr() : constants.forumStr();
					String nameStr = isAdSenseUpdate4User ? userName : projectName;
					utils.showStaticMessage(messages.savingAdSense4Msg(userOrForumStr,nameStr ));
					digestService.addAdSenseCode(projectId, userId, adsenseTxt, userName, userThumbnailUrl, isAdSenseUpdate4User, new AsyncCallback<JSONValue>() {
						
						@Override
						public void onSuccess(JSONValue result) {
							utils.dismissStaticMessage();
//							utils.showSuccessMessage(constants.successStr(), 3); //XXX it shows the msg several times for some reason?
							btnViewOnClick();
							utils.reportEvent("/adsenseTab/", "submit", userId, 1);
							Log.info(result.toString());
						}
						
						@Override
						public void onFailure(Throwable caught) {
							utils.dismissStaticMessage();
							utils.alert(caught.getMessage());
							Log.error("caught", caught);
						}
					});
				} catch (RequestException e) {
					utils.dismissAllStaticMessages();
					Log.error("", e);
					utils.alert(e.getMessage());
				}
			}
		});
		
		copyAdsenseBtn.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				adsenseTxt = adsSenseCode4UserInCaseOfProjStr;
				if("".equals(adsenseTxt)){
					utils.alert(constants.personalAdsenseCodeIsEmpty()); //should not happen
					return;
				}
				FieldVerifier.verifyAdSenseCode(adsenseTxt);
				String userName = utils.retrUserName();
				String userThumbnailUrl = utils.retrUserThumbnailUrl();
				try {
					String userOrForumStr = isAdSenseUpdate4User ? constants.userStr() : constants.forumStr();
					String nameStr = isAdSenseUpdate4User ? userName : projectName;
					utils.showStaticMessage(messages.savingAdSense4Msg(userOrForumStr,nameStr ));
					digestService.addAdSenseCode(projectId, userId, adsenseTxt, userName, userThumbnailUrl, isAdSenseUpdate4User, new AsyncCallback<JSONValue>() {
						
						@Override
						public void onSuccess(JSONValue result) {
							adsenseCodeTxtArea.setText(adsenseTxt);
							utils.dismissStaticMessage();
							utils.showSuccessMessage(constants.successStr(), 3);
							btnViewOnClick();
							utils.reportEvent("/adsenseTab/", "submit", userId, 1);
							Log.info(result.toString());
						}
						
						@Override
						public void onFailure(Throwable caught) {
							utils.dismissStaticMessage();
							utils.alert(caught.getMessage());
							Log.error("caught", caught);
						}
					});
				} catch (RequestException e) {
					utils.dismissAllStaticMessages();
					Log.error("", e);
					utils.alert(e.getMessage());
				}
			}
		});
		
		initImageHandler();
		
		
	}



	public void getAdSenseCode() {
		final String userId = utils.retrUserId();
		String userName = utils.retrUserName();
		if(isPrjModeNull()){
			return;
		}
//		String msg = isAdSenseUpdate4User ? messages.loadingAdSenseCode4Msg(userName) : messages.loadingAdSenseCode4Msg(projectName);
		img1.setVisible(true);
		try {
			digestService.getAdSenseCode(projectId, userId, isAdSenseUpdate4User, new AsyncCallback<JSONValue>() {
				
				@Override
				public void onSuccess(JSONValue result) {
					img1.setVisible(false);
					adsenseTxt = "";
					try{
						adsenseTxt = result.isObject().get("adSenseCode").isString().stringValue();
					}catch(Exception e){}
					adsenseCodeTxtArea.setText(adsenseTxt);
					if(!isAdSenseUpdate4User){
						adsSenseCode4UserInCaseOfProjStr = "";
						try{
							adsSenseCode4UserInCaseOfProjStr = result.isObject().get("adSenseCode4User").isString().stringValue();
						}catch(Exception e){}
						if(!"".equals(adsSenseCode4UserInCaseOfProjStr) && "".equals(adsenseTxt)){
							copyAdsenseCptnPanel.setVisible(true);
						}
					}
					Log.info("success");
				}
				
				@Override
				public void onFailure(Throwable caught) {
					utils.dismissStaticMessage();
					utils.alert(caught.getMessage());
					Log.warn("", caught);
				}
			});
		} catch (RequestException e) {
			utils.dismissAllStaticMessages();
			Log.error("", e);
		}
	}



	private boolean isPrjModeNull() {
		return !isAdSenseUpdate4User && (projectId == null || "none".equals(projectId));
	}


	
	public void disableAdSenseCodeArea() {
		isInViewMode = true;
		radioBtnViewMode.setValue(isInViewMode);
		adsenseCodeTxtArea.setStyleName(resources.globalCSS().readonly());
		adsenseCodeTxtArea.addKeyPressHandler(new KeyPressHandler() {//disable field
			@Override
			public void onKeyPress(KeyPressEvent event) {
				char c = event.getCharCode();
				if(c != '\t' && isInViewMode){
					event.preventDefault();
				}
			}
		});
		adsenseCodeTxtArea.setReadOnly(true);
	}
	
	public void enableAdSenseCodeArea() {
		isInViewMode = false;
		radioBtnViewMode.setValue(isInViewMode);
		adsenseCodeTxtArea.setStyleName(defaultAdSenseAreaStyleName);
		
		adsenseCodeTxtArea.addKeyPressHandler(new KeyPressHandler() {//disable field
			@Override
			public void onKeyPress(KeyPressEvent event) {
				char c = event.getCharCode();
				if(c != '\t' && isInViewMode){
					event.preventDefault();
				}
			}
		});
		adsenseCodeTxtArea.setReadOnly(false);
	}


	void hideAll() {
		adsensePanel.setVisible(false);
	}


	private void initAdSenseWidget() {
		disableAdSenseCodeArea();
		getAdSenseCode();
	}




	
	  
	  
	@Override
	public Runnable getRunOnTabSelect() {
		utils.reportPageview("/adsenseTab/");
		return runOnTabSelect;
	}
	@Override
	public String getName(){
		return "adsense";
	}



	private void btnViewOnClick() {
		radioBtnViewMode.setValue(true);
		adsenseCodeTxtArea.setText(adsenseTxt);
		submitAdsenseCodeBtn.setEnabled(false);
		disableAdSenseCodeArea();
	}
	
	private void setAdSenseCaption(String name){
		adsenseTitle4Cptn.setTitle(messages.adSenseCaptionMsg(name));
	}
	
	public void setIsUserOrForumMode(boolean isUser, String name, String id){
		isAdSenseUpdate4User = isUser;
		setAdSenseCaption(name);
		if(isUser){
			projectId = "none";
			userId = id;
		}else{
			projectId = id;
			projectName = name;
			userId = "none"; 
		}
	}

	public void setImgExplTitle(String expl){
		img1.setTitle(expl);
	}
	
	@UiField
	Image img1;
	@UiField
	Image img2;
	@UiField
	Image img3;
	private void initImageHandler(){
		MouseDownHandler mouseDownHandler = new DigestMouseDownHandler(utils);
		Image[] images = {img1,img2,img3};
		for(Image image : images){
			image.addMouseDownHandler(mouseDownHandler);
		}
	}

}

