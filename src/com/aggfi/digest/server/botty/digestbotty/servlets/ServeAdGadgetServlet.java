package com.aggfi.digest.server.botty.digestbotty.servlets;



import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vegalabs.general.server.rpc.util.Util;
import com.aggfi.digest.server.botty.google.forumbotty.dao.AdminConfigDao;
import com.aggfi.digest.server.botty.google.forumbotty.model.AdminConfig;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ServeAdGadgetServlet extends HttpServlet{
	private AdminConfigDao adminConfigDao = null;
	private static final Logger LOG = Logger.getLogger(ServeAdGadgetServlet.class.getName());
	private static final  Util util = new Util();
	
	@Inject
	public ServeAdGadgetServlet(AdminConfigDao adminConfigDao){
		this.adminConfigDao = adminConfigDao;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter writer = response.getWriter();
		try{
			String projectId = request.getParameter("id");
			
			if (util.isNullOrEmpty(projectId) ) {
			  throw new IllegalArgumentException("Missing required param: id (Digest id)");
			}
			String out = generateAdSenseGadgetXml(projectId);
			writer.print(out);
			writer.flush();
		}catch(Exception e){
			writer.print(e.getMessage());
			e.printStackTrace(writer);
			writer.flush();
			LOG.severe(e.toString() + "\n" + e.getMessage());
		}
	}

	public String generateAdSenseGadgetXml(String projectId) {
		String adsenseStr = null;
		if(projectId != null &&  projectId.equals("vega113@googlewave.com")){
			adsenseStr = "<script type=\"text/javascript\"><!--\n" +
			"google_ad_client = \"pub-3589749845269196\";\n"+
			"/* 468x60, created 8/23/10 */\n" +
			"google_ad_slot = \"7979283764\";\n"+
			"google_ad_width = 468;\n"+
			"google_ad_height = 60;\n"+
			"//-->\n"+
			"</script>\n"+
			"<script type=\"text/javascript\"\n"+
			"src=\"http://pagead2.googlesyndication.com/pagead/show_ads.js\">\n"+
			"</script>\n";
			LOG.info("Adding Ad by DB");
		}else{
			AdminConfig adminConfig= adminConfigDao.getAdminConfig(projectId);
			
			
			adsenseStr = adminConfig.getAdsense() != null ? adminConfig.getAdsense().getValue() : "";
			if("".equals(adsenseStr)){
				throw new IllegalArgumentException("Invalid forumId!");
			}
		}
		
		//let's find the width;
		String width = "\"" + (Integer.parseInt(extractValue(adsenseStr, "google_ad_width")) + 4) + "\"";
		String height = "\"" + (Integer.parseInt(extractValue(adsenseStr, "google_ad_height")) +4) + "\"" ;
		
		String adtrackStr =  "<script type=\"text/javascript\">document.write(unescape(\"%3Cscript src='\" + \"http://digestbotty.appspot.com/js/adtracker.js' type='text/javascript'%3E%3C/script%3E\"));</script>";
		
		Object[] args = {width,height,adsenseStr,adtrackStr};
		
		String extensionStr = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?> <Module> <ModulePrefs title=\"AdSense Gadget\" width={0} height={1}><Require feature=\"wave\" />  </ModulePrefs> <Content type=\"html\">  <![CDATA[" + 
			"<html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">{3}</head><body><iframe id=\"frameId\" tabIndex='-1' style=\"position:absolute;width:0;height:0;border:0\"></iframe><table align=\"center\"><tr><td>{2}</td> </tr></table></body></html>" +
			"]]></Content></Module>";
		
		
		MessageFormat fmt = new MessageFormat(extensionStr);
		String out = fmt.format(args);
		LOG.info("Serving ad for " + projectId + "\n" + out);
		return out;
	}

	public String  extractValue(String adsenseStr, String valueToFind) {
		int s1 = adsenseStr.indexOf(valueToFind);
		int s2 = adsenseStr.substring(s1 + valueToFind.length()).indexOf("=");
		int s3 = adsenseStr.substring(s1 + valueToFind.length()).indexOf(";");
		return adsenseStr.substring(s1 + valueToFind.length()).substring(s2+1, s3).trim();
	}
}


