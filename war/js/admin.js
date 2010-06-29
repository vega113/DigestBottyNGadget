var admin = admin || {};

jQuery(window.document).ready(function() {  
  admin.init();
});

admin.init = function() {
  admin.URL_PARAMS = admin.getUrlParams();
  
  admin.projectId = admin.URL_PARAMS.id;  
  
  admin.getAdminConfig(function(json) {
    for (var i=0; i<json.defaultParticipants.length; i++) {
      admin.appendDefaultParticipantsList(json.defaultParticipants[i]);
    }    
    
    for (var i=0; i<json.defaultTags.length; i++) {
      admin.appendDefaultTagsList(json.defaultTags[i]);
    }
    
    for (tag in json.autoTagRegexMap) {      
      var regex = json.autoTagRegexMap[tag];      
      admin.appendAutoTagsList(tag, regex);
    }          
  });
  
  jQuery('#addDefaultParticipant').click(function() {
    var participantId = jQuery('#defaultParticipantId').val();
    
    admin.addDefaultParticipant(participantId, function() {
      admin.appendDefaultParticipantsList(participantId);
    });
  });    
  
  jQuery('#addDefaultTag').click(function() {
    var tag = jQuery('#defaultTag').val();
    
    admin.addDefaultTag(tag, function() {
      admin.appendDefaultTagsList(tag);
    });
  });  
  
  jQuery('#addAutoTag').click(function() {
    var tag = jQuery('#autoTag').val();
    var regex = jQuery('#autoTagRegex').val();
    
    admin.addAutoTag(tag, regex, function() {
      admin.appendAutoTagsList(tag, regex);
    });
  });    
};

admin.appendDefaultParticipantsList = function(participantId) {
  var listDiv = jQuery('#defaultParticipantsList');
  var div = jQuery('<div/>');
  
  var deleteImg = jQuery('<img style="verticial-align: middle;" src="images/close.jpg"/>');
  
  deleteImg.click(function() {
    if (!confirm('Delete "' + participantId + '"?')) {
      return;
    }       
    admin.removeDefaultParticipant(participantId, function() {
      div.remove();
    });            
  });        
  
  div.append(deleteImg);
  div.append(' ' + participantId);
    
  listDiv.append(div);
};

admin.appendDefaultTagsList = function(tag) {
  var listDiv = jQuery('#defaultTagsList');
  var div = jQuery('<div/>');
  
  var deleteImg = jQuery('<img style="verticial-align: middle;" src="images/close.jpg"/>');
  
  deleteImg.click(function() {
    if (!confirm('Delete "' + tag + '"?')) {
      return;
    }       
    admin.removeDefaultTag(tag, function() {
      div.remove();
    });            
  });        
  
  div.append(deleteImg);
  div.append(' ' + tag);
    
  listDiv.append(div);
};

admin.appendAutoTagsList = function(tag, regex) {
  var listDiv = jQuery('#autoTagsList');
  var div = jQuery('<div/>');
  
  var deleteImg = jQuery('<img style="verticial-align: middle;" src="images/close.jpg"/>');
  
  deleteImg.click(function() {        
    if (!confirm('Delete "tag=' + tag + ' regx=' + regex + '"?')) {
      return;
    }    
    admin.removeAutoTag(tag, function() {
      div.remove();
    });            
  });        
  
  div.append(deleteImg);
  div.append(' tag=' + tag + '&nbsp;&nbsp;&nbsp;regex=' + regex);
    
  listDiv.append(div);
};

admin.getAdminConfig = function(callback) {
  var command = 'GET_ADMIN_CONFIG';        
  var params = {};
  params.projectId = admin.projectId;
  
  var callback_ = function(jsonStr) {
    var json = JSON.parse(jsonStr);
    if (!json.error) {
      admin.clearStatus();
      callback(json.result);
    } else {
      admin.setStatus(json.error);
    }
  };
  admin.setStatus('processing ...');
  jsonrpc.makeRequest(command, params, callback_);
};

admin.addDefaultParticipant = function(participantId, callback) {
  var command = 'ADD_DEFAULT_PARTICIPANT';        
  var params = {};
  params.projectId = admin.projectId;
  params.participantId = participantId;
  
  var callback_ = function(jsonStr) {
    var json = JSON.parse(jsonStr);
    if (!json.error) {
      jQuery('#defaultParticipantId').val('');
      admin.clearStatus();
      callback();
    } else {
      admin.setStatus(json.error);
    }
  };
  admin.setStatus('processing ...');
  jsonrpc.makeRequest(command, params, callback_);
};

admin.removeDefaultParticipant = function(participantId, callback) {
  var command = 'REMOVE_DEFAULT_PARTICIPANT';        
  var params = {};
  params.projectId = admin.projectId;
  params.participantId = participantId;
  
  var callback_ = function(jsonStr) {
    var json = JSON.parse(jsonStr);
    if (!json.error) {
      admin.clearStatus();
      callback();
    } else {
      admin.setStatus(json.error);
    }
  };
  admin.setStatus('processing ...');
  jsonrpc.makeRequest(command, params, callback_);
};

admin.addDefaultTag = function(tag, callback) {
  var command = 'ADD_DEFAULT_TAG';        
  var params = {};
  params.projectId = admin.projectId;
  params.tag = tag;
  
  var callback_ = function(jsonStr) {
    var json = JSON.parse(jsonStr);
    if (!json.error) {
      jQuery('#defaultTag').val('');
      admin.clearStatus();
      callback();
    } else {
      admin.setStatus(json.error);
    }
  };
  admin.setStatus('processing ...');
  jsonrpc.makeRequest(command, params, callback_);
};

admin.removeDefaultTag = function(tag, callback) {
  var command = 'REMOVE_DEFAULT_TAG';        
  var params = {};
  params.projectId = admin.projectId;
  params.tag = tag;
  
  var callback_ = function(jsonStr) {
    var json = JSON.parse(jsonStr);
    if (!json.error) {
      admin.clearStatus();
      callback();
    } else {
      admin.setStatus(json.error);
    }
  };
  admin.setStatus('processing ...');
  jsonrpc.makeRequest(command, params, callback_);
};

admin.addAutoTag = function(tag, regex, callback) {
  var command = 'ADD_AUTO_TAG';        
  var params = {};
  params.projectId = admin.projectId;
  params.tag = tag;
  params.regex = regex;
  
  var callback_ = function(jsonStr) {
    var json = JSON.parse(jsonStr);
    if (!json.error) {
      jQuery('#autoTag').val('');
      jQuery('#autoTagRegex').val('');
      admin.clearStatus();
      callback();
    } else {
      admin.setStatus(json.error);
    }
  };
  admin.setStatus('processing ...');
  jsonrpc.makeRequest(command, params, callback_);
};

admin.removeAutoTag = function(tag, callback) {
  var command = 'REMOVE_AUTO_TAG';        
  var params = {};
  params.projectId = admin.projectId;
  params.tag = tag;
  
  var callback_ = function(jsonStr) {
    var json = JSON.parse(jsonStr);
    if (!json.error) {
      admin.clearStatus();
      callback();
    } else {
      admin.setStatus(json.error);
    }
  };
  admin.setStatus('processing ...');
  jsonrpc.makeRequest(command, params, callback_);
};

admin.setStatus = function(msg) {
  jQuery('#status').html('&nbsp;' + msg + '&nbsp;');
};

admin.clearStatus = function() {
  jQuery('#status').empty();
};

admin.getUrlParams = function() {
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
