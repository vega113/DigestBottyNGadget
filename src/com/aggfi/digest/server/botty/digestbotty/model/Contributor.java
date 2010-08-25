package com.aggfi.digest.server.botty.digestbotty.model;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;
import com.google.gson.annotations.Expose;

public class Contributor {
	@Expose
	@Persistent
	@PrimaryKey
	private String participantId  = null;
	@Expose
	@Persistent
	private String firstName = null;
	@Expose
	@Persistent
	private String familyName = null;
	@Expose
	@Persistent
	private String fullName = null;
	@Expose
	@Persistent
	private String email = null;
	@Persistent
	@Expose
	private Text bio = null;
	@Expose
	@Persistent
	private String iconUrl = null;
	@Expose
	@Persistent
	private String website = null;
	@Expose
	@Persistent
	private String pubId = null;
	@Expose
	@Persistent
	private int pubFetchedCount = 0;
	@Expose
	@Persistent(serialized = "true", defaultFetchGroup = "true")
	private Map<String,Object> props  = null;
	@Expose
	@Persistent
	private Date created = null;
	@Expose
	@Persistent
	private Date updated = null;
	@Override
	public String toString() {
		final int maxLen = 10;
		StringBuilder builder = new StringBuilder();
		builder.append("Contributor [participantId=");
		builder.append(participantId);
		builder.append(", firstName=");
		builder.append(firstName);
		builder.append(", familyName=");
		builder.append(familyName);
		builder.append(", fullName=");
		builder.append(fullName);
		builder.append(", email=");
		builder.append(email);
		builder.append(", bio=");
		builder.append(bio);
		builder.append(", iconUrl=");
		builder.append(iconUrl);
		builder.append(", website=");
		builder.append(website);
		builder.append(", pubId=");
		builder.append(pubId);
		builder.append(", pubFetchedCount=");
		builder.append(pubFetchedCount);
		builder.append(", props=");
		builder.append(props != null ? toString(props.entrySet(), maxLen)
				: null);
		builder.append(", created=");
		builder.append(created);
		builder.append(", updated=");
		builder.append(updated);
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
	public Contributor(String participantId) {
		super();
		this.participantId = participantId;
	}
	public String getParticipantId() {
		return participantId;
	}
	public void setParticipantId(String participantId) {
		this.participantId = participantId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getFamilyName() {
		return familyName;
	}
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Text getBio() {
		return bio;
	}
	public void setBio(Text bio) {
		this.bio = bio;
	}
	public String getIconUrl() {
		return iconUrl;
	}
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getPubId() {
		return pubId;
	}
	public void setPubId(String pubId) {
		this.pubId = pubId;
	}
	public int getPubFetchedCount() {
		return pubFetchedCount;
	}
	public void setPubFetchedCount(int pubFetchedCount) {
		this.pubFetchedCount = pubFetchedCount;
	}
	public Map<String, Object> getProps() {
		return props;
	}
	public void setProps(Map<String, Object> props) {
		this.props = props;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getUpdated() {
		return updated;
	}
	public void setUpdated(Date updated) {
		this.updated = updated;
	}
}
