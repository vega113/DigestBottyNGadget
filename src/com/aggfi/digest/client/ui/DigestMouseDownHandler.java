package com.aggfi.digest.client.ui;

import com.vegalabs.general.client.utils.VegaUtils;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.Image;

public class DigestMouseDownHandler implements MouseDownHandler {
	private VegaUtils vegaUtils;
	public DigestMouseDownHandler(VegaUtils vegaUtils){
		this.vegaUtils = vegaUtils;
	}
	@Override
	public void onMouseDown(MouseDownEvent event) {
		String title = ((Image)event.getSource()).getTitle();
		vegaUtils.showTimerMessage(title, 12);
	}
}
