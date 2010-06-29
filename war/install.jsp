<% 
	String domain = request.getServerName();
	boolean isLocal = "localhost".equals(domain);
	int index = domain.indexOf(".");
	String appId = isLocal? "localhost" : domain.substring(0, index);
	String projectId = request.getParameter("id");
	
	if (projectId == null) {
	  throw new IllegalArgumentException("Missing required param: id");
	}
	String port = isLocal ? ":8888" : "";
	String robotAddress = appId + "+" + projectId + "@" + domain.substring(index + 1, domain.length());
   String profileImageUrl = "http://" + domain + port + "/images/forumbotty_thumb.png";
	String projectName = "Aggfi Forum Botty";
	String projectDescription = "Makes it easy to create waves discussing Aggregated Finance (Aggfi).";
	String triggerText = "New Aggfi Post";%>
<extension 
    name="<%=projectName%>"
    thumbnailUrl="<%=profileImageUrl%>"
    description="<%=projectDescription%>"> 
  <author name="Aggfi"/> 
  <menuHook location="NEW_WAVE_MENU" text="<%=triggerText%>"> 
    <createNewWave> 
      <participant id="<%=robotAddress%>"/>
    </createNewWave> 
  </menuHook>
  <menuHook location="TOOLBAR" text="<%=triggerText%>"
      iconUrl="<%=profileImageUrl%>">    
    <addParticipants> 
      <participant id="<%=robotAddress%>" />
    </addParticipants>    
  </menuHook>    
</extension>