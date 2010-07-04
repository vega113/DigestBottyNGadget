/**
 * 
 */
package com.aggfi.digest.client.ui;

import com.aggfi.digest.client.constants.DigestConstants;
import com.aggfi.digest.client.constants.DigestMessages;
import com.aggfi.digest.client.model.JsDigest;
import com.aggfi.digest.client.resources.GlobalResources;
import com.aggfi.digest.client.service.DigestService;
import com.aggfi.digest.client.utils.DigestUtils;
import com.aggfi.digest.shared.FieldVerifier;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.inject.Inject;

/**
 * @author vega
 *
 */
public class DigestCreateWidget extends Composite{

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
	HTML outputTxt;
	@UiField
	CheckBox isPublicBox;
	@UiField
	HTML isPublicQuestion;
	@UiField
	CaptionPanel outputTxtCaption;
	
	
	
	protected class ClearWarningClickHandler implements ClickHandler {
		GlobalResources resources;
		public ClearWarningClickHandler(GlobalResources resources){
			this.resources = resources;
		}
		
		@Override
		public void onClick(ClickEvent event) {
			outputTxt.setText("");
			outputTxt.setStyleName(resources.globalCSS().warning());
		}
	}


	@Inject
	public DigestCreateWidget(final DigestMessages messages, final DigestConstants constants, final GlobalResources resources, final DigestService digestService) {
		initWidget(uiBinder.createAndBindUi(this));
		
		resources.globalCSS().ensureInjected();
		
		this.digestService = digestService;
		
		initCreateGadgetFlexTbl(createGadgetFlexTbl,constants,resources);
		submitBtn.setText(constants.submitBtnStr());
		
		outputTxt.setStyleName(resources.globalCSS().readonly());

		isPublicQuestion.setText(constants.isPublicQuestion());
		isPublicQuestion.setTitle(constants.isPublicQuestionTtl());
		isPublicBox.setTitle(constants.isPublicQuestionTtl());

		submitBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				try{
//					JsDigest digest = new JsDigest();
					JsDigest digest = initExtDigestFromFields(createGadgetFlexTbl,isPublicBox);
					FieldVerifier.areValidDigestFields(digest,messages,constants);
					outputTxt.setStyleName(resources.globalCSS().message());
					outputTxt.setText("Sending request...");
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
									throw new AssertionError ("No message body in create digest - it's cannot happen!");
								}
								outputTxt.setStyleName(resources.globalCSS().messageSuccess());
								outputTxt.setText(outMessage);
//								outputTxt.setStyleName(resources.globalCSS().message());
							}
							
