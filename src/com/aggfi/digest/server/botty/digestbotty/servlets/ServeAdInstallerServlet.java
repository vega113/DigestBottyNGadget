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
import com.aggfi.digest.server.botty.digestbotty.dao.ExtDigestDao;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ServeAdInstallerServlet extends HttpServlet{
	private ExtDigestDao extDigestDao = null;
	private static final Logger LOG = Logger.getLogger(ServeAdInstallerServlet.class.getName());
	private static final  Util util = new Util();
	private String projectId;
	
	@Inject
	public ServeAdInstallerServlet(ExtDigestDao extDigestDao){
		this.extDigestDao = extDigestDao;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter writer = response.getWriter();
		try{
			projectId = request.getParameter("id");
			
			if (util.isNullOrEmpty(projectId) ) {
			  throw new IllegalArgumentException("Missing required param: id (Digest id)");
			}
			String out = generateAdSenseInstallerXml(projectId, request);
			writer.print(out);
			writer.flush();
		}catch(Exception e){
			writer.print(e.getMessage());
			e.printStackTrace(writer);
			writer.flush();
			LOG.severe(e.toString() + "\n" + e.getMessage());
		}
	}

	public String generateAdSenseInstallerXml(String projectId, HttpServletRequest request) {
		String installerXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><extension name=\"AdSense Gadget\" thumbnailUrl=\"http://lh5.ggpht.com/_tsWs83xehHE/TIvz1zOiaDI/AAAAAAAAFeI/SaKZG_HeBz8/adsense_icon.png\" description=\"AdSense gadget for {0} forum \"> <author name=\"{1}\"/>  <menuHook location=\"toolbar\" text=\"Add AdSense\"  iconUrl=\"http://lh5.ggpht.com/_tsWs83xehHE/TIvz1zOiaDI/AAAAAAAAFeI/SaKZG_HeBz8/adsense_icon.png\"> <insertGadget url=\"http://{2}/serveAd?id={3}\"/> </menuHook> </extension>";
		com.aggfi.digest.server.botty.digestbotty.model.ExtDigest extDigest= extDigestDao.retrDigestsByProjectId(projectId).get(0);
		
		Object[] args = {extDigest.getName(), extDigest.getName(), request.getServerName() + ":" + request.getServerPort(),projectId};
		MessageFormat fmt = new MessageFormat(installerXml);
		String out = fmt.format(args);
		LOG.info("Serving ad installer for " + projectId + "\n" + out);
		return out;
	}
}



