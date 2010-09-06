package com.aggfi.digest.client.ui;

import com.aggfi.digest.client.constants.DigestConstants;
import com.aggfi.digest.client.constants.DigestMessages;
import com.aggfi.digest.client.resources.GlobalResources;
import com.aggfi.digest.client.service.DigestService;
import com.vegalabs.general.client.utils.VegaUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class DigestAboutWidget extends Composite implements RunnableOnTabSelect{

	@UiField
	HTML aboutDigestBottyPnl;
	@UiField
	HTML aboutHelpPnl;
	@UiField
	HTML aboutCreatePnl;
	@UiField
	HTML aboutAdminPnl;
	@UiField
	HTML aboutReportPnl;
	@UiField
	Anchor installAnchor;
	@UiField
	Anchor discussAnchor;
	
	private VegaUtils vegaUtils;
	
	private static DigestAboutWidgetUiBinder uiBinder = GWT
			.create(DigestAboutWidgetUiBinder.class);

	interface DigestAboutWidgetUiBinder extends
			UiBinder<Widget, DigestAboutWidget> {
	}


	@Inject
	public DigestAboutWidget(final DigestMessages messages, final DigestConstants constants, final GlobalResources resources, final DigestService digestService, final VegaUtils vegaUtils) {
		initWidget(uiBinder.createAndBindUi(this));
		this.vegaUtils = vegaUtils;
		aboutDigestBottyPnl.setHTML(constants.aboutDigestBottyStr());
		aboutHelpPnl.setHTML(messages.aboutHlpMsg(resources.tooltip().getURL()));
		aboutCreatePnl.setHTML(constants.aboutCreateStr());
		aboutAdminPnl.setHTML(constants.aboutAdminStr());
		aboutReportPnl.setHTML(constants.aboutReportStr());
		installAnchor.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				vegaUtils.reportEvent("/aboutTab/click","install", vegaUtils.retrUserId(), 1);
				vegaUtils.requestNavigateTo(constants.installDigestBottyUrl(), null);
				event.preventDefault();
			}
		});
		
		discussAnchor.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				vegaUtils.reportEvent("/aboutTab/click","discuss", vegaUtils.retrUserId(), 1);
				vegaUtils.requestNavigateTo(constants.discussDigestBottyUrl(), null);
				event.preventDefault();
			}
		});
	}
	

	public String getName(){
		return "about";
	}
	@Override
	public Runnable getRunOnTabSelect() {
		return new Runnable() {
			
			@Override
			public void run() {
				vegaUtils.reportPageview("/aboutTab/");
			}
		};
	}


}
