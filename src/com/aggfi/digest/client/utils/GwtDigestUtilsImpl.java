package com.aggfi.digest.client.utils;

import com.google.gwt.user.client.Window;

public class GwtDigestUtilsImpl implements IDigestUtils {

	@Override
	public String retrUserId() {
		return "vega113@googlewave.com";
	}

	@Override
	public void alert(String msg) {
		Window.alert(msg);
	}

}
