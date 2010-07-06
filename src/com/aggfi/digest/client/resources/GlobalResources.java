package com.aggfi.digest.client.resources;

import com.aggfi.digest.client.resources.css.GlobalCSS;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface GlobalResources extends ClientBundle {

	@Source("css/globalCSS.css")
	GlobalCSS globalCSS();
	
	@Source("images/remove.png")
	ImageResource remove();
	
	@Source("images/close.jpg")
	ImageResource close();
	
	@Source("images/images3.jpg")
	ImageResource tooltip();


}
