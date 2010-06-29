<% String projectId = request.getParameter("id");
  
  if (projectId == null) {
    throw new IllegalArgumentException("Missing required param: id");
  }  
%>
		<div>
		[ <a class="menuLink" href="/admin?id=<%=projectId%>">Admin</a> ]&nbsp;&nbsp;
		[ <a class="menuLink" href="/report?id=<%=projectId%>">Report</a> ]&nbsp;&nbsp;
		</div>

