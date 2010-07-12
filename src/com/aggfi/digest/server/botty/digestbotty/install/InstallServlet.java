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

import com.aggfi.digest.server.botty.digestbotty.dao.ExtDigestDao;
import com.aggfi.digest.server.botty.digestbotty.model.ExtDigest;
import com.aggfi.digest.server.botty.google.forumbotty.Util;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class InstallServlet extends HttpServlet{
	private ExtDigestDao extDigestDao = null;
	private static final Logger LOG = Logger.getLogger(InstallServlet.class.getName());
	private static final  Util util = new Util();
	
	@Inject
	public InstallServlet(ExtDigestDao extDigestDao){
		this.extDigestDao = extDigestDao;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter writer = response.getWriter();
		try{
			String domain = request.getServerName();
			boolean isLocal = "localhost".equals(domain);
			int index = domain.indexOf(".");
			String appId = isLocal? "localhost" : domain.substring(0, index);
			String projectId = request.getParameter("id");
			
			if (util.isNullOrEmpty(projectId) ) {
			  throw new IllegalArgumentException("Missing required param: id (Digest id)");
			}
			List<ExtDigest> digests = extDigestDao.retrDigestsByProjectId(projectId);
			if(digests.size() > 1 ){
				throw new IllegalArgumentException("no digest found for: " + projectId);
			}
			if(digests.size() == 0 ){
				throw new IllegalArgumentException("no digest found for: " + projectId);
			}
			ExtDigest digest = digests.get(0);
			String port = isLocal ? ":8888" : "";
			if(digest.getInstallerThumbnailUrl() == null){
				digest.setInstallerThumbnailUrl("http://wave.google.com/images/wave-60_wshadow.gif");
			}
			/*
			 * 0 - name
			 * 1 - thumbnailUrl
			 * 2 - description
			 * 3 - version
			 * 4 - infoUrl
			 * 5 - author name
			 * 6 - menu trigger text
			 * 7 - robot_id
			 * 8 - toolbar trigger text
			 * 9 - icon url
			 */
			String projectName = digest.getName(); //0
		    String profileImageUrl = digest.getInstallerThumbnailUrl();//1
			String projectDescription = digest.getDescription();//2
			String version = "0.5";//3
			String infoUrl = digest.getForumSiteUrl();//4
			String authorName = digest.getAuthor();//5
			String triggerText = "New " + digest.getName() + " Post"; //6
			String robotAddress = System.getProperty("APP_DOMAIN") + "+" + projectId +  "@appspot.com" ;//7
			String menuText = "Add " + digest.getName();//8
			String iconUrl = digest.getRobotThumbnailUrl();//9
			Object[] args = {projectName, profileImageUrl, projectDescription,version,infoUrl,
					authorName, triggerText,robotAddress,
					menuText,iconUrl,projectId};
			
			String extensionStr = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"<extension name=\"{0}\" thumbnailUrl=\"{1}\" description=\"{2}\" version=\"{3}\">" + 
					"<author name=\"{5}\" /> " +
					"<savedSearchHook name=\"{0}\"  query=\"with:{7}\" color=\"#00FF00\" />" +
					"<menuHook location=\"newwavemenu\" text=\"{6}\">" +
						"<createNewWave>" +
								"<participant id=\"{7}\"/>" +
						"</createNewWave>" +
					"</menuHook>" + 
					"<menuHook location=\"TOOLBAR\" text=\"{8}\" iconUrl=\"{9}\">" +     
						"<addParticipants>" + 
							"<participant id=\"{7}\" />" + 
						"</addParticipants>" +  
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

