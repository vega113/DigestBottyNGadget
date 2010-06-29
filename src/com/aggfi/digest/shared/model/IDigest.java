package com.aggfi.digest.shared.model;

public interface IDigest {

	public abstract String getDomain();

	public abstract String getProjectId();

	public abstract void setDomain(String domain);

	public abstract void setProjectId(String projectId);

}