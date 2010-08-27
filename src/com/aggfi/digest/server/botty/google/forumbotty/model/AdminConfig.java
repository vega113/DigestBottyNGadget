/* Copyright (c) 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aggfi.digest.server.botty.google.forumbotty.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import com.google.appengine.api.datastore.Text;

import com.google.gson.annotations.Expose;

@PersistenceCapable(detachable = "true")
public class AdminConfig {

  @PrimaryKey
  @Persistent
  private String id;

  @Expose
  @Persistent
  private List<String> defaultTags;

  @Expose
  @Persistent
  private List<String> defaultParticipants;
  
  @Expose
  @Persistent
  private List<String> managers;

  
  @Expose
  @Persistent
  private Date updated;
  
  @Expose
  @Persistent
  private Date created = null;;
  @Expose
  @Persistent
  Text adsense = null;
  @Expose
  @Persistent
  private Boolean  isAtomFeedPublic = Boolean.FALSE;
  
  public Date getCreated() {
	return created;
}

@Persistent
  @Expose
  private Boolean isAdsEnabled = Boolean.FALSE;
  public boolean isAdsEnabled() {
	return isAdsEnabled != null ?  isAdsEnabled.booleanValue() : false;
  }

  @Expose
  @Persistent(serialized = "true", defaultFetchGroup = "true")
  private Map<String, Pattern> autoTagRegexMap = null;

  public AdminConfig() {
    this.updated = new Date();
    this.created = new Date();
    this.autoTagRegexMap = new HashMap<String, Pattern>();
    this.defaultTags = new ArrayList<String>();
    this.defaultParticipants = new ArrayList<String>();
    this.managers = new ArrayList<String>();
  }

  public void setCreated(Date created) {
	this.created = created;
}

public void setAdsEnabled(boolean isAdsEnabled) {
	this.isAdsEnabled = isAdsEnabled;
}

public AdminConfig(String id) {
    this.id = id;
    this.updated = new Date();
    this.autoTagRegexMap = new HashMap<String, Pattern>();
    this.defaultTags = new ArrayList<String>();
    this.managers = new ArrayList<String>();
  }

  public Date getUpdated() {
    return updated;
  }

  public void setUpdated(Date date) {
    this.updated = date;
  }

  public void setAutoTagRegexMap(Map<String, Pattern> autoTagRegexMap) {
    this.autoTagRegexMap = autoTagRegexMap;
  }

  public Map<String, Pattern> getAutoTagRegexMap() {
    if (this.autoTagRegexMap == null) {
      this.autoTagRegexMap = new HashMap<String, Pattern>();
    }

    return this.autoTagRegexMap;
  }

  public void setDefaultTags(List<String> defaultTags) {
    this.defaultTags = defaultTags;
  }

  public List<String> getDefaultTags() {
    if (this.defaultTags == null) {
      this.defaultTags = new ArrayList<String>();
    }
    return this.defaultTags;
  }

  public String getId() {
    return id;
  }

  public void setDefaultParticipants(List<String> defaultParticipants) {
    this.defaultParticipants = defaultParticipants;
  }

  public List<String> getDefaultParticipants() {
    if (this.defaultParticipants == null) {
      this.defaultParticipants = new ArrayList<String>();
    }

    return this.defaultParticipants;
  }
  
  public void setManagers(List<String> managers) {
	    this.managers = managers;
	  }

	  public List<String> getManagers() {
	    if (this.managers == null) {
	      this.managers = new ArrayList<String>();
	    }

	    return this.managers;
	  }

	public Text getAdsense() {
		return adsense != null ? adsense : new Text("");
	}

	public void setAdsense(Text adsense) {
		this.adsense = adsense;
	}

	public void setAtomFeedPublic(boolean isPublicOnCreate) {
		this.isAtomFeedPublic = isPublicOnCreate;
		
	}
	
	public Boolean isAtomFeedPublic(){
		return isAtomFeedPublic;
	}
}