package com.aggfi.digest.client.ui;

import com.aggfi.digest.client.constants.DigestConstants;
import com.aggfi.digest.client.constants.DigestMessages;
import com.aggfi.digest.client.resources.GlobalResources;
import com.aggfi.digest.client.service.DigestService;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
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
	HTML contactInfoPnl;
	
	private static DigestAboutWidgetUiBinder uiBinder = GWT
			.create(DigestAboutWidgetUiBinder.class);

	interface DigestAboutWidgetUiBinder extends
			UiBinder<Widget, DigestAboutWidget> {
	}


	@Inject
	public DigestAboutWidget(final DigestMessages messages, final DigestConstants constants, final GlobalResources resources, final DigestService digestService) {
		initWidget(uiBinder.createAndBindUi(this));
		aboutDigestBottyPnl.setHTML(constants.aboutDigestBottyStr());
		aboutHelpPnl.setHTML(constants.aboutHlpStr());
		aboutCreatePnl.setHTML(constants.aboutCreateStr());
		aboutAdminPnl.setHTML(constants.aboutAdminStr());
		aboutReportPnl.setHTML(constants.aboutReportStr());
		String cntMsg = messages.contactInfoValueMsg(constants.discussDigestBottyUrl(),constants.installDigestBottyUrl());
		contactInfoPnl.setHTML(cntMsg);
	}
	

	@Override
	public Runnable getRunOnTabSelect() {
		return new Runnable() {
			
			@Override
			public void run() {
				
			}
		};
	}


}
