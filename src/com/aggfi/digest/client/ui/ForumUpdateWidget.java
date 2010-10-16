package com.aggfi.digest.client.ui;

import com.aggfi.digest.client.constants.ConstantsImpl;
import com.aggfi.digest.client.constants.DigestConstants;
import com.aggfi.digest.client.constants.DigestMessages;
import com.aggfi.digest.client.constants.MessagesImpl;
import com.aggfi.digest.client.resources.GlobalResources;
import com.aggfi.digest.client.service.DigestService;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.inject.Inject;
import com.vegalabs.general.client.utils.VegaUtils;

public class ForumUpdateWidget extends Composite implements RunnableOnTabSelect{

	private static ForumUpdateWidgetUiBinder uiBinder = GWT
			.create(ForumUpdateWidgetUiBinder.class);

	interface ForumUpdateWidgetUiBinder extends
			UiBinder<Widget, ForumUpdateWidget> {
	}
	
	
	@UiField FlexTable updateGadgetFlexTbl;
	@UiField PushButton updateBtn;
	@UiField Image spinnerImg;
	
	
	DigestService service;
	DigestMessages messages;
	DigestConstants constants;
	MessagesImpl messagesAdSense;
	ConstantsImpl constantsAdSense;
	GlobalResources resources;
	private VegaUtils utils;
	protected Runnable onProjectsLoadCallback;
	private Runnable runOnTabSelect;
	ProjectSelectWidget projectSelectWidget;

