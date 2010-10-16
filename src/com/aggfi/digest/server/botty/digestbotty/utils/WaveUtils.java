package com.aggfi.digest.server.botty.digestbotty.utils;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.wave.api.Annotation;
import com.google.wave.api.Annotations;
import com.google.wave.api.Blip;

public class WaveUtils {
	 private static final Logger LOG = Logger.getLogger(WaveUtils.class.getName());
	 
	public static void updateLinkTitleInDigestBlip(String forumName,String oldForumName, Blip ablip, String linkType) {
		String content = ablip.getContent();
		LOG.info("blip: " + ablip.getBlipId() + ", content: " + content + ", old name: " + oldForumName + ", new name: " + forumName);
		if(content.contains(oldForumName)){
			Annotations annotations = ablip.getAnnotations();
			List<Annotation> likAnnotations =   annotations.get(linkType);
			Annotation link = null;
			
			if(likAnnotations != null && likAnnotations.size() > 0){
				link = likAnnotations.get(0);
				String linkContent = ablip.range(link.getRange().getStart(), link.getRange().getEnd()).value().getText();
				LOG.info("linkContent: " + linkContent);
				if(likAnnotations.size() != 1){
					LOG.log(Level.WARNING,"annotation links size is: " + likAnnotations.size());
				}
				linkContent = linkContent.replace(oldForumName,forumName);
				LOG.info("linkContent after replace: " + linkContent);
				ablip.range(link.getRange().getStart(), link.getRange().getEnd()).delete();
				ablip.at(link.getRange().getStart()).insert(linkContent);
				ablip.range(link.getRange().getStart(), link.getRange().getEnd() - (oldForumName.length() - forumName.length())).annotate(link.getName(), link.getValue());
			}
		}
	}
	
	public static String extractBlipIdFromAnnon(Blip ablip, String annonType, String annonTxt) {

		Annotations annotations = ablip.getAnnotations();
		List<Annotation> likAnnotations =   annotations.get(annonType);
		if(likAnnotations != null && likAnnotations.size() > 0){
			for(Annotation link : likAnnotations){
				String linkContent = ablip.range(link.getRange().getStart(), link.getRange().getEnd()).value().getText();
				String linkValue = link.getValue();
				if( linkContent.trim().equals(annonTxt.trim())){
					int start = linkValue.indexOf("b+");
					if(start != -1){
						String blipId = linkValue.substring(start,linkValue.length());
						return blipId;
					}
				}
			}
			
		}
		return null;
	}
	
	public static String extractWaveIdFromAnnon(Blip ablip,String annonType, String annonTxt) {
		Annotations annotations = ablip.getAnnotations();
		List<Annotation> likAnnotations =   annotations.get(annonType);
		if(likAnnotations != null && likAnnotations.size() > 0){
			for(Annotation link : likAnnotations){
				String linkContent = ablip.range(link.getRange().getStart(), link.getRange().getEnd()).value().getText();
				String linkValue = link.getValue();
				if( linkContent.trim().contains(annonTxt.trim())){
					LOG.info("waveId: " + linkValue);
					String waveId = linkValue.split("!")[1];
					return waveId;
				}
			}
			
		}
		return null;
	}
}
