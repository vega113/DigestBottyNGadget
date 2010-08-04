//package com.aggfi.digest.client.feature.views;
//
//import com.allen_sauer.gwt.log.client.Log;
//import com.google.gwt.gadgets.client.GadgetFeature;
//
//
//public class ViewsFeature implements GadgetFeature {
//	Views views;
//
//	private native Views initViews() /*-{
//		return gadgets.views;
//	}-*/;
//
//	public void initViewsFeature(){
//		try{
//			views = initViews();
//			if(views == null){
//				Log.error("views is null");
//			}else{
//				Log.info("views: " + views.toString());
//			}
//		}catch (Exception e) {
//			Log.error("", e);
//		}
//	}
//
//	public  void requestNavigateTo(String view, String optParams) {
//		try{
//			views.requestNavigateTo(view,optParams);
//		}catch (Exception e) {
//			Log.error("ViewsFeature.requestNavigateTo", e);
//		}
//	}
//
//}
