package com.aggfi.digest.server.botty.google.forumbotty.model;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;
import com.google.gson.annotations.Expose;
import com.google.wave.api.Annotation;
import com.google.wave.api.Range;
import com.google.wave.api.Wavelet;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class ForumPost {
	
  @SuppressWarnings("unused")
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Long primaryKey;
	
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
  @Expose
  private String digestBlipId = null;
  @Persistent
  @Expose
  private String creator = null;
  @Expose
  private String updater = null;
  @Persistent
  @Expose
  private Date lastUpdated = null;
  @Persistent
  @Expose
  private Date created = null;
  @Persistent
  @Expose
  private Integer rootBlipsWithoutAdCount = 0;
  @Persistent
  @Expose
  private Boolean isCreatedByRobot;
  

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
  @Persistent
  @Expose
  private Text firstBlipContent = null;
  @Persistent
  @Expose
  private Boolean isDispayAtom;
  

  public ForumPost(String domain, Wavelet wavelet) {
    this.domain = wavelet.getWaveId().getDomain();
    this.waveId = wavelet.getWaveId().getId();
    this.id =  UUID.randomUUID().toString();
    this.primaryKey = System.currentTimeMillis();
    
  //handle the case when created with new post gadget
    
    
    if(wavelet.getRootBlip().getCreator() == null || wavelet.getRootBlip().getCreator().indexOf("gwave.com") > 0 || wavelet.getRootBlip().getCreator().indexOf("appspot.com") > 0){
    	this.isCreatedByRobot = true;
    }else{
    	this.isCreatedByRobot = false;
    	this.creator = wavelet.getRootBlip().getCreator();
    }
    
   if(isCreatedByRobot){
	   if(wavelet.getDataDocuments().contains("creator")){
			this.setCreator(wavelet.getDataDocuments().get(System.getProperty("APP_DOMAIN") + ".appspot.com/creator")); 
		}else{
			List<String> contributors = wavelet.getRootBlip().getContributors();
			for(String contributor : contributors){
				if(contributor.indexOf("gwave.com") < 0 && contributor.indexOf("appspot.com") < 0){
					this.setCreator(contributor);
					break;
				}
			}
		}
   }
    
    this.lastUpdated = new Date();
    this.created = new Date();
    this.title = wavelet.getTitle();
    this.blipCount = wavelet.getBlips().size();
    String backtodigestAnnotationName = System.getProperty("APP_DOMAIN") + ".appspot.com/backtodigest#" + projectId;
	String backtodigestAnnotationNameOld = System.getProperty("APP_DOMAIN") + ".appspot.com/backtodigest";
    List<Annotation> annonList = wavelet.getRootBlip().getAnnotations().get(backtodigestAnnotationName);
    if(annonList == null || annonList.size() == 0){
    	annonList =  wavelet.getRootBlip().getAnnotations().get(backtodigestAnnotationNameOld);
    }
    Range range = null;
    if(annonList != null && annonList.size() > 0){
    	range = annonList.get(0).getRange();
    	this.firstBlipContent = new Text(wavelet.getRootBlip().getContent().replace(wavelet.getRootBlip().getContent().substring(range.getStart(), range.getEnd()), "")); 
    }else{
    	this.firstBlipContent = new Text(wavelet.getRootBlip().getContent()); 
    }
    this.isDispayAtom = true;
  }

  public String getId() {
    return domain + "!" + waveId;
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



public String getUpdater() {
	return updater;
}

public void setUpdater(String updater) {
	this.updater = updater;
}

public String getDigestBlipId() {
	return digestBlipId;
}

public void setDigestBlipId(String digestBlipId) {
	this.digestBlipId = digestBlipId;
}

public Text getFirstBlipContent() {
	return firstBlipContent;
}

public void setFirstBlipContent(Text firstBlipContent) {
	this.firstBlipContent = firstBlipContent;
}

public int getRootBlipsWithoutAdCount() {
	return rootBlipsWithoutAdCount != null ? rootBlipsWithoutAdCount : 0;
}

public void setRootBlipsWithoutAdCount(int rootBlipsWithoutAdCount) {
	this.rootBlipsWithoutAdCount = rootBlipsWithoutAdCount;
}

public void setCreator(String creator) {
	this.creator = creator;
}

public Boolean isDispayAtom() {
	return isDispayAtom != null ? isDispayAtom : true;
}

public void setDispayAtom(Boolean isDispayAtom) {
	this.isDispayAtom = isDispayAtom;
}

public Long getPrimaryKey() {
	return primaryKey;
}

public void setPrimaryKey(Long primaryKey) {
	this.primaryKey = primaryKey;
}

public void setId(String id) {
	this.id = id;
}

@Override
public String toString() {
	final int maxLen = 10;
	StringBuilder builder = new StringBuilder();
	builder.append("ForumPost [primaryKey=");
	builder.append(primaryKey);
	builder.append(", id=");
	builder.append(id);
	builder.append(", domain=");
	builder.append(domain);
	builder.append(", waveId=");
	builder.append(waveId);
	builder.append(", projectId=");
	builder.append(projectId);
	builder.append(", digestBlipId=");
	builder.append(digestBlipId);
	builder.append(", creator=");
	builder.append(creator);
	builder.append(", updater=");
	builder.append(updater);
	builder.append(", lastUpdated=");
	builder.append(lastUpdated);
	builder.append(", created=");
	builder.append(created);
	builder.append(", rootBlipsWithoutAdCount=");
	builder.append(rootBlipsWithoutAdCount);
	builder.append(", tags=");
	builder.append(tags != null ? toString(tags, maxLen) : null);
	builder.append(", contributors=");
	builder.append(contributors != null ? toString(contributors, maxLen) : null);
	builder.append(", title=");
	builder.append(title);
	builder.append(", blipCount=");
	builder.append(blipCount);
//	builder.append(", firstBlipContent=");
//	builder.append(firstBlipContent);
	builder.append(", isDispayAtom=");
	builder.append(isDispayAtom);
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

public Boolean isCreatedByRobot() {
	return isCreatedByRobot;
}

public void setCreatedByRobot(Boolean isCreatedByRobot) {
	this.isCreatedByRobot = isCreatedByRobot;
}

public Object getRealId() {
	return id;
}


}
