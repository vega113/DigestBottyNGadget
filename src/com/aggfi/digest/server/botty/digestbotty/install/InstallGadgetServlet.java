package com.aggfi.digest.server.botty.digestbotty.install;


import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.inject.Singleton;

@SuppressWarnings("serial")
@Singleton
public class InstallGadgetServlet extends HttpServlet{
	private static final Logger LOG = Logger.getLogger(InstallGadgetServlet.class.getName());
	

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter writer = response.getWriter();
		try{
			
			String projectName = "DigestBotty"; //0
		    String profileImageUrl = "http://" + System.getProperty("APP_DOMAIN") + ".appspot.com/images/forumbotty_thumb_old_2.png";//1
			String projectDescription = "DigestBotty is an extension of ForumBotty. It allows 'on the fly' creation of forums to provide Forum like experience inside Google Wave.";//2
			String version = "0.1";//3
			String authorName = "Yuri Zelikov";//4
			String robotAddress = System.getProperty("APP_DOMAIN") + "+gadget@appspot.com" ;//5
			String gadgetbottyInstallUrl = "http://" + System.getProperty("APP_DOMAIN") + ".appspot.com/digestbottygadget/com.aggfi.digest.client.DigestBottyGadget.gadget.xml";//6
			Object[] args = {projectName, profileImageUrl, projectDescription,version,authorName, robotAddress,gadgetbottyInstallUrl};
			
			String extensionStr = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"<extension name=\"{0}\" thumbnailUrl=\"{1}\" description=\"{2}\">" + 
					"<author name=\"{4}\" /> " +
					"<menuHook location=\"TOOLBAR\" text=\"{0}\" iconUrl=\"{1}\">" +     
						"<addParticipants>" + 
							"<participant id=\"{5}\" />" + 
						"</addParticipants>" +  
						 "<insertGadget url=\"{6}\"/>" +
					"</menuHook>" + 
			    "</extension>";
			
			MessageFormat fmt = new MessageFormat(extensionStr);
			String out = fmt.format(args);
			writer.print(out);
			writer.flush();
		}catch(Exception e){
			writer.print(e.getMessage());
			e.printStackTrace(writer);
			writer.flush();
			LOG.severe(e.toString() + "\n" + e.getMessage());
		}
	}
}

