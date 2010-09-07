

package com.aggfi.digest.server.botty.google.forumbotty.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
  
  @Expose
  @Persistent
  private Boolean  isDiggBtnEnabled = Boolean.FALSE;
  
  @Expose
  @Persistent
  private Boolean  isBuzzBtnEnabled = Boolean.FALSE;
  
  @Expose
  @Persistent
  private Boolean  isTweetBtnEnabled = Boolean.FALSE;
  
  @Expose
  @Persistent
  private Boolean  isFaceBtnEnabled = Boolean.FALSE;
  
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

	public Boolean isDiggBtnEnabled() {
		return isDiggBtnEnabled;
	}

	public void setDiggBtnEnabled(Boolean isDiggBtnEnabled) {
		this.isDiggBtnEnabled = isDiggBtnEnabled;
	}

	public Boolean isBuzzBtnEnabled() {
		return isBuzzBtnEnabled;
	}

	public void setBuzzBtnEnabled(Boolean isBuzzBtnEnabled) {
		this.isBuzzBtnEnabled = isBuzzBtnEnabled;
	}

	public Boolean isTweetBtnEnabled() {
		return isTweetBtnEnabled;
	}

	public void setTweetBtnEnabled(Boolean isTweetBtnEnabled) {
		this.isTweetBtnEnabled = isTweetBtnEnabled;
	}

	public Boolean isFaceBtnEnabled() {
		return isFaceBtnEnabled;
	}

	public void setFaceBtnEnabled(Boolean isFaceBtnEnabled) {
		this.isFaceBtnEnabled = isFaceBtnEnabled;
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		StringBuilder builder = new StringBuilder();
		builder.append("AdminConfig [id=");
		builder.append(id);
		builder.append(", defaultTags=");
		builder.append(defaultTags != null ? toString(defaultTags, maxLen)
				: null);
		builder.append(", defaultParticipants=");
		builder.append(defaultParticipants != null ? toString(
				defaultParticipants, maxLen) : null);
		builder.append(", managers=");
		builder.append(managers != null ? toString(managers, maxLen) : null);
		builder.append(", updated=");
		builder.append(updated);
		builder.append(", created=");
		builder.append(created);
		builder.append(", adsense=");
		builder.append(adsense);
		builder.append(", isAtomFeedPublic=");
		builder.append(isAtomFeedPublic);
		builder.append(", isDiggBtnEnabled=");
		builder.append(isDiggBtnEnabled);
		builder.append(", isBuzzBtnEnabled=");
		builder.append(isBuzzBtnEnabled);
		builder.append(", isTweetBtnEnabled=");
		builder.append(isTweetBtnEnabled);
		builder.append(", isFaceBtnEnabled=");
		builder.append(isFaceBtnEnabled);
		builder.append(", isAdsEnabled=");
		builder.append(isAdsEnabled);
		builder.append(", autoTagRegexMap=");
		builder.append(autoTagRegexMap != null ? toString(
				autoTagRegexMap.entrySet(), maxLen) : null);
		builder.append("]");
		return builder.toString();
	}

	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext()
				&& i < maxLen; i++) {
			if (i > 0)
				builder.append(", ");
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}
}