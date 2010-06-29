<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" 
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
<title>Report</title>
<link type="text/css" href="/css/admin.css" rel="stylesheet" />
    <script type="text/javascript" 
     src="http://www.google.com/jsapi">
    </script>
<script type="text/javascript" src="/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="/js/json2.js"></script>
<script type="text/javascript" src="/js/jsonrpc.js"></script>
<script type="text/javascript" src="/js/report.js"></script>
</head>
<body>
  <div id="status"></div>
  <br/>
  <%@include file="menu.jsp" %>
  <br/>
  <div id="main">
  
    <table>
      <tr>
        <td colspan="2">
          <fieldset>
          <legend><b>New Waves from Last 14 Days</b></legend>
            <div id="newPosts"></div>      
          </fieldset>           
        </td>    
      </tr>  
      <tr>
        <td colspan="2">
          <fieldset>
          <legend><b>Breakdown for All Tags</b></legend>
            <div id="allTags"></div>      
          </fieldset>           
        </td>    
      </tr>
      <tr>
        <td>
          <fieldset>
          <legend><b>Breakdown for API Tags</b></legend>
            <div id="apiTags"></div>      
          </fieldset>           
        </td>
        <td>
          <fieldset>
          <legend><b>Breakdown for Language Tags</b></legend>
            <div id="langTags"></div>      
          </fieldset>           
        </td>                
      </tr>    
    </table>           
            
    <br>       
  </div>
</body>
</html>