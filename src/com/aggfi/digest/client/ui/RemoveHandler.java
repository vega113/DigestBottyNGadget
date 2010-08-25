package com.aggfi.digest.client.ui;

import com.aggfi.digest.client.constants.DigestMessages;
import com.vegalabs.general.client.utils.VegaUtils;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ComplexPanel;

abstract class RemoveHandler {
	private DigestMessages messages = null;
	private ComplexPanel panel;
	private AfterRemovalAsyncCallback afterRemovalAsyncCallback = new AfterRemovalAsyncCallback();
	AddRemDefLabel widget = null;
	private Runnable onProjectsLoadCallback = null;
	private VegaUtils vegaUtils;
	
	protected class AfterRemovalAsyncCallback implements AsyncCallback<JSONValue>{
		
		@Override
		public void onSuccess(JSONValue result) {
			final String removedItem = widget.getSecondLblVal() != null ?  widget.getFirstLblVal() + " : " + widget.getSecondLblVal() :  widget.getFirstLblVal();
			String tag = widget.getFirstLblVal();
			widget.getSecondValLbl().setText(messages.removalSuccessMsg(removedItem));
			Timer t = new Timer() {
				public void run() {
					getPanel().remove(widget);
					widget.setVisible(false);
					vegaUtils.adjustHeight();
				}
			};
			t.schedule(1500);
			vegaUtils.dismissAllStaticMessages();
			String successMsg = "";
			if(result.isNumber() != null){
				int appliedCount = (int)result.isNumber().doubleValue();
				successMsg = messages.removedFromWavesSuccessMsg(tag,appliedCount);
			}else{
				successMsg = messages.removeSuccessMsg(tag);
			}
			onProjectsLoadCallback.run();
			vegaUtils.showSuccessMessage(successMsg, 8);
		}

		@Override
		public void onFailure(Throwable caught) {
			widget.setFirstLblKey("");
			widget.getFirstValLbl().setVisible(true);
			Log.error("", caught);
			vegaUtils.alert(caught.getMessage());
		}

	}
	
	public RemoveHandler(DigestMessages messages,ComplexPanel panel, Runnable onProjectsLoadCallback, VegaUtils vegaUtils) {
		super();
		this.messages = messages;
		this.panel = panel;
		this.onProjectsLoadCallback= onProjectsLoadCallback;
		this.vegaUtils = vegaUtils;
	}

	/**
	 * 
	 * @param widget - the widget to remove, it is obligatory to assign the widget passed as input to the field, i.e. this.widget=widget
	 */
	public abstract void onRemove(AddRemDefLabel widget);

	protected ComplexPanel getPanel() {
		return panel;
	}

	protected AfterRemovalAsyncCallback getAfterRemovalAsyncCallback() {
		return afterRemovalAsyncCallback;
	}
	
	/**
	 * set the widget to remove after the removal response is recieved from server
	 * @param widget
	 */
	public void setWidget(AddRemDefLabel widget) {
		this.widget = widget;
	}
}
