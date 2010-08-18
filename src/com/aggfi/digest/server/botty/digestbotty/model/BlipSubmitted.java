package com.aggfi.digest.server.botty.digestbotty.model;

import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.gson.annotations.Expose;
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class BlipSubmitted{

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@Expose
	private Long id;

	@Persistent
	@Expose
	private String creator;

	@Persistent
	@Expose
	private String replytoCreator;

	@Persistent
	@Expose
	String modifier;

	@Persistent
	@Expose
	private List<String> contributors;

	@Persistent
	@Expose
	private List<String> replytoContributors;

	@Persistent
	@Expose
	private long version;

	@Persistent
	@Expose
	private String blipId;

	@Persistent
	@Expose
	private String parentBlipId;

	@Persistent
	@Expose
	private String waveletId;

	@Persistent
	private String waveId;

	@Persistent
	@Expose
	private String projectId;

	@Persistent
	@Expose
	private long blipLength;

	@Persistent
	@Expose
	private long createdTime;
	
	@Persistent
	@Expose
	private Date lastUpdated;

	@Persistent
	@Expose
	private List<String> properties1;

	@Persistent
	@Expose
	private List<String> properties2;

	public BlipSubmitted(String creator, String replytoCreator,
			String modifier, List<String> contributors,
			List<String> replytoContributors, long version, String blipId,
			String parentBlipId, String waveletId, String waveId,
			String projectId, long blipLength, long createdTime,
			List<String> properties1, List<String> properties2) {
		super();
		this.creator = creator;
		this.replytoCreator = replytoCreator;
		this.modifier = modifier;
		this.contributors = contributors;
		this.replytoContributors = replytoContributors;
		this.version = version;
		this.blipId = blipId;
		this.parentBlipId = parentBlipId;
		this.waveletId = waveletId;
		this.waveId = waveId;
		this.projectId = projectId;
		this.blipLength = blipLength;
		this.createdTime = createdTime;
		this.lastUpdated = new Date(createdTime);
		this.properties1 = properties1;
		this.properties2 = properties2;
	}
	public Date getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getReplytoCreator() {
		return replytoCreator;
	}
	public void setReplytoCreator(String replytoCreator) {
		this.replytoCreator = replytoCreator;
	}
	public String getModifier() {
		return modifier;
	}
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}
	public List<String> getContributors() {
		return contributors;
	}
	public void setContributors(List<String> contributors) {
		this.contributors = contributors;
	}
	public List<String> getReplytoContributors() {
		return replytoContributors;
	}
	public void setReplytoContributors(List<String> replytoContributors) {
		this.replytoContributors = replytoContributors;
	}
	public long getVersion() {
		return version;
	}
	public void setVersion(long version) {
		this.version = version;
	}
	public String getBlipId() {
		return blipId;
	}
	public void setBlipId(String blipId) {
		this.blipId = blipId;
	}
	public String getParentBlipId() {
		return parentBlipId;
	}
	public void setParentBlipId(String parentBlipId) {
		this.parentBlipId = parentBlipId;
	}
	public String getWaveletId() {
		return waveletId;
	}
	public void setWaveletId(String waveletId) {
		this.waveletId = waveletId;
	}
	public String getWaveId() {
		return waveId;
	}
	public void setWaveId(String waveId) {
		this.waveId = waveId;
	}
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public long getBlipLength() {
		return blipLength;
	}
	public void setBlipLength(long blipLength) {
		this.blipLength = blipLength;
	}
	public long getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
	public List<String> getProperties1() {
		return properties1;
	}
	public void setProperties1(List<String> properties1) {
		this.properties1 = properties1;
	}
	public List<String> getProperties2() {
		return properties2;
	}
	public void setProperties2(List<String> properties2) {
		this.properties2 = properties2;
	}
	@Override
	public String toString() {
		final int maxLen = 10;
		StringBuilder builder = new StringBuilder();
		builder.append("BlipSubmitted [id=");
		builder.append(id);
		builder.append(", creator=");
		builder.append(creator);
		builder.append(", replytoCreator=");
		builder.append(replytoCreator);
		builder.append(", modifier=");
		builder.append(modifier);
		builder.append(", contributors=");
		builder.append(contributors != null ? contributors.subList(0,
				Math.min(contributors.size(), maxLen)) : null);
		builder.append(", replytoContributors=");
		builder.append(replytoContributors != null ? replytoContributors
				.subList(0, Math.min(replytoContributors.size(), maxLen))
				: null);
		builder.append(", version=");
		builder.append(version);
		builder.append(", blipId=");
		builder.append(blipId);
		builder.append(", parentBlipId=");
		builder.append(parentBlipId);
		builder.append(", waveletId=");
		builder.append(waveletId);
		builder.append(", waveId=");
		builder.append(waveId);
		builder.append(", projectId=");
		builder.append(projectId);
		builder.append(", blipLength=");
		builder.append(blipLength);
		builder.append(", createdTime=");
		builder.append(createdTime);
		builder.append(", lastUpdated=");
		builder.append(lastUpdated);
		builder.append(", properties1=");
		builder.append(properties1 != null ? properties1.subList(0,
				Math.min(properties1.size(), maxLen)) : null);
		builder.append(", properties2=");
		builder.append(properties2 != null ? properties2.subList(0,
				Math.min(properties2.size(), maxLen)) : null);
		builder.append("]");
		return builder.toString();
	}
	
}
