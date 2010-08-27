<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<!--<meta http-equiv="X-UA-Compatible" content="chrome=1">-->

<title>Embedded Wave by DigestBotty</title>
<script type="text/javascript">
function loadDigg() {
var s = document.createElement('SCRIPT'), s1 = document.getElementsByTagName('SCRIPT')[0];
s.type = 'text/javascript';
s.async = true;
s.src = 'http://widgets.digg.com/buttons.js';
s1.parentNode.insertBefore(s, s1);

};
</script>

<script src="http://www.google.com/jsapi" type="text/javascript"></script>
<script type="text/javascript">
	var waveIdToLoad = null;
	var wavePanel = null;
	var globalUrl = "http://" + "<%= request.getServerName() %>";
    google.load("wave", "1");
    
    function createDiggLink(href){
    	var newlink = document.getElementById("diggLink");
		var diggSpan = document.getElementById("diggSpan");
		var title = "<%=request.getParameter("title") != null ? request.getParameter("title") : ""%>";
		var description= "<%=request.getParameter("description") != null ? request.getParameter("description") : ""%>";
		diggSpan.innerHTML = description;
		if(title != null && title != ""){
			newlink.setAttribute('class', 'DiggThisButton DiggCompact');
			var newHref = "http://digg.com/submit?url=" + escape(href) + "&amp;title=" + escape(title);
			newlink.setAttribute('href',newHref );
		}
		
    }
    
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
      var idWave = "<%=request.getParameter("waveId") != null ? request.getParameter("waveId") : ""%>";
      if(idWave != null && idWave != ""){
    	  waveIdToLoad = idWave;
    	  document.getElementById("likeFrame").src="http://www.facebook.com/plugins/like.php?href=" + escape(globalUrl) + "/showembedded?waveId=" + escape(idWave) + ";layout=standard&amp;show_faces=false&amp;width=450&amp;action=like&amp;colorscheme=light&amp;height=35";
    	  loadDigg();
    	  createDiggLink(globalUrl + "/showembedded?waveId=" + escape(idWave));
    	  wavePanel.loadWave(waveIdToLoad);
    	  var title = "<%=request.getParameter("title") != null ? request.getParameter("title") : ""%>";
    	  if(title != null && title != ""){
    		  document.title=title;
    	  }
    	  var description = "<%=request.getParameter("description") != null ? request.getParameter("description") : ""%>";
    	  if(description != null && description != ""){
    		  var forumDescription = document.getElementById("forumDescription");
    		  forumDescription.style.display="inline";
    		  forumDescription.innerHTML="<b>Forum description</b>: " + description;
    	  }else{
    		  var forumDescription = document.getElementById("forumDescription");
    		  forumDescription.style.display="none";
    		  forumDescription.innerHTML="";
    	  }
      }
      
     
      jQuery('#submitForumId').click(function() {
		  var selectedProjId = document.getElementById('forumIdInput').value;
		  var waveId = prjsMap[selectedProjId];
		  var description = null;
		  waveIdToLoad = waveId;
		  var title = null;
		  if(titlesMap != null && titlesMap[waveIdToLoad] != null && titlesMap[waveIdToLoad].length > 0){
			  title = titlesMap[waveIdToLoad];
		  }
		  if(descriptionsMap != null && descriptionsMap[waveIdToLoad] != null && descriptionsMap[waveIdToLoad].length > 0){
			  description = descriptionsMap[waveIdToLoad];
		  }
		  if(waveId != null && waveId != ""){
			 
			  var url =   globalUrl + "/showembedded?waveId=" + escape(waveIdToLoad);
			  if(title != null){
				  url = url +  "&title=" + escape(title);
			  }
			  if(description != null){
				  url = url +  "&description=" + escape(description);
			  }
				try{
					window.location.href = url;
				//_trackEvent("/embed", "clickAd");
				}catch(e){
					alert(url + ": " + e);
				}
		  }
		  
	  });

	  jQuery('#submitWaveId').click(function() {
		  var selectedWaveId = document.getElementById('waveSubmitId').value;
		  if(selectedWaveId != null && selectedWaveId != ""){
			  waveIdToLoad = selectedWaveId;
			  var url =  globalUrl + "/showembedded?waveId=" + waveIdToLoad;
				try{
					window.location.href = url;
				//_trackEvent("/embed", "clickAd");
				}catch(e){
					alert(url + ": " + e);
				}
		  }
		  
	  });
	  
    	  jsonrpc.makeRequest("GET_ALL_FORUMS_IDS", null, function(data) {
    	      try {
    		      json = JSON.parse(data);
    		      if(!json.error){
    		    	  prjsMap = json.result;
    		    	  titlesMap = json.resultTitles;
    		    	  descriptionsMap = json.resultDescriptions;
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
    						var url =  "http://digestbotty.appspot.com/showembedded?waveId=" + waveIdToLoad + "&title=" + escape(titlesMap[waveIdToLoad]) + "&description=" + escape(descriptionsMap[waveIdToLoad]);
    						try{
    							window.location.href = url;
    						//_trackEvent("/embed", "clickAd");
    						}catch(e){
    							alert(url + ": " + e);
    						}
    					}
    		      }
    	      } catch (exception) {
    	        alert(exception);
    	      }
    	  });
      
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
var titlesMap = null;
var descriptionsMap = null;
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
	<tr align="center"><td><div id="forumDescription" style="display: none"></div></td></tr>
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
			<div>
				<a id="openInGWave" href="#" onclick="openInGWave()"> Open natively in Google Wave</a>
			</div>
			<div>
				<a id='diggLink'><span style="display:none" id="diggSpan"></span></a>
			</div>
			<div style="height: 125px">
				<iframe id="likeFrame"  scrolling="no" frameborder="0" style="border:none; overflow:hidden; width:450px; height:75px;" allowTransparency="true"></iframe>
			</div>
			
		</td>
	</tr>
	
</table>

<script type="text/javascript">
function linkClicked(){
	var url =  "http://digestbotty.appspot.com/showembedded?waveId=googlewave.com!w+KNw8wPWXA";
	try{
		window.location.href = url;
	//_trackEvent("/embed", "clickAd");
	}catch(e){
		alert(url + ": " + e);
	}
}

function openInGWave(){
	if(waveIdToLoad == null)
		return;
	var url =  "https://wave.google.com/wave/waveref/" + waveIdToLoad.replace("!", "/");
	
	window.open(url);
}
</script>
</body>
</html>