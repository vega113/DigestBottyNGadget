package com.aggfi.digest.server.botty.digestbotty.model;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.gson.annotations.Expose;
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class TrackerEvent {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@Expose
	private Long id;
	@Persistent
	@Expose
	private String sourceId;
	@Persistent
	@Expose
	private Date created;
	@Persistent
	@Expose
	private String projectId;
	@Persistent
	@Expose
	private String waveId;
	@Persistent
	@Expose
	private String eventType;
	@Persistent
	@Expose
	private String eventValue;
	@Persistent(serialized = "true", defaultFetchGroup = "true")
	@Expose
	private Map<String,String> properties;
	
	public TrackerEvent(String sourceId, String projectId, String waveId,
			String eventType) {
		
		this.sourceId = sourceId;
		this.projectId = projectId;
		this.waveId = waveId;
		this.eventType = eventType;
		this.created = new Date();
		this.eventValue = null;
		
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getWaveId() {
		return waveId;
	}

	public void setWaveId(String waveId) {
		this.waveId = waveId;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getEventValue() {
		return eventValue;
	}

	public void setEventValue(String eventValue) {
		this.eventValue = eventValue;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		StringBuilder builder = new StringBuilder();
		builder.append("TrackerEvent [id=");
		builder.append(id);
		builder.append(", sourceId=");
		builder.append(sourceId);
		builder.append(", created=");
		builder.append(created);
		builder.append(", projectId=");
		builder.append(projectId);
		builder.append(", waveId=");
		builder.append(waveId);
		builder.append(", eventType=");
		builder.append(eventType);
		builder.append(", eventValue=");
		builder.append(eventValue);
		builder.append(", properties=");
		builder.append(properties != null ? toString(properties.entrySet(),
				maxLen) : null);
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
