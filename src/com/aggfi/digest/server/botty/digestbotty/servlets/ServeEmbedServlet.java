package com.aggfi.digest.server.botty.digestbotty.servlets;



import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.vegalabs.general.server.rpc.util.Util;
import com.aggfi.digest.server.botty.digestbotty.utils.EscapeChars;
import com.google.inject.Singleton;

@Singleton
public class ServeEmbedServlet extends HttpServlet{
	private static final Logger LOG = Logger.getLogger(ServeEmbedServlet.class.getName());
	private static final  Util util = new Util();
	

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter writer = response.getWriter();
		try{
			String siteUrl = request.getParameter("siteUrl");
			
			if (util.isNullOrEmpty(siteUrl) ) {
			  throw new IllegalArgumentException("Missing required param: siteUrl");
			}
			String out = generateEmbedGadgetXml(siteUrl);
			writer.print(out);
			writer.flush();
		}catch(Exception e){
			writer.print(e.getMessage());
			e.printStackTrace(writer);
			writer.flush();
			LOG.severe(e.toString() + "\n" + e.getMessage());
		}
	}

	public String generateEmbedGadgetXml(String siteUrl) throws UnsupportedEncodingException {

		Object[] args = {EscapeChars.forScriptTagsOnly(siteUrl)};

		String extensionStr = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?> <Module> <ModulePrefs title=\"Embed Gadget\" width=\"977\" height=\"677\"><Require feature=\"wave\" />  </ModulePrefs> <Content type=\"html\">  <![CDATA[" +
			"<div style=\"position:absolute; left:77; top:77; width:977; height:677; clip:rect(0,981,681,0); background:#FFF;\"><iframe src=\"{0}/\" width=\"977\" height=\"677\" marginwidth=\"0\" marginheight=\"0\" frameborder=\"no\" scrolling=\"yes\" style=\"border-width:2px; border-color:#333; background:#FFF; border-style:solid;\"></iframe></div>" +
			"]]></Content></Module>";

		MessageFormat fmt = new MessageFormat(extensionStr);
		String out = fmt.format(args);
		LOG.info("Serving embed for " + siteUrl + "\n" + out);
		return out;
	}
}

