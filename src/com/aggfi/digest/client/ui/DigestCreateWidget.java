/**
 * 
 */
package com.aggfi.digest.client.ui;

import com.aggfi.digest.client.constants.DigestConstants;
import com.aggfi.digest.client.constants.DigestMessages;
import com.aggfi.digest.client.model.JsDigest;
import com.aggfi.digest.client.resources.GlobalResources;
import com.aggfi.digest.client.service.DigestService;
import com.vegalabs.general.client.utils.VegaUtils;
import com.aggfi.digest.shared.FieldVerifier;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.inject.Inject;

/**
 * @author vega
 *
 */
public class DigestCreateWidget extends Composite  implements RunnableOnTabSelect{

	private static DigestCreateWidgetUiBinder uiBinder = GWT
	.create(DigestCreateWidgetUiBinder.class);

	interface DigestCreateWidgetUiBinder extends
	UiBinder<Widget, DigestCreateWidget> {
	}
	
	DigestService digestService;

	DisclosurePanel instructionsDsPanel;
	@UiField
	FlexTable createGadgetFlexTbl;
	@UiField
	PushButton submitBtn;
	@UiField
	CheckBox isPublicBox;
	@UiField
	CheckBox isAdsEnabledBox;
	@UiField
	HorizontalPanel isEnableAdsPanel;
	
	@UiField
	HTML isPublicQuestion;
	
	@UiField
	Image imgIsPublicCheckBox;
	
	private VegaUtils vegaUtils;

	private Runnable onDigestCreateWidgetLoad;
	
	
	
	protected class ClearWarningClickHandler implements ClickHandler {
		GlobalResources resources;
		public ClearWarningClickHandler(GlobalResources resources){
			this.resources = resources;
		}
		
		@Override
		public void onClick(ClickEvent event) {
			vegaUtils.dismissAlert();
		}
	}


