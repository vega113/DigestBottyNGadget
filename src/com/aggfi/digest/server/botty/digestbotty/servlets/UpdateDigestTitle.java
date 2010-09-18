package com.aggfi.digest.server.botty.digestbotty.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.waveprotocol.wave.model.id.WaveId;
import org.waveprotocol.wave.model.id.WaveletId;

import com.vegalabs.general.server.rpc.util.Util;
import com.aggfi.digest.server.botty.google.forumbotty.ForumBotty;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.wave.api.Wavelet;

@Singleton
public class UpdateDigestTitle extends HttpServlet{
	private static final Logger LOG = Logger.getLogger(UpdateDigestTitle.class.getName());
	private static final  Util util = new Util();
	
	private ForumBotty robot;
	
	@Inject
	public UpdateDigestTitle(ForumBotty robot){
		this.robot = robot;
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter writer = response.getWriter();
		try{
			String waveId = request.getParameter("waveId");
			
			if (util.isNullOrEmpty(waveId) ) {
			  throw new IllegalArgumentException("Missing required param: waveId)");
			}
			
			String newTitle = request.getParameter("newTitle");
			
			if (util.isNullOrEmpty(newTitle) ) {
			  throw new IllegalArgumentException("Missing required param: newTitle)");
			}
			
			String domain = waveId.split("!")[0];
			String id = waveId.split("!")[1];
			Wavelet wavelet = null;
			try{
				wavelet = robot.fetchWavelet(new WaveId(domain,id), new WaveletId(domain, "conv+root"), robot.getRpcServerUrl());
			}catch(IOException e){
				LOG.log(Level.SEVERE, "waveId: " + waveId + ", title: " + newTitle, e);
				e.printStackTrace(writer);
				writer.flush();
				return;
			}
			
			if(wavelet != null){
				wavelet.setTitle(newTitle);
			}
			
			try{
				robot.submit(wavelet, robot.getRpcServerUrl());
			}catch(IOException e){
				LOG.log(Level.SEVERE, "waveId: " + waveId + ", title: " + newTitle, e);
				e.printStackTrace(writer);
				writer.flush();
				return;
			}
			
			
			
			String out = "updated!";
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
