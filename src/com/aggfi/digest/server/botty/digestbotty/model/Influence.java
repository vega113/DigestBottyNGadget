package com.aggfi.digest.server.botty.digestbotty.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.aggfi.digest.server.botty.digestbotty.utils.InfluenceUtils;
import com.google.gson.annotations.Expose;
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class Influence implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@Expose
	private Long id;
	@Expose
	@Persistent
	String projectId;
	@Expose
	@Persistent
	Date forDate;
	@Expose
	@Persistent
	String forDateStr;
	@Expose
	@Persistent(serialized = "true", defaultFetchGroup = "true")
	Map<String,Double> influenceMap;
	public Influence(String projectId, Date forDate,
			Map<String, Double> influenceMap) {
		super();
		this.projectId = projectId;
		this.forDate = forDate;
		this.influenceMap = influenceMap;
		this.forDateStr = InfluenceUtils.getSdf().format(forDate);
	}
	@Override
	public String toString() {
		final int maxLen = 10;
		StringBuilder builder = new StringBuilder();
		builder.append("Influence [id=");
		builder.append(id);
		builder.append(", projectId=");
		builder.append(projectId);
		builder.append(", forDate=");
		builder.append(forDate);
		builder.append(", forDateStr=");
		builder.append(forDateStr);
		builder.append(", influenceMap=");
		builder.append(influenceMap != null ? toString(influenceMap.entrySet(),
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
	public String getForDateStr() {
		return forDateStr;
	}
	public void setForDateStr(String forDateStr) {
		this.forDateStr = forDateStr;
	}
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public Date getForDate() {
		return forDate;
	}
	public void setForDate(Date forDate) {
		this.forDate = forDate;
	}
	public Map<String, Double> getInfluenceMap() {
		return influenceMap;
	}
	public void setInfluenceMap(Map<String, Double> influenceMap) {
		this.influenceMap = influenceMap;
	}
}
