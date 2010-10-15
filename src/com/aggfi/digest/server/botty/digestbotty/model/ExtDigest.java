package com.aggfi.digest.server.botty.digestbotty.model;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.aggfi.digest.server.botty.google.forumbotty.model.Digest;
import com.google.appengine.api.datastore.Text;
import com.google.gson.annotations.Expose;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class ExtDigest  extends Digest{
	public final static int CURRENT_VERSION = 2;
	Logger LOG = Logger.getLogger(this.getClass().getName());

	public ExtDigest(String domain, String waveId, String projectId,
			String description, String name, String installerThumbnailUrl,
			String installerIconUrl, String robotThumbnailUrl,
			String forumSiteUrl, String googlegroupsId, String ownerId,String author, int maxDigests, int version) {
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
		this.version = version;
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
	@Persistent
	@Expose
	private String lastDigestBlipId = null;
	@Persistent
	@Expose
	private String firstDigestBlipId = null;
	@Persistent
	@Expose
	Integer version = 0;
	
	@Expose
	@Persistent
	private Text googleAdsenseCode = null;
	
	@Expose
	@Persistent
	private String faqWaveId = null;
	
	@Expose
	@Persistent
	private String postWaveId = null;

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
	
	/**
	 * is the primary forum icon
	 * @return
	 */
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


	public String getLastDigestBlipId() {
		return lastDigestBlipId;
	}

	public void setLastDigestBlipId(String lastDigestBlipId) {
		if(lastDigestBlipId == null){
			return;
		}else{
			this.lastDigestBlipId = lastDigestBlipId;
		}
	}

	public String getFirstDigestBlipId() {
		return firstDigestBlipId;
	}

	public void setFirstDigestBlipId(String firstDigestBlipId) {
		this.firstDigestBlipId = firstDigestBlipId;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	@Deprecated
	/**
	 * store/get the adsense in adminConfig
	 */
	public Text getGoogleAdsenseCode() {
		return googleAdsenseCode;
	}
	@Deprecated
	public void setGoogleAdsenseCode(Text code) {
		this.googleAdsenseCode = code;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ExtDigest [description=");
		builder.append(description);
		builder.append(", name=");
		builder.append(name);
		builder.append(", author=");
		builder.append(author);
		builder.append(", installerUrl=");
		builder.append(installerUrl);
		builder.append(", installerThumbnailUrl=");
		builder.append(installerThumbnailUrl);
		builder.append(", installerIconUrl=");
		builder.append(installerIconUrl);
		builder.append(", robotThumbnailUrl=");
		builder.append(robotThumbnailUrl);
		builder.append(", forumSiteUrl=");
		builder.append(forumSiteUrl);
		builder.append(", googlegroupsId=");
		builder.append(googlegroupsId);
		builder.append(", ownerId=");
		builder.append(ownerId);
		builder.append(", maxDigests=");
		builder.append(maxDigests);
		builder.append(", lastDigestBlipId=");
		builder.append(lastDigestBlipId);
		builder.append(", firstDigestBlipId=");
		builder.append(firstDigestBlipId);
		builder.append(", version=");
		builder.append(version);
		builder.append("]");
		return builder.toString();
	}

	public String getFaqWaveId() {
		return faqWaveId;
	}

	public void setFaqWaveId(String faqWaveId) {
		this.faqWaveId = faqWaveId;
	}

	public String getPostWaveId() {
		return postWaveId;
	}

	public void setPostWaveId(String postWaveId) {
		this.postWaveId = postWaveId;
	}
}
