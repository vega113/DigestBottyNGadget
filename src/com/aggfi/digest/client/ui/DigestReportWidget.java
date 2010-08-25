package com.aggfi.digest.client.ui;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import com.vegalabs.general.client.utils.VegaUtils;
import com.aggfi.digest.client.constants.ConstantsImpl;
import com.aggfi.digest.client.constants.DigestConstants;
import com.aggfi.digest.client.constants.DigestMessages;
import com.aggfi.digest.client.constants.MessagesImpl;
import com.aggfi.digest.client.resources.GlobalResources;
import com.aggfi.digest.client.service.DigestService;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.LegendPosition;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.Selection;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.events.SelectHandler;
import com.google.gwt.visualization.client.visualizations.BarChart;
import com.google.gwt.visualization.client.visualizations.LineChart;
import com.google.gwt.visualization.client.visualizations.PieChart;
import com.google.gwt.visualization.client.visualizations.PieChart.Options;
import com.google.inject.Inject;

public class DigestReportWidget extends Composite implements RunnableOnTabSelect{

	private static final String NEW_WAVES = "NEW_WAVES";
	private static final String NEW_BLIPS = "NEW_BLIPS";
	private static final String BLIPS_CONTRIBUTORS = "BLIPS_CONTRIBUTORS";
	private static final String CONTRIBUTORS_PER_INFLUENCE = "CONTRIBUTORS_PER_INFLUENCE";
	private static final String TAGS_BREAKDOWN = "TAGS_BREAKDOWN";
	private static final String POSTS_BY_ACTIVITY = "POSTS_BY_ACTIVITY";
	
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
	
	DigestService digestService;
	DigestMessages messages;
	DigestConstants constants;
	MessagesImpl messagesReport;
	ConstantsImpl constantsReport;
	GlobalResources resources;
	private VegaUtils utils;
	protected Runnable onProjectsLoadCallback;
	private Runnable runOnTabSelect;
	
	

