var report = report || {};

/* for non-modern browser without Array.indexOf() support */
if (!Array.indexOf) {
  Array.prototype.indexOf = function(arg) {
    var index = -1;
    for (var i = 0; i < this.length; i++){
      var value = this[i];
      if (value == arg) {
        index = i;
        break;
      } 
    }
    return index;
  }
}

report.init = function() {
  report.URL_PARAMS = report.getUrlParams();
  report.projectId = report.URL_PARAMS.id;

  report.getTagCounts( function(json) {
    report.drawTagsColumnChart('allTags', json);
    report.drawTagsPieChart('apiTags', json, null, ['gadget', 'robot', 'embed']);
    report.drawTagsPieChart('langTags', json, null, ['java', 'python']);
  });
  
  report.getPostCounts(14, function(json) {  
    report.drawPostCountsLineChart('newPosts', json);
  });
};

report.drawPostCountsLineChart = function(elementId, json, title) {
  var data = new google.visualization.DataTable();  
  data.addColumn('string', 'Date');
  data.addColumn('number', 'New Waves');
  data.addRows(json.length);
  
  for ( var i = 0; i < json.length; i++) {
    var date = new Date(json[i].date);    
    var dateString = date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + (date.getDate() + 1);    
    var count = json[i].count;
    
    data.setValue(i, 0, dateString);
    data.setValue(i, 1, count);
  }

  var options = {};
  options.width = 600;
  options.height = 400;
  options.is3D = true;
  options.title = title;  
  
  var chart = new google.visualization.LineChart(document.getElementById(elementId));
  chart.draw(data, options);  
};

report.drawTagsColumnChart = function(elementId, json, title, filters) {
  var data = new google.visualization.DataTable();  
  data.addColumn('string', 'Tag');
  data.addColumn('number', 'Tag Count');
  
  data.addRows(json.length);
  
  for ( var i = 0; i < json.length; i++) {
    var tag = json[i].tag.toLowerCase();
    var count = json[i].count;
    
    data.setValue(i, 0, tag);
    data.setValue(i, 1, count);    
  }

  var options = {};
  options.width = 600;
  options.height = 400;
  options.is3D = true;
  options.title = title;  
  
  var chart = new google.visualization.ColumnChart(document.getElementById(elementId));
  chart.draw(data, options);  
};

report.drawTagsPieChart = function(elementId, json, title, filters) {
  var data = new google.visualization.DataTable();
  data.addColumn('string', 'Tag');
  data.addColumn('number', 'Count');
  
  if (filters != null) {  
    data.addRows(filters.length);
  } else {
    data.addRows(json.length);    
  }
  
  var row = 0;  
  for ( var i = 0; i < json.length; i++) {
    var tag = json[i].tag.toLowerCase();
    var count = json[i].count;
    
    if (filters != null) {    
      if (filters.indexOf(tag) > -1) {
        data.setValue(row, 0, tag);
        data.setValue(row, 1, count);
        row++;
      }
    } else {
      data.setValue(i, 0, tag);
      data.setValue(i, 1, count);       
    }
  }
  
  var options = {};
  options.width = 270;
  options.height = 270;
  options.is3D = true;
  options.title = title;
  
  new google.visualization.PieChart(
      document.getElementById(elementId)).draw(data, options);  
};

report.getTagCounts = function(callback) {
  var command = 'GET_TAG_COUNTS';
  var params = {};
  params.projectId = report.projectId;

  var callback_ = function(jsonStr) {
    var json = JSON.parse(jsonStr);
    if (!json.error) {
      // do something      
      report.clearStatus();
      callback(json.result);
    } else {
      report.setStatus(json.error);
    }
  };
  report.setStatus('processing ...');
  jsonrpc.makeRequest(command, params, callback_);
};

report.getPostCounts = function(days, callback) {
  var command = 'GET_POST_COUNTS';
  var params = {};
  params.projectId = report.projectId;
  params.target = (new Date()).getTime();
  params.days = days;

  var callback_ = function(jsonStr) {
    var json = JSON.parse(jsonStr);
    if (!json.error) {
      // do something      
      report.clearStatus();
      callback(json.result);
    } else {
      report.setStatus(json.error);
    }
  };
  report.setStatus('processing ...');
  jsonrpc.makeRequest(command, params, callback_);
};

report.setStatus = function(msg) {
  jQuery('#status').html('&nbsp;' + msg + '&nbsp;');
};

report.clearStatus = function() {
  jQuery('#status').empty();
};

report.getUrlParams = function() {
  var args = new Object();
  var params = window.location.href.split('?');

  if (params.length > 1) {
    params = params[1];
    var pairs = params.split("&");
    for ( var i = 0; i < pairs.length; i++) {
      var pos = pairs[i].indexOf('=');
      if (pos == -1)
        continue;
      var argname = pairs[i].substring(0, pos);
      var value = pairs[i].substring(pos + 1);
      value = value.replace(/\+/g, " ");
      args[argname] = value;
    }
  }
  return args;
};

google.load('visualization', '1', {
  packages : [ 'piechart', 'columnchart', 'linechart']
});
google.setOnLoadCallback(report.init);