package com.aggfi.digest.client.ui;

import com.aggfi.digest.client.utils.DigestUtils;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.Image;

public class DigestMouseDownHandler implements MouseDownHandler {
	@Override
	public void onMouseDown(MouseDownEvent event) {
		String title = ((Image)event.getSource()).getTitle();
		DigestUtils.getInstance().showTimerMessage(title, 12);
	}
}