							@Override
							public void onFailure(Throwable caught) {
								outputTxt.setStyleName(resources.globalCSS().warning());
								outputTxt.setText(caught.getMessage());
//								outputTxt.setStyleName(resources.globalCSS().message());
							}
						});
					} catch (RequestException e) {
						outputTxt.setText(e.getMessage());
					}
				}catch(IllegalArgumentException e){
					outputTxt.setText(e.getMessage());
					outputTxt.setStyleName(resources.globalCSS().warning());
					event.preventDefault();
				}catch(Exception e){
					outputTxt.setText(e.getMessage());
					outputTxt.setStyleName(resources.globalCSS().warning());
					event.preventDefault();
				}
			}
		});
		outputTxtCaption.setCaptionText(constants.createDigestRequestStatus());
	}
	


	private void initCreateGadgetFlexTbl(FlexTable tbl,DigestConstants constants, GlobalResources resources) {
		tbl.setStylePrimaryName(resources.globalCSS().gridStyle());

		int row = 0;
		RowFormatter rowFormatter = tbl.getRowFormatter();
		
		ClearWarningClickHandler clearWarningClickHandler = new ClearWarningClickHandler(resources);
		
		Label ownerLbl = new Label(constants.ownerStr());
		TextBox ownerVal = new TextBox();
		ownerVal.setText(DigestUtils.getInstance().retrUserId());
		ownerVal.setStyleName(resources.globalCSS().readonly());
		ownerVal.addClickHandler(clearWarningClickHandler);
		ownerVal.setTitle(constants.ownerTitle());
		tbl.setWidget(row, 0, ownerLbl);
		tbl.setWidget(row, 1, ownerVal);
		row++;

		Label authorLbl = new Label(constants.authorStr());
		TextBox authorVal = new TextBox();
		authorVal.setText(DigestUtils.getInstance().retrUserName());
		authorVal.addClickHandler(clearWarningClickHandler);
		authorVal.setTitle(constants.authorTitle());
		tbl.setWidget(row, 0, authorLbl);
		tbl.setWidget(row, 1, authorVal);
		row++;

		Label projectIdLbl = new Label(constants.projectIdStr());
		TextBox projectIdVal = new TextBox();
		projectIdVal.addClickHandler(clearWarningClickHandler);
		projectIdVal.setTitle(constants.projectIdTitle());
		tbl.setWidget(row, 0, projectIdLbl);
		tbl.setWidget(row, 1, projectIdVal);
		row++;

		Label domainLbl = new Label(constants.domainStr());
		TextBox domainVal = new TextBox();
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

		Label digestNameLbl = new Label(constants.digestNameStr());
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

		Label toolbarIconUrlLbl = new Label(constants.toolbarIconUrlStr());
		TextBox toolbarIconUrlVal = new TextBox();
		toolbarIconUrlVal.addClickHandler(clearWarningClickHandler);
		toolbarIconUrlVal.setTitle(constants.toolbarIconUrlTitle());
		tbl.setWidget(row, 0, toolbarIconUrlLbl);
		tbl.setWidget(row, 1, toolbarIconUrlVal);
		row++;

		Label robotThumbnailUrlLbl = new Label(constants.robotThumbnailUrlStr());
		TextBox robotThumbnailUrlVal = new TextBox();
		robotThumbnailUrlVal.addClickHandler(clearWarningClickHandler);
		robotThumbnailUrlVal.setTitle(constants.robotThumbnailUrlTitle());
		tbl.setWidget(row, 0, robotThumbnailUrlLbl);
		tbl.setWidget(row, 1, robotThumbnailUrlVal);
		row++;

		Label forumSiteUrlLbl = new Label(constants.forumSiteUrlStr());
		TextBox forumSiteUrlVal = new TextBox();
		forumSiteUrlVal.addClickHandler(clearWarningClickHandler);
		forumSiteUrlVal.setTitle(constants.forumSiteUrlTitle());
		tbl.setWidget(row, 0, forumSiteUrlLbl);
		tbl.setWidget(row, 1, forumSiteUrlVal);
		row++;

		Label googlegroupsIdLbl = new Label(constants.googlegroupsIdStr());
		TextBox googlegroupsIdVal = new TextBox();
		googlegroupsIdVal.setText("@googlegroups.com");
		googlegroupsIdVal.addClickHandler(clearWarningClickHandler);
		googlegroupsIdVal.setTitle(constants.googlegroupsIdTitle());
		tbl.setWidget(row, 0, googlegroupsIdLbl);
		tbl.setWidget(row, 1, googlegroupsIdVal);
		row++;
		
		String[] exampleStrs = {constants.ownerExmpl(),constants.authorExmpl(),constants.projectIdExmpl(),constants.domainExmpl(),
					constants.digestNameExmpl(),constants.descriptionExmpl(),constants.installerThumbnailUrlExmpl(),
					constants.toolbarIconUrlExmpl(),constants.robotThumbnailUrlExmpl(),constants.forumSiteUrlExmpl(),
					constants.googlegroupsIdExmpl()};

		for(int i = 0; i < row; i++){
			if(i%2 == 0){
				rowFormatter.addStyleName(i, resources.globalCSS().highlightRow());
			}else{
				rowFormatter.addStyleName(i, resources.globalCSS().regularRow());
			}
			Widget w = tbl.getWidget(i, 1);
			w.setWidth(((int)constants.basicWidthInt()/1.37) + "px");
			w.setTitle( w.getTitle() + " " + constants.exampleWord() + "\"" +  exampleStrs[i] + "\"");
		}
		ColumnFormatter colFormatter = tbl.getColumnFormatter();
		colFormatter.addStyleName(1, resources.globalCSS().inputRegular());
	}

	public static JsDigest initExtDigestFromFields(FlexTable w,CheckBox box) throws IllegalArgumentException{
		
		int row=0;
		String ownerId = getStrFromTxtBox(row,w).trim(); row++;
		String authorName = getStrFromTxtBox(row,w).trim();row++;
		String projectId = getStrFromTxtBox(row,w).trim();row++;
		String domain = getStrFromTxtBox(row,w).trim();row++;
		String digestName = getStrFromTxtBox(row,w).trim();row++;
		
		String description = getStrFromTxtBox(row,w).trim();row++;
		String installerThumbnailUrl = getStrFromTxtBox(row,w).trim();row++;
		String installerIconUrl = getStrFromTxtBox(row,w).trim();row++;
		String robotThumbnailUrl = getStrFromTxtBox(row,w).trim();row++;
		String forumSiteUrl = getStrFromTxtBox(row,w).trim();row++;
		String googlegroupsId = getStrFromTxtBox(row,w).trim();row++;
		boolean isPublicOnCreate = box.getValue();
		
		String ownerStr = ownerId.substring(0, ownerId.indexOf("@"));
		
		JsDigest digest = JsDigestInstance();
		digest.setOwnerId(ownerId.toLowerCase());
		digest.setAuthor(authorName);
		digest.setProjectId((ownerStr + "-" + projectId).toLowerCase());
		digest.setDomain(domain);
		digest.setName(digestName);
		digest.setDescription(description);
		digest.setInstallerThumbnailUrl(installerThumbnailUrl);
		digest.setInstallerIconUrl(installerIconUrl);
		digest.setRobotThumbnailUrl(robotThumbnailUrl);
		digest.setForumSiteUrl(forumSiteUrl);
		digest.setGooglegroupsId(googlegroupsId.equals("@googlegroups.com")? "" : googlegroupsId.toLowerCase());
		digest.setPublicOnCreate(isPublicOnCreate);
		return digest;
	}
	
	private final native static JsDigest JsDigestInstance() /*-{
				 $wnd.digest = {};
				return $wnd.digest;
			}-*/;
	
	private static String getStrFromTxtBox(int row,FlexTable widget ){
		String out = ((TextBox)widget.getWidget(row, 1)).getText();
		return out;
	}
}
