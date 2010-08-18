package com.aggfi.digest.server.botty.digestbotty.admin;

import java.io.Serializable;



public class BlipDataSortHelper implements Comparable<BlipDataSortHelper>, Serializable{
	  /**
	 * 
	 */
	private static final long serialVersionUID = -1306980624914175717L;
	public BlipDataSortHelper(String participantId){
		  this.participantId = participantId;
		  influence = 0;
	  }
	  public BlipDataSortHelper(String participantId, double influence) {
		super();
		this.participantId = participantId;
		this.influence = influence;
	}
	public double getInfluence() {
		return influence;
	}
	public void setInfluence(double influence) {
		this.influence = influence;
	}
	public String getParticipantId() {
		return participantId;
	}
	  String participantId = null;
	  double influence = 0;
	@Override
	public int compareTo(BlipDataSortHelper other) {
		return (int)Math.round((other.getInfluence() - influence)*1000) ;
	}
}  
