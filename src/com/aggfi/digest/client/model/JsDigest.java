package com.aggfi.digest.client.model;

import com.aggfi.digest.shared.model.IExtDigest;
import com.google.gwt.core.client.JavaScriptObject;

public class JsDigest extends JavaScriptObject implements IExtDigest {
	
	protected JsDigest(){};

	@Override
	public final native String getAuthor() /*-{
		return this.author;
	}-*/;
	
	

	@Override
	public final native String getDescription() /*-{
		return this.description;
	}-*/;

	@Override
	public final native String getDomain() /*-{
		return this.domain;
	}-*/;

	@Override
	public final native String getForumSiteUrl() /*-{
		return this.forumSiteUrl;
	}-*/;

	@Override
	public final native String getGooglegroupsId() /*-{
		return this.googlegroupsId;
	}-*/;

	@Override
	public final native String getInstallerIconUrl() /*-{
		return this.installerIconUrl;
	}-*/;

	@Override
	public final native String getInstallerThumbnailUrl()/*-{
		return this.installerThumbnailUrl;
	}-*/;

	@Override
	public final native String getInstallerUrl() /*-{
		return this.installerUrl;
	}-*/;

	@Override
	public final native String getName() /*-{
		return this.name;
	}-*/;

	@Override
	public final native String getOwnerId() /*-{
		return this.ownerId;
	}-*/;

	@Override
	public final native String getProjectId()/*-{
		return this.projectId;
	}-*/;

	@Override
	public final native String getRobotThumbnailUrl() /*-{
		return this.robotThumbnailUrl;
	}-*/;

	@Override
	public final native boolean isPublicOnCreate()/*-{
		return this.publicOnCreate;
	}-*/;

	@Override
	public final native void setAuthor(String author) /*-{
		this.author = author;
	}-*/;

	@Override
	public final native void setDescription(String description) /*-{
		this.description = description;
	}-*/;

	@Override
	public final native void setForumSiteUrl(String forumSiteUrl) /*-{
		this.forumSiteUrl = forumSiteUrl;
	}-*/;

	@Override
	public final native void setGooglegroupsId(String googlegroupsId) /*-{
		this.googlegroupsId = googlegroupsId;
	}-*/;

	@Override
	public final native void setInstallerIconUrl(String installerIconUrl) /*-{
		this.installerIconUrl = installerIconUrl;
	}-*/;

	@Override
	public final native void setInstallerThumbnailUrl(String installerThumbnailUrl) /*-{
		this.installerThumbnailUrl = installerThumbnailUrl;
	}-*/;

	@Override
	public final native void setInstallerUrl(String installerUrl) /*-{
		this.installerUrl = installerUrl;
	}-*/;

	@Override
	public final native void setName(String name) /*-{
		this.name = name;
	}-*/;

	@Override
	public final native void setOwnerId(String ownerId) /*-{
		this.ownerId = ownerId;
	}-*/;

	@Override
	public final native void setPublicOnCreate(boolean publicOnCreate) /*-{
		this.publicOnCreate = publicOnCreate;
	}-*/;

	@Override
	public final native void setRobotThumbnailUrl(String robotThumbnailUrl) /*-{
		this.robotThumbnailUrl = robotThumbnailUrl;
	}-*/;



	@Override
	public final native void setProjectId(String projectId) /*-{
		this.projectId = projectId;
	}-*/;



	@Override
	public final native void setDomain(String domain) /*-{
		this.domain = domain;
	}-*/;

	@Override
	public final native void setAdsEnabled(boolean isAdsEnabled) /*-{
		this.isAdsEnabled = isAdsEnabled;
	}-*/;

	@Override
	public final native void setCopyAdSenseFromUser(boolean isCopyAdSenseFromUser) /*-{
		this.isCopyAdSenseFromUser = isCopyAdSenseFromUser;
	}-*/;
	
	
	
	
}