	@Inject
	public DigestReportWidget(final DigestMessages messages, final DigestConstants constants,final MessagesImpl messagesReport, final ConstantsImpl constantsReport, final GlobalResources resources, final DigestService digestService, final VegaUtils utils) {
		initWidget(uiBinder.createAndBindUi(this));
		hideAll();
		resources.globalCSS().ensureInjected();
		this.digestService = digestService;
		this.messagesReport = messagesReport;
		this.constantsReport = constantsReport;
		this.messages = messages;
		this.constants = constants;
		this.resources = resources;
		this.utils = utils;
		
		reportTypesList.addItem(constantsReport.newWavesLast14Days(),NEW_WAVES);
		reportTypesList.addItem(constantsReport.newBlipsLast14Days(),NEW_BLIPS);
		reportTypesList.addItem(constantsReport.breakDown4AllTags(),TAGS_BREAKDOWN);
		reportTypesList.addItem(constantsReport.activeContributors14Days(),BLIPS_CONTRIBUTORS);
		reportTypesList.addItem(constantsReport.influenceContributors14Days(),CONTRIBUTORS_PER_INFLUENCE);
		reportTypesList.addItem(constantsReport.postsByActivity14Days(),POSTS_BY_ACTIVITY);
		
		reportTypesList.setItemSelected(0, true);
		
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
					utils.adjustHeight();
			}
		};
		utils.reportPageview("/reportTab/");
	}


	private void hideAll() {
		reportPanel.setVisible(false);
	}


	private void initReportWidget() {
		this.projectSelectWidget =new com.aggfi.digest.client.ui.ProjectSelectWidget(messages, constants, resources, digestService, onProjectsLoadCallback,utils );
		prjListContainer.clear();
		prjListContainer.add(projectSelectWidget);
		utils.reportPageview("/reportTab/");
	}
	
	//start TagsBreakdown
	protected void createTagsBreakdownPieChart(MessagesImpl messages,
			final ConstantsImpl constants, GlobalResources resources,
			DigestService digestService2, VerticalPanel reportPanel2 ) {
		try {
			String msg = messages.loadingForumsMsg(constants.reportTabStr(), getProjectName());
			Log.debug(msg);
			if(getProjectId() != null && !"".equals(getProjectId())){
				utils.showStaticMessage(msg);
				digestService2.retrTagsDistributions(getProjectId() , new AsyncCallback<JSONValue>() {
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
						reportPanel.setVisible(true);
						utils.dismissStaticMessage();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						utils.dismissStaticMessage();
						utils.alert(caught.getMessage());
					}
				});
			}
			
		} catch (RequestException e) {
			Log.error("", e);
			utils.dismissStaticMessage();
			utils.alert(e.getMessage());
		}
		
		utils.reportEvent("/report/select/","createTagsBreakdownPieChart", getProjectId(), 1);
	}



	private String getProjectId() {
		return projectSelectWidget.getPrjList().getValue(projectSelectWidget.getPrjList().getSelectedIndex());
	}
	private String getProjectName() {
		return projectSelectWidget.getPrjList().getItemText (projectSelectWidget.getPrjList().getSelectedIndex());
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

	private Options createTagsBreakdownOptions(ConstantsImpl constants) {
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
	  //end TagsBreakdown
	  
	  //start PostCounts
	  protected void drawNewWavesLineChart(MessagesImpl messages,
				final ConstantsImpl constants, GlobalResources resources,
				DigestService digestService2, VerticalPanel reportPanel2 ) {
			try {
				String msg = messages.loadingForumsMsg(constants.reportTabStr(), getProjectName());
				Log.debug(msg);
				utils.showStaticMessage(msg);
				digestService2.retrPostCounts(getProjectId() , new AsyncCallback<JSONValue>() {
					
					@SuppressWarnings("deprecation")
					@Override
					public void onSuccess(JSONValue result) {
						Map<Date,Integer> tagsDistMap = new TreeMap<Date, Integer>();
						JSONArray tagsJsonArray =  result.isArray();
						int size = tagsJsonArray.size();
						for(int i = 0; i < size; i++){
							JSONValue tagJson = tagsJsonArray.get(i);
							String dateStr = tagJson.isObject().get("date").isString().stringValue();
							 Date date = new Date(dateStr);
							Integer postCount = (int)tagJson.isObject().get("count").isNumber().doubleValue();
							Log.info(date.toString() + " : " + postCount);
							tagsDistMap.put(date,postCount);
						}
						AbstractDataTable dataTable = createPostCountsTable(tagsDistMap);
						com.google.gwt.visualization.client.visualizations.LineChart.Options options = createPostCountsOptions(constants);
						LineChart lineChart = new LineChart(dataTable,options);
						lineChart.addSelectHandler(createPostCountsSelectHandler(lineChart));
						reportPanel.clear();
						reportPanel.setVisible(true);
						reportPanel.add(lineChart);
						utils.dismissStaticMessage();
					}

					@Override
					public void onFailure(Throwable caught) {
						utils.dismissStaticMessage();
						utils.alert(caught.getMessage());
					}
				});
			} catch (IllegalArgumentException e) {
				utils.dismissStaticMessage();
//				digestUtils.alert(e.getMessage());
				Log.error("", e);
			} catch (RequestException e) {
				utils.dismissStaticMessage();
				utils.alert(e.getMessage());
				Log.error("", e);
			}
			
			utils.reportEvent("/report/select/","drawNewWavesLineChart", getProjectId(), 1);
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
			        
//			        Window.alert(message);
			      }
			    };
			  
		}


		private AbstractDataTable createPostCountsTable(Map<Date, Integer> tagsBreakdown) {
			DataTable data = DataTable.create();
			data.addColumn(ColumnType.DATE, "Date");
		    data.addColumn(ColumnType.NUMBER, "New Waves");
		    
			Set<Date> keys = tagsBreakdown.keySet();
			data.addRows(keys.size());
			int row = 0;
			for(Date key : keys){
				data.setValue(row, 0, key);
			    data.setValue(row, 1, tagsBreakdown.get(key));
			    row++;
			}
		    return data;
		  }

		private com.google.gwt.visualization.client.visualizations.LineChart.Options createPostCountsOptions(ConstantsImpl constants) {
			com.google.gwt.visualization.client.visualizations.LineChart.Options options = com.google.gwt.visualization.client.visualizations.LineChart.Options.create();
		    options.setWidth(constants.basicWidthInt());
		    options.setHeight(constants.basicReportHeightInt());
		    options.setTitle(constants.newWavesLast14Days());
		    options.setLegend(LegendPosition.NONE);
		    return options;
		  }
		//end PostCounts
		
		//start BlipsPerContributor
		protected void drawBlipsPerContributorHorizontalBar(MessagesImpl messages,
				final ConstantsImpl constants, GlobalResources resources,
				DigestService digestService2, VerticalPanel reportPanel2 ) {
			try {
				String msg = messages.loadingForumsMsg(constants.reportTabStr(), getProjectName());
				Log.debug(msg);
				utils.showStaticMessage(msg);
				digestService2.retrBlipsPerContributor(getProjectId() , new AsyncCallback<JSONValue>() {
					
					@SuppressWarnings("deprecation")
					@Override
					public void onSuccess(JSONValue result) {
						Map<String,Integer> blipsDistMap = new HashMap<String, Integer>();
						JSONArray blipsJsonArray =  result.isArray();
						
						AbstractDataTable dataTable = createBlipsPerContributorTable(blipsJsonArray);
						com.google.gwt.visualization.client.visualizations.BarChart.Options options = createBlipsPerContributorOptions(constants);
						BarChart barChart = new BarChart(dataTable,options);
						barChart.addSelectHandler(createBlipsPerContributorSelectHandler(barChart));
						reportPanel.clear();
						reportPanel.setVisible(true);
						reportPanel.add(barChart);
						utils.dismissStaticMessage();
					}

					@Override
					public void onFailure(Throwable caught) {
						utils.dismissStaticMessage();
						utils.alert(caught.getMessage());
					}
				});
			} catch (IllegalArgumentException e) {
				utils.dismissStaticMessage();
//				digestUtils.alert(e.getMessage());
				Log.error("", e);
			} catch (RequestException e) {
				utils.dismissStaticMessage();
				utils.alert(e.getMessage());
				Log.error("", e);
			}
			
			utils.reportEvent("/report/select/","drawActiveContributorsBarChart", getProjectId(), 1);
		}
	  private SelectHandler createBlipsPerContributorSelectHandler(
				final BarChart barChart) {
			//
		    return new SelectHandler() {
			      @Override
			      public void onSelect(SelectEvent event) {
			        String message = "";
			        
			        // May be multiple selections.
			        JsArray<Selection> selections = barChart.getSelections();

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
			        
//			        Window.alert(message);
			      }
			    };
			  
		}


		private AbstractDataTable createBlipsPerContributorTable(JSONArray blipsJsonArray) {
			DataTable data = DataTable.create();
			data.addColumn(ColumnType.STRING, "Contributor");
		    data.addColumn(ColumnType.NUMBER, "Blips");
		    
		    int size = blipsJsonArray.size();
		    data.addRows(size);
		    for(int i = 0; i < size; i++){
				JSONValue participantJson = blipsJsonArray.get(i);
				String dateStr = participantJson.isObject().get("participant").isString().stringValue();
				int postCount = (int)participantJson.isObject().get("count").isNumber().doubleValue();
				data.setValue(i, 0, dateStr);
			    data.setValue(i, 1, postCount);
				
			}
		    
		    return data;
		  }

		private com.google.gwt.visualization.client.visualizations.BarChart.Options createBlipsPerContributorOptions(ConstantsImpl constants) {
			com.google.gwt.visualization.client.visualizations.BarChart.Options options = com.google.gwt.visualization.client.visualizations.BarChart.Options.create();
		    options.setWidth(constants.basicWidthInt());
		    options.setHeight(constants.basicReportHeightInt());
		    options.setTitle(constants.activeContributors14Days());
		    options.setLegend(LegendPosition.NONE);
		    options.set3D(true);
		    return options;
		  }
		//end BlipsPerContributor
		
		
		//start PostsByActivity
		protected void drawPostsByActivityHorizontalBar(MessagesImpl messages,
				final ConstantsImpl constants, GlobalResources resources,
				DigestService digestService2, VerticalPanel reportPanel2 ) {
			try {
				String msg = messages.loadingForumsMsg(constants.reportTabStr(), getProjectName());
				Log.debug(msg);
				utils.showStaticMessage(msg);
				digestService2.retrPostsByActivity(getProjectId() , new AsyncCallback<JSONValue>() {
					
					@SuppressWarnings("deprecation")
					@Override
					public void onSuccess(JSONValue result) {
						Map<String,Integer> blipsDistMap = new HashMap<String, Integer>();
						JSONArray blipsJsonArray =  result.isArray();
						
						AbstractDataTable dataTable = createPostsByActivityTable(blipsJsonArray);
						com.google.gwt.visualization.client.visualizations.BarChart.Options options = createPostsByActivityOptions(constants);
						BarChart barChart = new BarChart(dataTable,options);
						barChart.addSelectHandler(createPostsByActivitySelectHandler(barChart));
						reportPanel.clear();
						reportPanel.setVisible(true);
						reportPanel.add(barChart);
						utils.dismissStaticMessage();
					}

					@Override
					public void onFailure(Throwable caught) {
						utils.dismissStaticMessage();
						utils.alert(caught.getMessage());
					}
				});
			} catch (IllegalArgumentException e) {
				utils.dismissStaticMessage();
//				digestUtils.alert(e.getMessage());
				Log.error("", e);
			} catch (RequestException e) {
				utils.dismissStaticMessage();
				utils.alert(e.getMessage());
				Log.error("", e);
			}
			
			utils.reportEvent("/report/select/","drawPostsByActivityBarChart", getProjectId(), 1);
		}
	  private SelectHandler createPostsByActivitySelectHandler(
				final BarChart barChart) {
			//
		    return new SelectHandler() {
			      @Override
			      public void onSelect(SelectEvent event) {
			        String message = "";
			        
			        // May be multiple selections.
			        JsArray<Selection> selections = barChart.getSelections();

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
			        
//			        Window.alert(message);
			      }
			    };
			  
		}


		private AbstractDataTable createPostsByActivityTable(JSONArray blipsJsonArray) {
			DataTable data = DataTable.create();
			data.addColumn(ColumnType.STRING, "Post");
		    data.addColumn(ColumnType.NUMBER, "Blips");
		    
		    int size = blipsJsonArray.size();
		    data.addRows(size);
		    for(int i = 0; i < size; i++){
				JSONValue participantJson = blipsJsonArray.get(i);
				String title = participantJson.isObject().get("title").isString().stringValue();
				if(title.length() > 35){
					title = title.substring(0, 32) + "...";
				}
				int postCount = (int)participantJson.isObject().get("count").isNumber().doubleValue();
				data.setValue(i, 0, title);
			    data.setValue(i, 1, postCount);
				
			}
		    
		    return data;
		  }

		private com.google.gwt.visualization.client.visualizations.BarChart.Options createPostsByActivityOptions(ConstantsImpl constants) {
			com.google.gwt.visualization.client.visualizations.BarChart.Options options = com.google.gwt.visualization.client.visualizations.BarChart.Options.create();
		    options.setWidth(constants.basicWidthInt()-10);//TODO there's problem with left edge, need to fix it some how.
		    options.setHeight(constants.basicReportHeightInt());
		    options.setTitle(constants.postsByActivity14Days());
		    options.setLegend(LegendPosition.NONE);
		    options.set3D(true);
		    return options;
		  }
		//end PostsByActivity
		
		
		
		//start ContributorsPerInfluence
		protected void drawContributorsPerInfluenceHorizontalBar(MessagesImpl messages,
				final ConstantsImpl constants, GlobalResources resources,
				DigestService digestService2, VerticalPanel reportPanel2 ) {
			try {
				String msg = messages.loadingForumsMsg(constants.reportTabStr(), getProjectName());
				Log.debug(msg);
				utils.showStaticMessage(msg);
				digestService2.retrContributorsPerInfluence(getProjectId() , new AsyncCallback<JSONValue>() {
					
					@SuppressWarnings("deprecation")
					@Override
					public void onSuccess(JSONValue result) {
						Map<String,Integer> blipsDistMap = new HashMap<String, Integer>();
						JSONArray blipsJsonArray =  result.isArray();
						
						AbstractDataTable dataTable = createContributorsPerInfluenceTable(blipsJsonArray);
						com.google.gwt.visualization.client.visualizations.BarChart.Options options = createContributorsPerInfluenceOptions(constants);
						BarChart barChart = new BarChart(dataTable,options);
						barChart.addSelectHandler(createContributorsPerInfluenceSelectHandler(barChart));
						reportPanel.clear();
						reportPanel.setVisible(true);
						reportPanel.add(barChart);
						utils.dismissStaticMessage();
					}

					@Override
					public void onFailure(Throwable caught) {
						utils.dismissStaticMessage();
						utils.alert(caught.getMessage());
					}
				});
			} catch (IllegalArgumentException e) {
				utils.dismissStaticMessage();
//				digestUtils.alert(e.getMessage());
				Log.error("", e);
			} catch (RequestException e) {
				utils.dismissStaticMessage();
				utils.alert(e.getMessage());
				Log.error("", e);
			}
			
			utils.reportEvent("/report/select/","drawContributorsPerInfluenceBarChart", getProjectId(), 1);
		}
	  private SelectHandler createContributorsPerInfluenceSelectHandler(
				final BarChart barChart) {
			//
		    return new SelectHandler() {
			      @Override
			      public void onSelect(SelectEvent event) {
			        String message = "";
			        
			        // May be multiple selections.
			        JsArray<Selection> selections = barChart.getSelections();

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
			        
//			        Window.alert(message);
			      }
			    };
			  
		}


		private AbstractDataTable createContributorsPerInfluenceTable(JSONArray blipsJsonArray) {
			DataTable data = DataTable.create();
			data.addColumn(ColumnType.STRING, "Contributor");
		    data.addColumn(ColumnType.NUMBER, "Influence");
		    
		   if(blipsJsonArray != null){
			   int size = blipsJsonArray.size();
			    data.addRows(size);
			    for(int i = 0; i < size; i++){
					JSONValue participantJson = blipsJsonArray.get(i);
					String dateStr = participantJson.isObject().get("participant").isString().stringValue();
					int postCount = (int)participantJson.isObject().get("influence").isNumber().doubleValue();
					data.setValue(i, 0, dateStr);
				    data.setValue(i, 1, postCount);
					
				}
		   }
		    
		    return data;
		  }

		private com.google.gwt.visualization.client.visualizations.BarChart.Options createContributorsPerInfluenceOptions(ConstantsImpl constants) {
			com.google.gwt.visualization.client.visualizations.BarChart.Options options = com.google.gwt.visualization.client.visualizations.BarChart.Options.create();
		    options.setWidth(constants.basicWidthInt());
		    options.setHeight(constants.basicReportHeightInt());
		    options.setTitle(constants.influenceContributors14Days());
		    options.setLegend(LegendPosition.NONE);
		    options.set3D(true);
		    return options;
		  }
		//end ContributorsPerInfluence
		
		
		//start BlipsCounts
		protected void drawBlipsCountsLineChart(MessagesImpl messages,
				final ConstantsImpl constants, GlobalResources resources,
				DigestService digestService2, VerticalPanel reportPanel2 ) {
			try {
				String msg = messages.loadingForumsMsg(constants.reportTabStr(), getProjectName());
				Log.debug(msg);
				utils.showStaticMessage(msg);
				digestService2.retrBlipsCounts(getProjectId() , new AsyncCallback<JSONValue>() {
					
					@SuppressWarnings("deprecation")
					@Override
					public void onSuccess(JSONValue result) {
						Map<Date,Integer> blipsDistMap = new TreeMap<Date, Integer>();
						JSONArray blipsJsonArray =  result.isArray();
						int size = blipsJsonArray.size();
						for(int i = 0; i < size; i++){
							JSONValue tagJson = blipsJsonArray.get(i);
							String dateStr = tagJson.isObject().get("date").isString().stringValue();
							 Date date = new Date(dateStr);
							Integer postCount = (int)tagJson.isObject().get("count").isNumber().doubleValue();
							Log.info(date.toString() + " : " + postCount);
							blipsDistMap.put(date,postCount);
						}
						AbstractDataTable dataTable = createBlipsCountsTable(blipsDistMap);
						com.google.gwt.visualization.client.visualizations.LineChart.Options options = createBlipsCountsOptions(constants);
						LineChart lineChart = new LineChart(dataTable,options);
						lineChart.addSelectHandler(createBlipsCountsSelectHandler(lineChart));
						reportPanel.clear();
						reportPanel.setVisible(true);
						reportPanel.add(lineChart);
						utils.dismissStaticMessage();
					}

					@Override
					public void onFailure(Throwable caught) {
						utils.dismissStaticMessage();
						utils.alert(caught.getMessage());
					}
				});
			} catch (IllegalArgumentException e) {
				utils.dismissStaticMessage();
//				digestUtils.alert(e.getMessage());
				Log.error("", e);
			} catch (RequestException e) {
				utils.dismissStaticMessage();
				utils.alert(e.getMessage());
				Log.error("", e);
			}
			
			utils.reportEvent("/report/select/","drawNewBlipsLineChart", getProjectId(), 1);
		}
	  private SelectHandler createBlipsCountsSelectHandler(
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
			        
//			        Window.alert(message);
			      }
			    };
			  
		}


		private AbstractDataTable createBlipsCountsTable(Map<Date, Integer> blipsMap) {
			DataTable data = DataTable.create();
			data.addColumn(ColumnType.DATE, "Date");
		    data.addColumn(ColumnType.NUMBER, "Blips submitted/edited");
		    
			Set<Date> keys = blipsMap.keySet();
			data.addRows(keys.size());
			int row = 0;
			for(Date key : keys){
				data.setValue(row, 0, key);
			    data.setValue(row, 1, blipsMap.get(key));
			    row++;
			}
		    return data;
		  }

		private com.google.gwt.visualization.client.visualizations.LineChart.Options createBlipsCountsOptions(ConstantsImpl constants) {
			com.google.gwt.visualization.client.visualizations.LineChart.Options options = com.google.gwt.visualization.client.visualizations.LineChart.Options.create();
		    options.setWidth(constants.basicWidthInt());
		    options.setHeight(constants.basicReportHeightInt());
		    options.setTitle(constants.newBlipsLast14Days());
		    options.setLegend(LegendPosition.NONE);
		    return options;
		  }
		
		//end BlipsCounts
	  
		void handleOnSelectPrjList(ChangeEvent event){
			hideAll();
			if(getProjectId() != null && !"".equals(getProjectId())){
				reportTypesList.setEnabled(true);
				reportPanel.clear();
				if(reportTypesList.getValue(reportTypesList.getSelectedIndex()).equals(TAGS_BREAKDOWN) ){
					createTagsBreakdownPieChart(messagesReport,constantsReport,resources,digestService,reportPanel);
				}else if(reportTypesList.getValue(reportTypesList.getSelectedIndex()).equals(NEW_WAVES) ){
					drawNewWavesLineChart(messagesReport,constantsReport,resources,digestService,reportPanel);
				}else if(reportTypesList.getValue(reportTypesList.getSelectedIndex()).equals(NEW_BLIPS) ){
					drawBlipsCountsLineChart(messagesReport,constantsReport,resources,digestService,reportPanel);
				}else if(reportTypesList.getValue(reportTypesList.getSelectedIndex()).equals(BLIPS_CONTRIBUTORS) ){
					drawBlipsPerContributorHorizontalBar(messagesReport,constantsReport,resources,digestService,reportPanel);
				}else if(reportTypesList.getValue(reportTypesList.getSelectedIndex()).equals(CONTRIBUTORS_PER_INFLUENCE) ){
					drawContributorsPerInfluenceHorizontalBar (messagesReport,constantsReport,resources,digestService,reportPanel);
				}else if(reportTypesList.getValue(reportTypesList.getSelectedIndex()).equals(POSTS_BY_ACTIVITY) ){
					drawPostsByActivityHorizontalBar (messagesReport,constantsReport,resources,digestService,reportPanel);
				}
				
				
			}else{
				reportTypesList.setEnabled(false);
				utils.alert(constants.noForumSelectedWarning());
			}
		}
	  
	  @UiHandler("reportTypesList")
	  void handleOnSelectReportTypesList(ChangeEvent event){
		  handleOnSelectPrjList(event);
	  }


	private void initReport() {
		Runnable onLoadCallback = new Runnable() {
		  public void run() {
			drawNewWavesLineChart(messagesReport,constantsReport,resources,digestService,reportPanel);
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
	@Override
	public String getName(){
		return "report";
	}
}
