package com.aggfi.digest.client.ui;

import com.aggfi.digest.client.constants.SimpleConstants;
import com.aggfi.digest.client.constants.SimpleMessages;
import com.aggfi.digest.client.resources.GlobalResources;
import com.aggfi.digest.client.service.IDigestService;
import com.aggfi.digest.client.utils.IDigestUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class DigestAboutWidget extends Composite {

	@UiField
	HTML aboutDigestBottyPnl;
	@UiField
	HTML contactInfoPnl;
	
	private static DigestAboutWidgetUiBinder uiBinder = GWT
			.create(DigestAboutWidgetUiBinder.class);

	interface DigestAboutWidgetUiBinder extends
			UiBinder<Widget, DigestAboutWidget> {
	}


	@Inject
	public DigestAboutWidget(final SimpleMessages messages, final SimpleConstants constants, final GlobalResources resources, final IDigestService digestService, final IDigestUtils digestUtils) {
		initWidget(uiBinder.createAndBindUi(this));
		aboutDigestBottyPnl.setHTML(constants.aboutDigestbottyStr());
		contactInfoPnl.setHTML(constants.contactInfoValueStr());
	}


}
