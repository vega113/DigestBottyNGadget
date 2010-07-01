package com.aggfi.digest.server.botty.digestbotty.model;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.aggfi.digest.server.botty.google.forumbotty.model.Digest;
import com.google.gson.annotations.Expose;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class ExtDigest  extends Digest{
	Logger LOG = Logger.getLogger(this.getClass().getName());

	public ExtDigest(String domain, String waveId, String projectId,
			String description, String name, String installerThumbnailUrl,
			String installerIconUrl, String robotThumbnailUrl,
			String forumSiteUrl, String googlegroupsId, String ownerId,String author, int maxDigests) {
		super(domain, waveId, projectId);
		this.description = description;
		this.name = name;
		this.installerThumbnailUrl = installerThumbnailUrl;
		this.installerIconUrl = installerIconUrl;
		this.robotThumbnailUrl = robotThumbnailUrl;
		this.forumSiteUrl = forumSiteUrl;
		this.googlegroupsId = googlegroupsId;
		this.ownerId = ownerId;
		this.author = author;
		this.maxDigests = maxDigests;
	}

	@Persistent
	@Expose
	private String description = null;

	@Persistent
	@Expose
	/**
	 * name of forum in installer
	 */
	private String name = null;
	
	@Persistent
	@Expose
	private String author;
	
	@Persistent
	@Expose
	private String installerUrl = null;
	
	@Persistent
	@Expose
	private String installerThumbnailUrl = null;
	
	@Persistent
	@Expose
	private String installerIconUrl = null;

	@Persistent
	@Expose
	private String robotThumbnailUrl = null;

	@Persistent
	@Expose
	private String forumSiteUrl = null;

	@Persistent
	@Expose
	private String googlegroupsId = null;
	
	@Persistent
	@Expose
	private String ownerId = null;
	
	@Persistent
	@Expose
	private int maxDigests;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInstallerThumbnailUrl() {
		return installerThumbnailUrl;
	}

	public void setInstallerThumbnailUrl(String installerThumbnailUrl) {
		this.installerThumbnailUrl = installerThumbnailUrl;
	}

	public String getInstallerIconUrl() {
		return installerIconUrl;
	}

	public void setInstallerIconUrl(String installerIconUrl) {
		this.installerIconUrl = installerIconUrl;
	}

	public String getRobotThumbnailUrl() {
		return robotThumbnailUrl;
	}

	public void setRobotThumbnailUrl(String robotThumbnailUrl) {
		this.robotThumbnailUrl = robotThumbnailUrl;
	}

	public String getForumSiteUrl() {
		return forumSiteUrl;
	}

	public void setForumSiteUrl(String forumSiteUrl) {
		this.forumSiteUrl = forumSiteUrl;
	}

	public String getGooglegroupsId() {
		return googlegroupsId;
	}

	public void setGooglegroupsId(String googlegroupsId) {
		this.googlegroupsId = googlegroupsId;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getInstallerUrl() {
		return installerUrl;
	}

	public void setInstallerUrl(String installerUrl) {
		this.installerUrl = installerUrl;
	}

	@Override
	public String toString() {
		return "ExtDigest [author=" + author + ", description=" + description
				+ ", forumSiteUrl=" + forumSiteUrl + ", googlegroupsId="
				+ googlegroupsId + ", installerIconUrl=" + installerIconUrl
				+ ", installerThumbnailUrl=" + installerThumbnailUrl
				+ ", installerUrl=" + installerUrl
				+ ", name=" + name + ", ownerId=" + ownerId
				+ ", robotThumbnailUrl=" + robotThumbnailUrl
				+ ", getCreated()=" + getCreated() + ", getDomain()="
				+ getDomain() + ", getId()=" + getId() + ", getWaveId()="
				+ getWaveId() + "]";
	}

}
