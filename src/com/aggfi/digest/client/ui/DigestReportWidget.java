package com.aggfi.digest.client.ui;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.aggfi.digest.client.constants.DigestConstants;
import com.aggfi.digest.client.constants.DigestMessages;
import com.aggfi.digest.client.resources.GlobalResources;
import com.aggfi.digest.client.service.DigestService;
import com.aggfi.digest.client.utils.DigestUtils;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.Selection;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.events.SelectHandler;
import com.google.gwt.visualization.client.visualizations.LineChart;
import com.google.gwt.visualization.client.visualizations.PieChart;
import com.google.gwt.visualization.client.visualizations.PieChart.Options;
import com.google.inject.Inject;

public class DigestReportWidget extends Composite implements RunnableOnTabSelect{

	private static final String NEW_WAVES = "NEW_WAVES";
	private static final String TAGS_BREAKDOWN = "TAGS_BREAKDOWN";
	private static DigestReportWidgetUiBinder uiBinder = GWT
			.create(DigestReportWidgetUiBinder.class);

	interface DigestReportWidgetUiBinder extends
			UiBinder<Widget, DigestReportWidget> {
	}

	@UiField
	protected VerticalPanel reportPanel;
	@UiField
	ListBox reportTypesList;
	@UiField
	SimplePanel prjListContainer;
	ProjectSelectWidget projectSelectWidget;
	ListBox prjList = null;
	
	DigestService digestService;
	DigestMessages messages;
	DigestConstants constants;
	GlobalResources resources;
	DigestUtils digestUtils;
	protected Runnable onProjectsLoadCallback;
	private Runnable runOnTabSelect;
	
	

	@Inject
	public DigestReportWidget(final DigestMessages messages, final DigestConstants constants, final GlobalResources resources, final DigestService digestService) {
		initWidget(uiBinder.createAndBindUi(this));
		
		resources.globalCSS().ensureInjected();
		this.digestService = digestService;
		this.messages = messages;
		this.constants = constants;
		this.resources = resources;
		this.digestUtils = DigestUtils.getInstance();
		
		reportTypesList.addItem(constants.newWavesLast14Days(),NEW_WAVES);
		reportTypesList.addItem(constants.breakDown4AllTags(),TAGS_BREAKDOWN);
		reportTypesList.setItemSelected(1, true);
		
		onProjectsLoadCallback = new Runnable() {
			
			@Override
			public void run() {
				Log.debug("DigestReportWidget::DigestReportWidget Running");
				initReport();
			}
		};
		
		runOnTabSelect = new Runnable() {
			
			@Override
			public void run() {
				initReportWidget();
				
			}
		};
		
	}


