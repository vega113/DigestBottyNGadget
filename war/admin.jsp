<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" 
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
<title>Admin</title>
<link type="text/css" href="/css/admin.css" rel="stylesheet" />

<script type="text/javascript" src="/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="/js/json2.js"></script>
<script type="text/javascript" src="/js/jsonrpc.js"></script>
<script type="text/javascript" src="/js/admin.js"></script>
</head>
<body>
  <div id="status"></div>
  <br>
  <%@include file="menu.jsp" %>
  <br>     
  <div id="main">      
    <fieldset>
    <legend><b>Set up Default Participants</b></legend>
      <input type="text" style="width: 120px;" id="defaultParticipantId"/>
      <input type="button" id="addDefaultParticipant" value="add"/>
      <div id="defaultParticipantsList"></div>
    </fieldset>    
    <br>
    
    <fieldset>
    <legend><b>Set up Default Tags</b></legend>
	    <input type="text" style="width: 120px;" id="defaultTag"/>
	    <input type="button" id="addDefaultTag" value="add"/>
	    <div id="defaultTagsList"></div>
    </fieldset>
    <br>
    
    <fieldset>
    <legend><b>Set up Auto-tagging</b></legend>
	    Tag:&nbsp;<input type="text" style="width: 120px;" id="autoTag"/>&nbsp;
	    Regex:&nbsp;<input type="text" style="width: 120px;" id="autoTagRegex"/>
	    <input type="button" id="addAutoTag" value="add"/>
	    <div id="autoTagsList"></div>  
    </fieldset>              
  </div>
</body>
</html>