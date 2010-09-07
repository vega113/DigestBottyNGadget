
var JSON_RPC_URL = "/admin/jsonrpc";
var div = document.getElementById('content_div'); 


function stateUpdated() {
	if(!wave.getState().get('totalViewersCounter')) {
		div.innerHTML = "0"
	}
	else {
		div.innerHTML = wave.getState().get('totalViewersCounter');
	} 
}

function init() {
	if (wave && wave.isInWaveContainer()) {
		wave.setStateCallback(stateUpdated);
		setTimeout("participantCallback()",2000);
	}else{
		alert("wave is null in init!")
	}
}

function participantCallback(){
	if (wave && wave.isInWaveContainer() && wave.getState() && wave.getViewer()) {
		
		
		var delta = {};
		var viewerId = wave.getViewer().getId();
		var viewerCounter = parseInt(wave.getState().get(viewerId, '0'));
		var uniqueViewersCounter = parseInt(wave.getState().get('uniqueViewersCounter', '0'));
		var totalViewersCounter = parseInt(wave.getState().get('totalViewersCounter', '0'));
		
		if(viewerCounter == 0){
			// increase unique counter
			uniqueViewersCounter++; 
		}
		
		delta[viewerId] = viewerCounter+1; 
		delta['uniqueViewersCounter'] = uniqueViewersCounter;
		totalViewersCounter++;
		delta['totalViewersCounter'] = totalViewersCounter;
		
		var projectId = wave.getState().get('projectId', 'none');
		var params = {};
		params.projectId = projectId;
		params.userId = viewerId;
		params.waveId = wave.getWaveId();
		params.type = "VIEW_POST";
		params.value = uniqueViewersCounter;
		
		var postData = {};
		postData.method = 'REPORT_POST_VIEW';
		postData.params = params;
		
		var DOMAIN = = wave.getState().get('domain', 'http://digestbotty.appspot.com');
		//use gadgets.io to send
		var url = DOMAIN + JSON_RPC_URL + '?cachebust=' + (new Date()).getTime();
		var params = {};
		params[gadgets.io.RequestParameters.CONTENT_TYPE] = gadgets.io.ContentType.JSON;
		params[gadgets.io.RequestParameters.METHOD]=gadgets.io.MethodType.POST;
		params[gadgets.io.RequestParameters.POST_DATA]= JSON.stringify(postData);
		 
		gadgets.io.makeRequest(url, null, params);
		
		wave.getState().submitDelta(delta);
		
	}
}
gadgets.util.registerOnLoadHandler(init);

// Reset value of "count" to 0
function resetCounter(){
	wave.getState().submitDelta({'viewersCounter': '0'});
}