	@Inject
	public ForumUpdateWidget(final DigestMessages messages, final DigestConstants constants,final MessagesImpl messagesAdSense, final ConstantsImpl constantsAdSense,
			final GlobalResources resources, final DigestService service, final VegaUtils utils) {
		initWidget(uiBinder.createAndBindUi(this));
		
		resources.globalCSS().ensureInjected();
		this.service = service;
		this.messagesAdSense = messagesAdSense;
		this.constantsAdSense = constantsAdSense;
		this.messages = messages;
		this.constants = constants;
		this.resources = resources;
		this.utils = utils;
		
		initImageHandler();
		
		spinnerImg.setVisible(false);
		
		onProjectsLoadCallback = new Runnable() {
			
			@Override
			public void run() {
				Log.debug("ForumUpdateWidget Running");
			}
		};
		
		runOnTabSelect = new Runnable() {
			
			@Override
			public void run() {
				initForumUpdateWidget();
				utils.adjustHeight();
			}
		};
		
		
		updateBtn.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				spinnerImg.setVisible(true);
				try {
					service.updateDigestInfo(getProjectId(),authorVal.getText(), digestNameVal.getText(),
								descriptionVal.getText(),installerThumbnailUrlVal.getText(), forumSiteUrlVal.getText(),new AsyncCallback<JSONValue>() {
						
						@Override
						public void onSuccess(JSONValue result) {
							spinnerImg.setVisible(false);
							utils.showSuccessMessage(constants.successStr(), 3);
							utils.showDismissibleMessage(constants.updateForumInfoBtnExpl());
						}
						
						@Override
						public void onFailure(Throwable caught) {
							utils.alert(caught.getMessage());
							Log.warn("", caught);
							spinnerImg.setVisible(false);
						}
					});
				} catch (RequestException e) {
					spinnerImg.setVisible(false);
					Log.warn("", e);
				}
				
			}
		});
	}
	
	TextBox authorVal = null;
	TextBox digestNameVal = null;
	TextBox descriptionVal = null;
	TextBox installerThumbnailUrlVal = null;
	TextBox forumSiteUrlVal = null;
	
	private void initCreateGadgetFlexTbl(FlexTable tbl) {
		tbl.setStylePrimaryName(resources.globalCSS().gridStyle());

		int row = 0;
		

		Label authorLbl = new Label(constants.authorStr()+"*");
		authorVal = new TextBox();
		authorVal.setTitle(constants.authorTitle());
		tbl.setWidget(row, 0, authorLbl);
		tbl.setWidget(row, 1, authorVal);
		row++;



		Label digestNameLbl = new Label(constants.digestNameStr()+"*");
		digestNameVal = new TextBox();
		digestNameVal.setTitle(constants.digestNameTitle());
		tbl.setWidget(row, 0, digestNameLbl);
		tbl.setWidget(row, 1, digestNameVal);
		row++;

		Label descriptionLbl = new Label(constants.descriptionStr());
		descriptionVal = new TextBox();
		descriptionVal.setTitle(constants.descriptionTitle());
		tbl.setWidget(row, 0, descriptionLbl);
		tbl.setWidget(row, 1, descriptionVal);
		row++;

		Label installerThumbnailUrlLbl = new Label(constants.installerThumbnailUrlStr());
		installerThumbnailUrlVal = new TextBox();
		installerThumbnailUrlVal.setTitle(constants.installerThumbnailUrlTitle());
		tbl.setWidget(row, 0, installerThumbnailUrlLbl);
		tbl.setWidget(row, 1, installerThumbnailUrlVal);
		row++;


		Label forumSiteUrlLbl = new Label(constants.forumSiteUrlStr());
		forumSiteUrlVal = new TextBox();
		forumSiteUrlVal.setTitle(constants.forumSiteUrlTitle());
		tbl.setWidget(row, 0, forumSiteUrlLbl);
		tbl.setWidget(row, 1, forumSiteUrlVal);
		row++;

		
		String[] exampleStrs = {constants.authorExmpl(),constants.digestNameExmpl(),constants.descriptionExmpl(),constants.installerThumbnailUrlExmpl(),constants.forumSiteUrlExmpl()};
		MouseDownHandler mouseDownHandler = new DigestMouseDownHandler(utils);
		
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
		
	}
	
	protected void initForumUpdateWidget() {
		initCreateGadgetFlexTbl(updateGadgetFlexTbl);
		String projectId = getProjectId();
		try {
			spinnerImg.setVisible(true);
			service.retrDigestInfo(projectId,new AsyncCallback<JSONValue>() {
				
				@Override
				public void onSuccess(JSONValue result) {
					String authorName = result.isObject().get("authorName").isString().stringValue();
					String forumName = result.isObject().get("forumName").isString().stringValue();
					String forumDescription = result.isObject().get("forumDescription").isString().stringValue();
					String installerThumbnailUrl = result.isObject().get("installerThumbnailUrl").isString().stringValue();
					String forumSiteUrl = result.isObject().get("forumSiteUrl").isString().stringValue();
					
					authorVal.setText(authorName);
					digestNameVal.setText(forumName);
					descriptionVal.setText(forumDescription);
					installerThumbnailUrlVal.setText(installerThumbnailUrl);
					forumSiteUrlVal.setText(forumSiteUrl);
					spinnerImg.setVisible(false);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					utils.alert(caught.getMessage());
					Log.warn("", caught);
					spinnerImg.setVisible(false);
				}
			});
		} catch (RequestException e) {
			Log.warn("", e);
		}
	}
	
	@UiField
	Image img2;
	private void initImageHandler(){
		MouseDownHandler mouseDownHandler = new DigestMouseDownHandler(utils);
		Image[] images = {img2};
		for(Image image : images){
			image.addMouseDownHandler(mouseDownHandler);
		}
	}
	
	protected String getProjectId() {
		String projectId = projectSelectWidget.getPrjList().getValue(projectSelectWidget.getPrjList().getSelectedIndex() > -1 ? projectSelectWidget.getPrjList().getSelectedIndex() : 0);
		return projectId;
	}

	@Override
	public Runnable getRunOnTabSelect() {
		utils.reportPageview("/forumTab/");
		return runOnTabSelect;
	}
	@Override
	public String getName(){
		return "forum update";
	}

	public void setProjectSelectWidget(ProjectSelectWidget projectSelectWidget) {
		this.projectSelectWidget = projectSelectWidget;
	}

}
