package com.aggfi.digest.client.ui;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.aggfi.digest.client.constants.ConstantsImpl;
import com.aggfi.digest.client.constants.DigestConstants;
import com.aggfi.digest.client.constants.DigestMessages;
import com.aggfi.digest.client.constants.MessagesImpl;
import com.aggfi.digest.client.resources.GlobalResources;
import com.aggfi.digest.client.service.DigestService;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.LegendPosition;
import com.google.gwt.visualization.client.Selection;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.events.SelectHandler;
import com.google.gwt.visualization.client.events.SelectHandler.SelectEvent;
import com.google.gwt.visualization.client.visualizations.BarChart;
import com.google.gwt.visualization.client.visualizations.LineChart;
import com.google.gwt.visualization.client.visualizations.PieChart;
import com.google.gwt.visualization.client.visualizations.PieChart.Options;
import com.google.inject.Inject;
import com.vegalabs.general.client.utils.VegaUtils;

public class AdsenseWidget extends Composite implements RunnableOnTabSelect{

	private static AdsenseWidgetUiBinder uiBinder = GWT
			.create(AdsenseWidgetUiBinder.class);

	interface AdsenseWidgetUiBinder extends UiBinder<Widget, AdsenseWidget> {
	}

	public AdsenseWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	
	
	@UiField
	protected VerticalPanel adsensePanel;
	@UiField
	SimplePanel prjListContainer;
	@UiField
	TextArea adsenseCodeTxtArea;
	@UiField
	Button submitAdsenseCodeBtn;
	ProjectSelectWidget projectSelectWidget;
	
	DigestService digestService;
	DigestMessages messages;
	DigestConstants constants;
	MessagesImpl messagesAdSense;
	ConstantsImpl constantsAdSense;
	GlobalResources resources;
	private VegaUtils utils;
	protected Runnable onProjectsLoadCallback;
	private Runnable runOnTabSelect;
	
	boolean isInViewMode = true;
	
	

	@Inject
	public AdsenseWidget(final DigestMessages messages, final DigestConstants constants,final MessagesImpl messagesAdSense, final ConstantsImpl constantsAdSense, final GlobalResources resources, final DigestService digestService, final VegaUtils utils) {
		initWidget(uiBinder.createAndBindUi(this));
//		hideAll();
		resources.globalCSS().ensureInjected();
		this.digestService = digestService;
		this.messagesAdSense = messagesAdSense;
		this.constantsAdSense = constantsAdSense;
		this.messages = messages;
		this.constants = constants;
		this.resources = resources;
		this.utils = utils;
		
		submitAdsenseCodeBtn.setEnabled(false);
		
		adsenseCodeTxtArea.setStyleName(resources.globalCSS().readonly());
		adsenseCodeTxtArea.addKeyPressHandler(new KeyPressHandler() {//disable field
			@Override
			public void onKeyPress(KeyPressEvent event) {
				char c = event.getCharCode();
				if(c != '\t' && isInViewMode){
					event.preventDefault();
				}
			}
		});
		
		onProjectsLoadCallback = new Runnable() {
			
			@Override
			public void run() {
				Log.debug("DigestAdSenseWidget::DigestAdSenseWidget Running");
			}
		};
		
		runOnTabSelect = new Runnable() {
			
			@Override
			public void run() {
					initAdSenseWidget();
					utils.adjustHeight();
			}
		};
		utils.reportPageview("/adsenseTab/");
	}


	private void hideAll() {
		adsensePanel.setVisible(false);
	}


	private void initAdSenseWidget() {
		this.projectSelectWidget =new com.aggfi.digest.client.ui.ProjectSelectWidget(messages, constants, resources, digestService, onProjectsLoadCallback,utils );
		prjListContainer.clear();
		prjListContainer.add(projectSelectWidget);
		utils.reportPageview("/adsenseTab/");
	}

	private String getProjectId() {
		return projectSelectWidget.getPrjList().getValue(projectSelectWidget.getPrjList().getSelectedIndex());
	}
	private String getProjectName() {
		return projectSelectWidget.getPrjList().getItemText (projectSelectWidget.getPrjList().getSelectedIndex());
	}

	
	  
		void handleOnSelectPrjList(ChangeEvent event){
//			hideAll();
			if(getProjectId() != null && !"".equals(getProjectId())){
			}else{
				utils.alert(constants.noForumSelectedWarning());
			}
		}
	  
	@Override
	public Runnable getRunOnTabSelect() {
		return runOnTabSelect;
	}
	@Override
	public String getName(){
		return "adsense";
	}
}

