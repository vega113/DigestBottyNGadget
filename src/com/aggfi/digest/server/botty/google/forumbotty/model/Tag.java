package com.aggfi.digest.server.botty.google.forumbotty.model;

import java.util.Date;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.gson.annotations.Expose;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class Tag {
  @PrimaryKey
  @Persistent
  @Expose
  private String id = null;

  @Persistent
  @Expose
  private String projectId = null;  
  
  @Persistent
  @Expose
  private String tag = null;  
  
  @Persistent
  @Expose
  private int count = 0;

  @Persistent
  @Expose
  private Date created = null;

  @Persistent
  @Expose
  private Date updated = null;

  public Tag(String projectId, String tag) {
    this.id = projectId + "__" + tag;
    this.projectId = projectId;
    this.tag = tag;
    created = new Date();
    updated = created;
  }

  public String getId() {
    return id;
  }

  public String toString() {
    return id;
  }
  
  public String getTag() {
    return tag;
  }
  
  public Date getUpdated() {
    return updated;
  }

  public Date getCreated() {
    return created;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public int getCount() {
    return count;
  }

}
