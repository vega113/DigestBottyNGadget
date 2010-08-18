<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<!--<meta http-equiv="X-UA-Compatible" content="chrome=1">-->

<title>Embedded Wave by DigestBotty</title>
<script src="http://www.google.com/jsapi" type="text/javascript"></script>
<script type="text/javascript">
	var waveIdToLoad = null;
	var wavePanel = null;
    google.load("wave", "1");

    function initialize() {
      var waveFrame = document.getElementById("waveframe");
      var embedOptions = {
        target: waveFrame,
        header: true,
        toolbar: true
              
      }
      if(wavePanel == null){
    	  wavePanel = new google.wave.WavePanel(embedOptions);
      }
     
      if(waveIdToLoad == null){
    	  var idWave = "<%=request != null && request.getParameter("waveId") != null ? request.getParameter("waveId") : ""%>";
          if(idWave != null && idWave != ""){
        	  waveIdToLoad = idWave;
        	  wavePanel.loadWave(waveIdToLoad);
          }
      }else if(waveIdToLoad != ""){
    	  wavePanel.loadWave(waveIdToLoad);
      }
      
    }
    google.setOnLoadCallback(initialize);
</script>

<script type="text/javascript" src="js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="js/json2.js"></script>
<script type="text/javascript" src="js/jsonrpc.js"></script>
<link rel="stylesheet" href="http://dev.jquery.com/view/trunk/plugins/autocomplete/demo/main.css" type="text/css" />
  <link rel="stylesheet" href="http://dev.jquery.com/view/trunk/plugins/autocomplete/jquery.autocomplete.css" type="text/css" />
  <script type="text/javascript" src="http://dev.jquery.com/view/trunk/plugins/autocomplete/lib/jquery.bgiframe.min.js"></script>
  <script type="text/javascript" src="http://dev.jquery.com/view/trunk/plugins/autocomplete/lib/jquery.dimensions.js"></script>
  <script type="text/javascript" src="http://dev.jquery.com/view/trunk/plugins/autocomplete/jquery.autocomplete.js"></script>
<script type="text/javascript">
var prjsMap = null;
var json = null;
jQuery(document).ready(function() {  
  jQuery('#submitForumId').click(function() {
	  var selectedProjId = document.getElementById('forumIdInput').value;
	  var waveId = prjsMap[selectedProjId];
	  if(waveId != null && waveId != ""){
		  waveIdToLoad = waveId;
			initialize();
	  }
	  
  });

  jQuery('#submitWaveId').click(function() {
	  var selectedWaveId = document.getElementById('waveSubmitId').value;
	  if(selectedWaveId != null && selectedWaveId != ""){
		  waveIdToLoad = selectedWaveId;
			initialize();
	  }
	  
  });

  jsonrpc.makeRequest("GET_ALL_FORUMS_IDS", null, function(data) {
      try {
	      json = JSON.parse(data);
	      if(!json.error){
	    	  prjsMap = json.result;
				var prjValues = "";
				for (prjId in prjsMap) {  
					var prjIdElems = prjId.split("-");
					var realPrjId = prjIdElems[prjIdElems.length-1];
					if(prjIdElems.length == 2){
						realPrjId = realPrjId + " (" + prjIdElems[0] +")";
					}else if(prjIdElems.length  == 3){
						var user = prjIdElems[0] + "@" + prjIdElems[1] + ".com";
						realPrjId = realPrjId  +  "(" + user  +")";
					}
					prjValues = prjValues + realPrjId + "#" + prjId + "#";
					prjsMap[realPrjId] = prjsMap[prjId];
				}
				var autoCompleteData = prjValues.split("#");
				$("#forumIdInput").autocomplete(autoCompleteData); 
				var forumId = "<%=request != null && request.getParameter("forumId") != null ? request.getParameter("forumId") : ""%>";
				if(forumId != null && forumId != ""){
					waveIdToLoad = prjsMap[forumId];
					initialize();
				}
	      }
      } catch (exception) {
        alert(exception);
      }
  });
});
</script>

<script type="text/javascript">
var _gaq = _gaq || [];
_gaq.push(['_setAccount', 'UA-13269470-3']);
_gaq.push(['_trackPageview']);

(function() {
  var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
  ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
  var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
})();
</script>
</head>
<body style="background-color: #A3D1FF">
<table>
	<tr>
		<td align="center">
		<div style="background-color: #5a9adf; color: white; font-size: 2em">
		Create Wave-based forum with <a href="#" onclick="linkClicked()">DigestBotty</a></div>
		</td>
	</tr>
	<tr align="center">
		<td align="center">
		<div id="waveframe" style="width: 800px; height: 700px"></div>
		</td>
		<td style="vertical-align: text-top; text-align: left; font-size: 10pt; width: 320px" >
			<div>
				<span >Please input "DigestBotty" Forum Id to display the forum's Digest Wave:</span><br></br>
				<input id="forumIdInput" type="text" style="width: 250px "/>
				<input type="button" id="submitForumId" value="display"/>
			</div>
			<div >
				<span>Or input any Wave Id to display:</span><br></br>
				<input type="text" id="waveSubmitId" style="width: 250px " />
				<input type="button" id="submitWaveId" value="display"/>
			</div>
		</td>
	</tr>
</table>

<script type="text/javascript">
function linkClicked(){
	var url = "http://" + "<%= request.getServerName() %>" + "/showembedded?id=googlewave.com!w+KNw8wPWXA";
	try{
		window.location.href = url;
	//_trackEvent("/embed", "clickAd");
	}catch(e){
		alert(url + ": " + e);
	}
}
</script>
</body>
</html>