	private void initReportWidget() {
		this.projectSelectWidget = new ProjectSelectWidget(messages, constants, resources, digestService, onProjectsLoadCallback);
		prjListContainer.clear();
		prjListContainer.add(projectSelectWidget);
		prjList = this.projectSelectWidget.getPrjList();
		prjList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				handleOnSelectPrjList(event);
			}
		});
	}
	

	protected void createTagsBreakdownPieChart(DigestMessages messages,
			final DigestConstants constants, GlobalResources resources,
			DigestService digestService2, VerticalPanel reportPanel2 ) {
		try {
			
			digestService2.retrTagsDistributions(prjList.getValue(prjList.getSelectedIndex()) , new AsyncCallback<JSONValue>() {
				@Override
				public void onSuccess(JSONValue result) {
					Map<String,Integer> tagsDistMap = new HashMap<String, Integer>();
					JSONArray tagsJsonArray =  result.isArray();
					int size = tagsJsonArray.size();
					for(int i = 0; i < size; i++){
						JSONValue tagJson = tagsJsonArray.get(i);
						String tagName = tagJson.isObject().get("tag").isString().stringValue();
						Integer tagCount = (int)tagJson.isObject().get("count").isNumber().doubleValue();
						tagsDistMap.put(tagName,tagCount);
					}
					AbstractDataTable dataTable = createTagsBreakdownTable(tagsDistMap);
					Options options = createTagsBreakdownOptions(constants);
					PieChart pie = new PieChart(dataTable,options);
					pie.addSelectHandler(createSelectHandler(pie));
					reportPanel.clear();
					reportPanel.add(pie);
					digestUtils.adjustHeight();
				}
				
				@Override
				public void onFailure(Throwable caught) {
					
				}
			});
		} catch (RequestException e) {
			Log.error("", e);
		}
		
		
	}

	private AbstractDataTable createTagsBreakdownTable(Map<String, Integer> tagsBreakdown) {
		DataTable data = DataTable.create();
		data.addColumn(ColumnType.STRING, "Tag Name");
	    data.addColumn(ColumnType.NUMBER, "Tag Count");
	    
		Set<String> keys = tagsBreakdown.keySet();
		data.addRows(keys.size());
		int row = 0;
		for(String key : keys){
			data.setValue(row, 0, key);
		    data.setValue(row, 1, tagsBreakdown.get(key));
		    row++;
		}
	    return data;
	  }

	private Options createTagsBreakdownOptions(DigestConstants constants) {
	    Options options = Options.create();
	    options.setWidth(constants.basicWidthInt() - 10);
	    options.setHeight(constants.basicReportHeightInt());
	    options.set3D(true);
	    options.setTitle(constants.breakDown4AllTags());
	    return options;
	  }

	  private SelectHandler createSelectHandler(final PieChart chart) {
	    return new SelectHandler() {
	      @Override
	      public void onSelect(SelectEvent event) {
	        String message = "";
	        
	        // May be multiple selections.
	        JsArray<Selection> selections = chart.getSelections();

	        for (int i = 0; i < selections.length(); i++) {
	          // add a new line for each selection
	          message += i == 0 ? "" : "\n";
	          
	          Selection selection = selections.get(i);

	          if (selection.isCell()) {
	            // isCell() returns true if a cell has been selected.
	            
	            // getRow() returns the row number of the selected cell.
	            int row = selection.getRow();
	            // getColumn() returns the column number of the selected cell.
	            int column = selection.getColumn();
	            message += "cell " + row + ":" + column + " selected";
	          } else if (selection.isRow()) {
	            // isRow() returns true if an entire row has been selected.
	            
	            // getRow() returns the row number of the selected row.
	            int row = selection.getRow();
	            message += "row " + row + " selected";
	          } else {
	            // unreachable
	            message += "Pie chart selections should be either row selections or cell selections.";
	            message += "  Other visualizations support column selections as well.";
	          }
	        }
	        
//	        Window.alert(message);
	      }
	    };
	  }
	  
	  protected void drawPostCountsLineChart(DigestMessages messages,
				final DigestConstants constants, GlobalResources resources,
				DigestService digestService2, VerticalPanel reportPanel2 ) {
			try {
				
				digestService2.retrPostCountsT(prjList.getValue(prjList.getSelectedIndex()) , new AsyncCallback<JSONValue>() {
					
					@Override
					public void onSuccess(JSONValue result) {
						Map<String,Integer> tagsDistMap = new TreeMap<String, Integer>();
						JSONArray tagsJsonArray =  result.isArray();
						int size = tagsJsonArray.size();
						for(int i = 0; i < size; i++){
							JSONValue tagJson = tagsJsonArray.get(i);
							String dateStr = tagJson.isObject().get("date").isString().stringValue();
							 Date date = new Date(dateStr);
							dateStr = DateTimeFormat.getShortDateFormat().format(date);    
							Integer postCount = (int)tagJson.isObject().get("count").isNumber().doubleValue();
							tagsDistMap.put(dateStr,postCount);
						}
						AbstractDataTable dataTable = createPostCountsTable(tagsDistMap);
						com.google.gwt.visualization.client.visualizations.LineChart.Options options = createPostCountsOptions(constants);
						LineChart lineChart = new LineChart(dataTable,options);
						lineChart.addSelectHandler(createPostCountsSelectHandler(lineChart));
						reportPanel.clear();
						reportPanel.add(lineChart);
						digestUtils.adjustHeight();
					}
					

					private SelectHandler createPostCountsSelectHandler(
							final LineChart lineChart) {
						//
					    return new SelectHandler() {
						      @Override
						      public void onSelect(SelectEvent event) {
						        String message = "";
						        
						        // May be multiple selections.
						        JsArray<Selection> selections = lineChart.getSelections();

						        for (int i = 0; i < selections.length(); i++) {
						          // add a new line for each selection
						          message += i == 0 ? "" : "\n";
						          
						          Selection selection = selections.get(i);

						          if (selection.isCell()) {
						            // isCell() returns true if a cell has been selected.
						            
						            // getRow() returns the row number of the selected cell.
						            int row = selection.getRow();
						            // getColumn() returns the column number of the selected cell.
						            int column = selection.getColumn();
						            message += "cell " + row + ":" + column + " selected";
						          } else if (selection.isRow()) {
						            // isRow() returns true if an entire row has been selected.
						            
						            // getRow() returns the row number of the selected row.
						            int row = selection.getRow();
						            message += "row " + row + " selected";
						          } else {
						            // unreachable
						            message += "Pie chart selections should be either row selections or cell selections.";
						            message += "  Other visualizations support column selections as well.";
						          }
						        }
						        
//						        Window.alert(message);
						      }
						    };
						  
					}


					private AbstractDataTable createPostCountsTable(Map<String, Integer> tagsBreakdown) {
						DataTable data = DataTable.create();
						data.addColumn(ColumnType.STRING, "Date");
					    data.addColumn(ColumnType.NUMBER, "New Waves");
					    
						Set<String> keys = tagsBreakdown.keySet();
						data.addRows(keys.size());
						int row = 0;
						for(String key : keys){
							data.setValue(row, 0, key);
						    data.setValue(row, 1, tagsBreakdown.get(key));
						    row++;
						}
					    return data;
					  }

					private com.google.gwt.visualization.client.visualizations.LineChart.Options createPostCountsOptions(DigestConstants constants) {
						com.google.gwt.visualization.client.visualizations.LineChart.Options options = com.google.gwt.visualization.client.visualizations.LineChart.Options.create();
					    options.setWidth(constants.basicWidthInt());
					    options.setHeight(constants.basicReportHeightInt());
					    options.setTitle(constants.newWavesLast14Days());
					    return options;
					  }


					@Override
					public void onFailure(Throwable caught) {
						
					}
				});
			} catch (RequestException e) {
				Log.error("", e);
			}
			
			
		}
	  
	  void handleOnSelectPrjList(ChangeEvent event){
		  if(reportTypesList.getValue(reportTypesList.getSelectedIndex()).equals(TAGS_BREAKDOWN) ){
			  reportPanel.clear();
			  reportPanel.add(new HTML(constants.retrieveingDataStr()));
			  createTagsBreakdownPieChart(messages,constants,resources,digestService,reportPanel);
		  }else if(reportTypesList.getValue(reportTypesList.getSelectedIndex()).equals(NEW_WAVES) ){
			  reportPanel.clear();
			  reportPanel.add(new HTML(constants.retrieveingDataStr()));
			  drawPostCountsLineChart(messages,constants,resources,digestService,reportPanel);
		  }
	  }
	  
	  @UiHandler("reportTypesList")
	  void handleOnSelectReportTypesList(ChangeEvent event){
		  handleOnSelectPrjList(event);
	  }


	private void initReport() {
		Runnable onLoadCallback = new Runnable() {
		  public void run() {
		    // Create a pie chart visualization.
//							        PieChart pie = new PieChart(createTable(), createOptions());
			createTagsBreakdownPieChart(messages,constants,resources,digestService,reportPanel);
		  }
		};

		// Load the visualization api, passing the onLoadCallback to be called
		// when loading is done.
		VisualizationUtils.loadVisualizationApi(onLoadCallback, PieChart.PACKAGE,LineChart.PACKAGE );
	}


	@Override
	public Runnable getRunOnTabSelect() {
		return runOnTabSelect;
	}
}