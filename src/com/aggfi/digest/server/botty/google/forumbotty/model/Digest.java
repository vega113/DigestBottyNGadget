package com.aggfi.digest.server.botty.google.forumbotty.model;

import java.util.Date;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Unique;

import com.google.gson.annotations.Expose;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public class Digest {
  @PrimaryKey
  @Persistent
  @Expose
  private String id = null;

  @Persistent
  @Expose
  private String domain = null;

  @Persistent
  @Expose
  @Unique
  private String waveId = null;

  @Persistent
  @Expose
  @Unique
  private String projectId = null;

  @Persistent
  @Expose
  private Date created = null;

  public Digest(String domain, String waveId, String projectId) {
    this.projectId = projectId;
    this.domain = domain;
    this.waveId = waveId;
    this.id = domain + "!" + waveId;
    created = new Date();
  }

  public String getId() {
    return id;
  }

  public String getDomain() {
    return domain;
  }

  public String getWaveId() {
    return waveId;
  }

  public Date getCreated() {
    return created;
  }

public String getProjectId() {
	return projectId;
}

public void setWaveId(String waveId) {
	this.waveId = waveId;
	 this.id = domain + "!" + waveId;
}

}
