package com.aggfi.digest.client.ui;


import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class AddRemDefLabel extends Composite {
	
	private RemoveHandler removeHandler = null;
	
	@UiField
	Image removeBtn;
	@UiField
	Label firstValLbl;
	@UiField
	Label secondValLbl;
	
	String firstLblKey;
	String firstLblVal;
	String secondLblKey;
	String secondLblVal;
	
	private static AddRemDefLabelUiBinder uiBinder = GWT
			.create(AddRemDefLabelUiBinder.class);

	interface AddRemDefLabelUiBinder extends UiBinder<Widget, AddRemDefLabel> {
	}


	public AddRemDefLabel(RemoveHandler removeDefaultParticipantHandler, String firstLblKey, String firstLblVal, String secondLblKey, String secondLblVal) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.removeHandler = removeDefaultParticipantHandler;
		this.firstLblKey = firstLblKey;
		this.firstLblVal = firstLblVal;
		this.secondLblKey = secondLblKey;
		this.secondLblVal = secondLblVal;
		initWidget2();
	}


	private void initWidget2() {
		if(firstLblKey != null && firstLblKey.length() > 0){
			firstValLbl.setText(firstLblKey + "=" + firstLblVal);
		}else{
			firstValLbl.setText(firstLblVal);
		}
		if(secondLblKey != null && secondLblKey.length() > 0){
			secondValLbl.setText("  " + secondLblKey + "=" + secondLblVal);
		}else{
			secondValLbl.setText(secondLblVal);
		}
	}
	
	@UiHandler("removeBtn")
	public void removeThisLabel(ClickEvent event){
		if (removeHandler != null) {
			removeHandler.onRemove(this);
		}else{
			Log.error(this.getClass().getName() + ": RemoveHandler is null");
		}
	}


	

	public String getFirstLblKey() {
		return firstLblKey;
	}


	public void setFirstLblKey(String firstLblKey) {
		this.firstLblKey = firstLblKey;
	}


	public String getSecondLblKey() {
		return secondLblKey;
	}


	public void setSecondLblKey(String secondLblKey) {
		this.secondLblKey = secondLblKey;
	}


	public String getSecondLblVal() {
		return secondLblVal;
	}


	public void setSecondLblVal(String secondLblVal) {
		this.secondLblVal = secondLblVal;
	}


	public String getFirstLblVal() {
		return firstLblVal;
	}


	public void setFirstLblVal(String firstLblVal) {
		this.firstLblVal = firstLblVal;
	}


	protected Label getFirstValLbl() {
		return firstValLbl;
	}


	protected void setFirstValLbl(Label firstValLbl) {
		this.firstValLbl = firstValLbl;
	}


	protected Label getSecondValLbl() {
		return secondValLbl;
	}


	protected void setSecondValLbl(Label secondValLbl) {
		this.secondValLbl = secondValLbl;
	}

	
}
