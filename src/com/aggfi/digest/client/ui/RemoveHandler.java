package com.aggfi.digest.client.ui;

import com.aggfi.digest.client.constants.DigestMessages;
import com.aggfi.digest.client.utils.DigestUtils;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.ComplexPanel;

abstract class RemoveHandler {
	private DigestMessages messages = null;
	private ComplexPanel panel;
	private AfterRemovalAsyncCallback afterRemovalAsyncCallback = new AfterRemovalAsyncCallback();
	AddRemDefLabel widget = null;
	private Runnable onProjectsLoadCallback = null;
	
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
					DigestUtils.getInstance().adjustHeight();
				}
			};
			t.schedule(1500);
			DigestUtils.getInstance().dismissAllStaticMessages();
			String successMsg = "";
			if(result.isNumber() != null){
				int appliedCount = (int)result.isNumber().doubleValue();
				successMsg = messages.removedFromWavesSuccessMsg(tag,appliedCount);
			}else{
				successMsg = messages.removeSuccessMsg(tag);
			}
			onProjectsLoadCallback.run();
			DigestUtils.getInstance().showSuccessMessage(successMsg, 8);
		}

		@Override
		public void onFailure(Throwable caught) {
			widget.setFirstLblKey("");
			widget.getFirstValLbl().setVisible(true);
			Log.error("", caught);
			DigestUtils.getInstance().alert(caught.getMessage());
		}

	}
	
	public RemoveHandler(DigestMessages messages,ComplexPanel panel, Runnable onProjectsLoadCallback) {
		super();
		this.messages = messages;
		this.panel = panel;
		this.onProjectsLoadCallback= onProjectsLoadCallback;
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