	@Inject
	public DigestCreateWidget(final DigestMessages messages, final DigestConstants constants, final GlobalResources resources, final DigestService digestService, final VegaUtils vegaUtils) {
		initWidget(uiBinder.createAndBindUi(this));
		
		resources.globalCSS().ensureInjected();
		this.digestService = digestService;
		this.vegaUtils = vegaUtils;
		
		initCreateGadgetFlexTbl(createGadgetFlexTbl,constants,resources);
		submitBtn.setText(constants.submitBtnStr());
		
		isPublicQuestion.setText(constants.isPublicQuestion());
		isPublicQuestion.setTitle(constants.isPublicQuestionTtl());
		isPublicBox.setTitle(constants.isPublicQuestionTtl());
		
		isEnableAdsPanel.setVisible(false);//XXX remove

		submitBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				try{
					Log.debug("click on submitBtn");
					JsDigest digest = initExtDigestFromFields(createGadgetFlexTbl,isPublicBox,isAdsEnabledBox);
					FieldVerifier.areValidDigestFields(digest,messages,constants);
					encodeDigest(digest);
					vegaUtils.showStaticMessage(messages.sentRequestForCreateMsg(digest.getName()));
					try {
						digestService.createDigest(digest, new AsyncCallback<JSONValue>() {
							
							@Override
							public void onSuccess(JSONValue resultJsonValue) {
								JSONObject resultJson = resultJsonValue.isObject();
								String outMessage = null;
								if(resultJson != null && resultJson.isObject() != null && resultJson.isObject().containsKey("message")){
									outMessage = resultJson.isObject().get("message").isString().stringValue();
								}else if(resultJsonValue.isString() != null){
									outMessage = resultJsonValue.isString().stringValue();
								}else{
									//cannot happen
									throw new AssertionError ("No message body in create digest - it cannot happen!");
								}
								vegaUtils.dismissStaticMessage();
								outMessage = outMessage.substring(1).substring(0, outMessage.length()-2); //workaround to remove ""
								vegaUtils.showSuccessMessage(outMessage, 8);
								vegaUtils.reportPageview("/createTab/forumCreationSuccess");
							}
							
							@Override
							public void onFailure(Throwable caught) {
								vegaUtils.dismissAllStaticMessages();
								vegaUtils.alert(caught.getMessage());
							}
						});
					} catch (RequestException e) {
						vegaUtils.dismissStaticMessage();
						vegaUtils.alert(e.getMessage());
						Log.error("",e);
					}
				}catch(IllegalArgumentException e){
					Log.error("should be verification error!",e);
					vegaUtils.dismissStaticMessage();
					vegaUtils.alert(e.getMessage());
				}catch(Exception e){
					Log.error("",e);
					vegaUtils.dismissStaticMessage();
					vegaUtils.alert(e.getMessage());
				}
				vegaUtils.reportEvent("/createTab/click","createForum", vegaUtils.retrUserId(), 1);
			}

			
		});
	}
	

	private void encodeDigest(JsDigest digest) {
		digest.setAuthor(digest.getAuthor().replace("\"", "'"));
		digest.setName(digest.getName().replace("\"", "'"));
		digest.setDescription(digest.getDescription().replace("\"", "'"));
	}

	private void initCreateGadgetFlexTbl(FlexTable tbl,final DigestConstants constants, GlobalResources resources) {
		tbl.setStylePrimaryName(resources.globalCSS().gridStyle());

		int row = 0;
		ClearWarningClickHandler clearWarningClickHandler = new ClearWarningClickHandler(resources);
		
		Label ownerLbl = new Label(constants.ownerStr()+"*");
		final TextBox ownerVal = new TextBox();
		
		ownerVal.setStyleName(resources.globalCSS().readonly());
		ownerVal.addClickHandler(clearWarningClickHandler);
		ownerVal.addKeyPressHandler(new KeyPressHandler() {//disable field
			@Override
			public void onKeyPress(KeyPressEvent event) {
				char c = event.getCharCode();
				if(c != '\t'){
					event.preventDefault();
				}
			}
		});
		ownerVal.setTitle(constants.ownerTitle());
		tbl.setWidget(row, 0, ownerLbl);
		tbl.setWidget(row, 1, ownerVal);
		row++;

		Label authorLbl = new Label(constants.authorStr()+"*");
		final TextBox authorVal = new TextBox();
		authorVal.addClickHandler(clearWarningClickHandler);
		authorVal.setTitle(constants.authorTitle());
		tbl.setWidget(row, 0, authorLbl);
		tbl.setWidget(row, 1, authorVal);
		row++;

		Label projectIdLbl = new Label(constants.projectIdStr()+"*");
		TextBox projectIdVal = new TextBox();
		projectIdVal.addClickHandler(clearWarningClickHandler);
		projectIdVal.setTitle(constants.projectIdTitle());
		tbl.setWidget(row, 0, projectIdLbl);
		tbl.setWidget(row, 1, projectIdVal);
		row++;

		Label domainLbl = new Label(constants.domainStr()+"*");
		domainLbl.setVisible(false);
		final TextBox domainVal = new TextBox();
		domainVal.setVisible(false);
		domainVal.addClickHandler(clearWarningClickHandler);
		domainVal.setTitle(constants.domainTitle());
		tbl.setWidget(row, 0, domainLbl);
		tbl.setWidget(row, 1, domainVal);
		domainVal.setText("googlewave.com");
		domainVal.setStyleName(resources.globalCSS().readonly());
		domainVal.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				char c = event.getCharCode();
				if(c != '\t'){
					event.preventDefault();
				}
			}
		});
		row++;

		Label digestNameLbl = new Label(constants.digestNameStr()+"*");
		TextBox digestNameVal = new TextBox();
		digestNameVal.addClickHandler(clearWarningClickHandler);
		digestNameVal.setTitle(constants.digestNameTitle());
		tbl.setWidget(row, 0, digestNameLbl);
		tbl.setWidget(row, 1, digestNameVal);
		row++;

		Label descriptionLbl = new Label(constants.descriptionStr());
		TextBox descriptionVal = new TextBox();
		descriptionVal.addClickHandler(clearWarningClickHandler);
		descriptionVal.setTitle(constants.descriptionTitle());
		tbl.setWidget(row, 0, descriptionLbl);
		tbl.setWidget(row, 1, descriptionVal);
		row++;

		Label installerThumbnailUrlLbl = new Label(constants.installerThumbnailUrlStr());
		TextBox installerThumbnailUrlVal = new TextBox();
		installerThumbnailUrlVal.addClickHandler(clearWarningClickHandler);
		installerThumbnailUrlVal.setTitle(constants.installerThumbnailUrlTitle());
		tbl.setWidget(row, 0, installerThumbnailUrlLbl);
		tbl.setWidget(row, 1, installerThumbnailUrlVal);
		row++;

		Label robotThumbnailUrlLbl = new Label(constants.robotThumbnailUrlStr());
		robotThumbnailUrlLbl.setVisible(false);
		TextBox robotThumbnailUrlVal = new TextBox();
		robotThumbnailUrlVal.addClickHandler(clearWarningClickHandler);
		robotThumbnailUrlVal.setVisible(false);
		robotThumbnailUrlVal.setTitle(constants.robotThumbnailUrlTitle());
		tbl.setWidget(row, 0, robotThumbnailUrlLbl);
		tbl.setWidget(row, 1, robotThumbnailUrlVal);
		row++;

		Label forumSiteUrlLbl = new Label(constants.forumSiteUrlStr());
		forumSiteUrlLbl.setVisible(false);
		TextBox forumSiteUrlVal = new TextBox();
		forumSiteUrlVal.setVisible(false);
		forumSiteUrlVal.addClickHandler(clearWarningClickHandler);
		forumSiteUrlVal.setTitle(constants.forumSiteUrlTitle());
		tbl.setWidget(row, 0, forumSiteUrlLbl);
		tbl.setWidget(row, 1, forumSiteUrlVal);
		row++;

		Label googlegroupsIdLbl = new Label(constants.googlegroupsIdStr());
		TextBox googlegroupsIdVal = new TextBox();
		googlegroupsIdVal.setText("@googlegroups.com");
		googlegroupsIdVal.addClickHandler(new ClickHandler() {
			boolean isMessageShown = false;
			@Override
			public void onClick(ClickEvent event) {
				vegaUtils.dismissAlert();
				if(!isMessageShown){
					isMessageShown = true;
//					vegaUtils.showStaticMessage(constants.googlegroupsWarningStr());
					vegaUtils.alert(constants.googlegroupsWarningStr());
				}
				
			}
		});
		googlegroupsIdVal.setTitle(constants.googlegroupsIdTitle());
		tbl.setWidget(row, 0, googlegroupsIdLbl);
		tbl.setWidget(row, 1, googlegroupsIdVal);
		row++;
		
		String[] exampleStrs = {constants.ownerExmpl(),constants.authorExmpl(),constants.projectIdExmpl(),constants.domainExmpl(),
					constants.digestNameExmpl(),constants.descriptionExmpl(),constants.installerThumbnailUrlExmpl(),constants.robotThumbnailUrlExmpl(),constants.forumSiteUrlExmpl(),
					constants.googlegroupsIdExmpl()};
		MouseDownHandler mouseDownHandler = new DigestMouseDownHandler(vegaUtils);
		imgIsPublicCheckBox.addMouseDownHandler(mouseDownHandler);
		for(int i = 0; i < row; i++){
			if(i%2 == 0){
				tbl.getCellFormatter().setStyleName(row,0, resources.globalCSS().highlight());
			}else{
				tbl.getCellFormatter().setStyleName(row,0, resources.globalCSS().regularRow());
			}
			Widget w0 = tbl.getWidget(i, 0);
			w0.setHeight(constants.itemCreateHeight() + 4 + "px");
			HorizontalPanel hp = new HorizontalPanel();
			VerticalPanel vp = new VerticalPanel();
			ImageResource tooltipRes = resources.tooltip();
			Image tooltipImage = new Image(tooltipRes);
			tooltipImage.setPixelSize(16, 16);
			vp.add(tooltipImage);
			Widget w = tbl.getWidget(i, 1);
			w.setTitle( w.getTitle() + " " + constants.exampleWord() + "\"" +  exampleStrs[i] + "\"");
			String title = w.getTitle();
			tooltipImage.setTitle(title);
			tooltipImage.addMouseDownHandler(mouseDownHandler);
			int textBoxWidth = (int)(constants.basicWidthInt()*0.66);
			w.setWidth( textBoxWidth + "px");
			w.setHeight(constants.itemCreateHeight() + "px");
			hp.setWidth(textBoxWidth + 16 + "px");
			hp.add(vp);
			hp.add(w);
			hp.setVisible(w.isVisible());
			tbl.setWidget(i, 1, hp);
		}
		
		ColumnFormatter colFormatter = tbl.getColumnFormatter();
		colFormatter.addStyleName(1, resources.globalCSS().inputRegular());
		
		onDigestCreateWidgetLoad = new Runnable() {
			
			@Override
			public void run() {
				ownerVal.setText(vegaUtils.retrUserId());
				authorVal.setText(vegaUtils.retrUserName());
				domainVal.setText("googlewave.com");
				vegaUtils.adjustHeight();
				vegaUtils.reportPageview("/createTab/");
			}
		};
	}

	public static JsDigest initExtDigestFromFields(FlexTable w,CheckBox box, CheckBox isAdsBox) throws IllegalArgumentException{
		
		int row=0;
		String ownerId = getStrFromTxtBox(row,w).trim(); row++;
		String authorName = getStrFromTxtBox(row,w).trim();row++;
		String projectId = getStrFromTxtBox(row,w).trim();row++;
		String domain = getStrFromTxtBox(row,w).trim();row++;
		String digestName = getStrFromTxtBox(row,w).trim();row++;
		
		String description = getStrFromTxtBox(row,w).trim();row++;
		String installerThumbnailUrl = getStrFromTxtBox(row,w).trim();row++;
		String robotThumbnailUrl = getStrFromTxtBox(row,w).trim();row++;
		String forumSiteUrl = getStrFromTxtBox(row,w).trim();row++;
		String googlegroupsId = getStrFromTxtBox(row,w).trim();row++;
		boolean isPublicOnCreate = box.getValue();
		boolean isAdsEnabled = isAdsBox.getValue();
		
		String ownerStr = ownerId.substring(0, ownerId.indexOf("@"));
		String domainStr = ownerId.substring(ownerStr.length()+1, ownerStr.length()+1 + ownerId.substring(ownerStr.length()+1).indexOf("."));
		
		JsDigest digest = JsDigestInstance();
		digest.setOwnerId(ownerId.toLowerCase());
		digest.setAuthor(authorName);
		digest.setProjectId((ownerStr + "-" + domainStr + "-" + projectId).toLowerCase());
		digest.setDomain(domain);
		digest.setName(digestName);
		digest.setDescription(description);
		digest.setInstallerThumbnailUrl(installerThumbnailUrl);
		digest.setRobotThumbnailUrl(robotThumbnailUrl);
		digest.setForumSiteUrl(forumSiteUrl);
		digest.setGooglegroupsId(googlegroupsId.equals("@googlegroups.com")? "" : googlegroupsId.toLowerCase());
		digest.setPublicOnCreate(isPublicOnCreate);
		digest.setAdsEnabled(isAdsEnabled);
		return digest;
	}
	
	private final native static JsDigest JsDigestInstance() /*-{
				 $wnd.digest = {};
				return $wnd.digest;
			}-*/;
	
	private static String getStrFromTxtBox(int row,FlexTable widget ){
		ComplexPanel panel = (ComplexPanel)widget.getWidget(row, 1);
		TextBox textBox = (TextBox)panel.getWidget(1);
		String out = unescape(textBox.getText());
		return out;
	}
	
	 private native static String unescape (String val) /*-{
     	return unescape(val);
 	 }-*/;

	@Override
	public Runnable getRunOnTabSelect() {
		return onDigestCreateWidgetLoad;
	}
	@Override
	public String getName(){
		return "create";
	}
}
