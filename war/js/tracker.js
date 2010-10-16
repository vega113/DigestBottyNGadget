


var JSON_RPC_URL = "/admin/jsonrpc";

function init() {
	if (wave && wave.isInWaveContainer()) {
		setTimeout("participantCallback()",2000);
	}else{
		alert("wave is null in init!")
	}
}

function participantCallback(){
	if (wave && wave.isInWaveContainer() && wave.getState() && wave.getViewer()) {
		var viewerId = wave.getViewer().getId();
		var methodName = 'REPORT_POST_VIEW'; 
		
		
		var projectId = wave.getState().get('projectId', 'none');
		var domain = wave.getState().get('domain', 'http://digestbotty.appspot.com');
		var p = {};
		p.projectId = projectId;
		p.userId = viewerId;
		p.waveId = wave.getWaveId();
		p.type = wave.getState().get('eventType', 'VIEW_POST');
		p.value = wave.getState().get('eventValue', 'posttracker'); //viewer
		
		var postData = {};
		postData.method = 'REPORT_POST_VIEW';
		postData.params = p;
		
		//use gadgets.io to send
		var url = domain + JSON_RPC_URL + '?cachebust=' + (new Date()).getTime();
		var params = {};
		params[gadgets.io.RequestParameters.CONTENT_TYPE] = gadgets.io.ContentType.JSON;
		params[gadgets.io.RequestParameters.METHOD]=gadgets.io.MethodType.POST;
		params[gadgets.io.RequestParameters.POST_DATA]= JSON.stringify(postData);
		 
		gadgets.io.makeRequest(url, null, params);
		
	}
}
gadgets.util.registerOnLoadHandler(init);


