package com.aggfi.digest.shared.model;

public interface IExtDigest extends IDigest{

	/* (non-Javadoc)
	 * @see com.aggfi.digestbotty.gadget.shared.model.IExtDigest#getDescription()
	 */
	public abstract String getDescription();

	/* (non-Javadoc)
	 * @see com.aggfi.digestbotty.gadget.shared.model.IExtDigest#setDescription(java.lang.String)
	 */
	public abstract void setDescription(String description);

	/* (non-Javadoc)
	 * @see com.aggfi.digestbotty.gadget.shared.model.IExtDigest#getName()
	 */
	public abstract String getName();

	/* (non-Javadoc)
	 * @see com.aggfi.digestbotty.gadget.shared.model.IExtDigest#setName(java.lang.String)
	 */
	public abstract void setName(String name);

	/* (non-Javadoc)
	 * @see com.aggfi.digestbotty.gadget.shared.model.IExtDigest#getInstallerThumbnailUrl()
	 */
	public abstract String getInstallerThumbnailUrl();

	/* (non-Javadoc)
	 * @see com.aggfi.digestbotty.gadget.shared.model.IExtDigest#setInstallerThumbnailUrl(java.lang.String)
	 */
	public abstract void setInstallerThumbnailUrl(String installerThumbnailUrl);

	/* (non-Javadoc)
	 * @see com.aggfi.digestbotty.gadget.shared.model.IExtDigest#getInstallerIconUrl()
	 */
	public abstract String getInstallerIconUrl();

	/* (non-Javadoc)
	 * @see com.aggfi.digestbotty.gadget.shared.model.IExtDigest#setInstallerIconUrl(java.lang.String)
	 */
	public abstract void setInstallerIconUrl(String installerIconUrl);

	/* (non-Javadoc)
	 * @see com.aggfi.digestbotty.gadget.shared.model.IExtDigest#getRobotThumbnailUrl()
	 */
	public abstract String getRobotThumbnailUrl();

	/* (non-Javadoc)
	 * @see com.aggfi.digestbotty.gadget.shared.model.IExtDigest#setRobotThumbnailUrl(java.lang.String)
	 */
	public abstract void setRobotThumbnailUrl(String robotThumbnailUrl);

	/* (non-Javadoc)
	 * @see com.aggfi.digestbotty.gadget.shared.model.IExtDigest#getForumSiteUrl()
	 */
	public abstract String getForumSiteUrl();

	/* (non-Javadoc)
	 * @see com.aggfi.digestbotty.gadget.shared.model.IExtDigest#setForumSiteUrl(java.lang.String)
	 */
	public abstract void setForumSiteUrl(String forumSiteUrl);

	/* (non-Javadoc)
	 * @see com.aggfi.digestbotty.gadget.shared.model.IExtDigest#getGooglegroupsId()
	 */
	public abstract String getGooglegroupsId();

	/* (non-Javadoc)
	 * @see com.aggfi.digestbotty.gadget.shared.model.IExtDigest#setGooglegroupsId(java.lang.String)
	 */
	public abstract void setGooglegroupsId(String googlegroupsId);

	/* (non-Javadoc)
	 * @see com.aggfi.digestbotty.gadget.shared.model.IExtDigest#getOwnerId()
	 */
	public abstract String getOwnerId();

	/* (non-Javadoc)
	 * @see com.aggfi.digestbotty.gadget.shared.model.IExtDigest#setOwnerId(java.lang.String)
	 */
	public abstract void setOwnerId(String ownerId);

	/* (non-Javadoc)
	 * @see com.aggfi.digestbotty.gadget.shared.model.IExtDigest#getAuthor()
	 */
	public abstract String getAuthor();

	/* (non-Javadoc)
	 * @see com.aggfi.digestbotty.gadget.shared.model.IExtDigest#setAuthor(java.lang.String)
	 */
	public abstract void setAuthor(String author);

	/* (non-Javadoc)
	 * @see com.aggfi.digestbotty.gadget.shared.model.IExtDigest#getInstallerUrl()
	 */
	public abstract String getInstallerUrl();

	/* (non-Javadoc)
	 * @see com.aggfi.digestbotty.gadget.shared.model.IExtDigest#setInstallerUrl(java.lang.String)
	 */
	public abstract void setInstallerUrl(String installerUrl);

	/* (non-Javadoc)
	 * @see com.aggfi.digestbotty.gadget.shared.model.IExtDigest#isPublicOnCreate()
	 */
	public abstract boolean isPublicOnCreate();

	public abstract void setPublicOnCreate(boolean isPublicOnCreate);

}