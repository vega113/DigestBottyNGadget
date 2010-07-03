package com.aggfi.digest.client.ui;

import com.aggfi.digest.client.constants.DigestMessages;
import com.aggfi.digest.client.utils.DigestUtils;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CellPanel;

abstract class RemoveHandler {
	private DigestMessages messages = null;
	private CellPanel panel;
	private AfterRemovalAsyncCallback afterRemovalAsyncCallback = new AfterRemovalAsyncCallback();
	AddRemDefLabel widget = null;
	
	protected class AfterRemovalAsyncCallback implements AsyncCallback<JSONValue>{
		
		@Override
		public void onSuccess(JSONValue result) {
			final String removedItem = widget.getSecondLblVal() != null ?  widget.getFirstLblVal() + " : " + widget.getSecondLblVal() :  widget.getFirstLblVal();
			widget.getSecondValLbl().setText(messages.removalSuccessMsg(removedItem));
			Timer t = new Timer() {
				public void run() {
					getPanel().remove(widget);
					widget.setVisible(false);
					DigestUtils.getInstance().adjustHeight();
				}
			};
			t.schedule(1500);
		}

		@Override
		public void onFailure(Throwable caught) {
			widget.setFirstLblKey("");
			widget.getFirstValLbl().setVisible(true);
			Log.error("", caught);

		}

	}
	
	public RemoveHandler(DigestMessages messages,CellPanel panel) {
		super();
		this.messages = messages;
		this.panel = panel;
	}

	/**
	 * 
	 * @param widget - the widget to remove, it is obligatory to assign the widget passed as input to the field, i.e. this.widget=widget
	 */
	public abstract void onRemove(AddRemDefLabel widget);

	protected CellPanel getPanel() {
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
