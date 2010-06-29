package com.aggfi.digest.server.botty.google.forumbotty.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.wave.api.Wavelet;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class ForumPost {
  @PrimaryKey
  @Persistent
  @Expose
  private String id = null;
  @Persistent
  @Expose
  private String domain = null;
  @Persistent
  @Expose
  private String waveId = null;
  @Persistent
  @Expose
  private String projectId = null;
  @Persistent
  @Expose
  private String creator = null;
  @Persistent
  @Expose
  private Date lastUpdated = null;
  @Persistent
  @Expose
  private Date created = null;
  @Persistent
  @Expose
  private Set<String> tags = new HashSet<String>();
  @Persistent
  @Expose
  private Set<String> contributors = new HashSet<String>();
  @Persistent
  @Expose
  private String title = null;
  @Persistent
  @Expose
  private int blipCount = 0;

  public ForumPost(String domain, Wavelet wavelet) {
    this.domain = domain;
    this.waveId = wavelet.getWaveId().getId();
    this.id = domain + "!" + waveId;
    this.creator = wavelet.getCreator();
    this.lastUpdated = new Date();
    this.created = new Date();
    this.title = wavelet.getTitle();
    this.blipCount = wavelet.getBlips().size();
  }

  public String getId() {
    return id;
  }

  public String getWaveId() {
    return waveId;
  }

  public String getDomain() {
    return domain;
  }

  public Set<String> getTags() {
    return this.tags;
  }

  public void setTags(Set<String> tags) {
    this.tags = tags;
  }

  public void setLastUpdated(Date lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public Date getLastUpdated() {
    return lastUpdated;
  }

  public Date getCreated() {
    return created;
  }

  public String getCreator() {
    return creator;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void addContributor(String userId) {
    if (this.contributors.contains(userId)) {
      return;
    }
    this.contributors.add(userId);
  }

  public Set<String> getContributors() {
    if (this.contributors == null) {
      this.contributors = new HashSet<String>();
    }
    return this.contributors;
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  public String getProjectId() {
    return projectId;
  }

  public void setBlipCount(int blipCount) {
    this.blipCount = blipCount;
  }

  public int getBlipCount() {
    return blipCount;
  }

@Override
public String toString() {
	return "ForumPost [blipCount=" + blipCount + ", contributors="
			+ contributors + ", created=" + created + ", creator=" + creator
			+ ", domain=" + domain + ", id=" + id + ", lastUpdated="
			+ lastUpdated + ", projectId=" + projectId + ", tags=" + tags
			+ ", title=" + title + ", waveId=" + waveId + "]";
}
}
