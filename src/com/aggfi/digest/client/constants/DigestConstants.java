

package com.aggfi.digest.client.constants;

import com.google.gwt.i18n.client.Constants;
import com.google.gwt.i18n.client.Constants.DefaultIntValue;

/**
 * Interface that can be used to read constants from a properties file.
 *
 * Instances of this interface can be requested/injected by Gin without an
 * explicit binding: Gin will internallt call GWT.create on the requested type.
 */
public interface DigestConstants extends Constants {
	
	String updateForumInfoBtnExpl();
	String forumSettingsStr();
	String createBtnStr();
	String viewsCounterInstallerExpl();
	String createViewsCounterInstallerQuestionStr();
	String submitStr();
	String submitAdSenseBtnExpl();
	String createAdInstallerExpl();
	String createAdInstallerStr();
	String enableBtnName();
	String trackViewsExpl();
	String trackViewsQuestionStr();
	String trackerSettingsStr();

	String createTabStr();
	String adminTabStr();
	String reportTabStr();
	String aboutTabStr();
	String adsenseTabStr();
	String personalTabStr();
	
	String aboutCreateStr();
	String aboutDigestBottyStr();
	String aboutAdminStr();
	String aboutReportStr();
	
	String aboutDigestBottyCaptionStr();
	String aboutCreateCaptionStr();
	String aboutHlpCaptionStr();
	String contactInfoCaptionStr();
	
	String contactInfoValue1Msg();
	String contactInfoValue2Msg();
	String contactInfoValue3Msg();
	String contactInfoValue4Msg();
	String contactInfoValue5Msg();
	
	String instructionsHeaderStr();
	
	//warning
	String noForumSelectedWarning();
	
	String ownerStr();

	String authorStr();

	String projectIdStr();

	String domainStr();

	String digestNameStr();

	String descriptionStr();

	String installerThumbnailUrlStr();

	String toolbarIconUrlStr();

	String robotThumbnailUrlStr();

	String forumSiteUrlStr();

	String googlegroupsIdStr();

	String submitBtnStr();

	String outputStr();
	
	String ownerTitle();

	String authorTitle();

	String projectIdTitle();

	String domainTitle();

	String digestNameTitle();

	String descriptionTitle();

	String installerThumbnailUrlTitle();

	String toolbarIconUrlTitle();

	String robotThumbnailUrlTitle();

	String forumSiteUrlTitle();

	String googlegroupsIdTitle();
	
	String googlegroupsWarningStr();

	String fieldNameStr();

	String fieldValueStr();

	String fieldExampleStr();

	String ownerExmpl();

	String authorExmpl();

	String projectIdExmpl();

	String domainExmpl();

	String digestNameExmpl();

	String descriptionExmpl();

	String installerThumbnailUrlExmpl();

	String toolbarIconUrlExmpl();

	String robotThumbnailUrlExmpl();

	String forumSiteUrlExmpl();

	String googlegroupsIdExmpl();
	
	String managerWaveIdFieldName();
	String defaultParticipantWaveIdFieldName();
	String participantWaveIdFieldName();
	String waveIdFromExmpl();

	String isPublicQuestion();
	String isAdsEnabledQuestion();
	String isAdsEnabledQuestionTtl();
	String isPublicQuestionTtl();
	
	String exampleWord();
	
	String publicStr();
	String privateStr();
	String successStr();
	String isAtomFeedPublicTagExpl();
	String addSecureGadgetExpl();
	String addSecureGadgetQuestionStr();
	String secureGadgetStr();
	
	String digg();
	String buzz();
	String tweet();
	String face();
	String addSocialBtnsQuestion();
	String addSocialBtnstExpl();
	
	String participantSettingsStr();
	String generalSettingsStr();
	
	String refreshChartStr();
	String refreshPrjsList();
	
	String userStr();
	String forumStr();
	
	String goToWaveStr();

	/**
	 * Google Apps domain Id of the DigestBotty
	 * @return digestbotty.appspot.com
	 */
	
	@DefaultStringValue(value="Request Status Info")
	String createDigestRequestStatus();

	//ui titles
	
	String removeMeStr();
	String addBtnName();
	
	String selectDigestStr();
	String selectReportTypeStr();
	String setUpDefaultParticipantsStr();
	String participantsStr();
	String setUpDefaultTagsStr();
	String setUpAutoTagging();
	String setUpDigestManagers();
	String setUpParticipantWaves();
	
	String defaultParticipantsStr();
	String defaultTagsStr();
	String autoTagging();
	String digestManagers();
	
	String tagStr();
	String regexStr();
	String syncStr();
	String setUpAtomFeedStr();
	String makeTheFeedPublicQuestionStr();
	String updateStr();
	
	String newWavesLast14Days();
	String breakDown4AllTags();
	
	String retrieveingDataStr();
	String waitingForResponseStr();
	
	String defaultTagExpl();
	String autoTagExpl();
	String autoTagNameExpl();
	String autoTagSyncExpl();
	String managersExpl();
	String defaultParticipantExpl();
	String participantWavesExpl();
	String participantWavesTagExpl();
	
	String helloUserStr();
	String pleaseSelectMode();
	String viewModeStr();
	String editModeStr();
	
	String adsenseCodeCaptionStr();
	
	String isCopyAdsQuestion();
	
	String personalAdSenseExpl();
	String forumAdSenseExpl();
	
	String copyProjAdsense4PersonalQuestionStr();
	String copyProjAdsense4PersonalQuestionExpl();
	String copyBtnName();
	String personalAdsenseCodeIsEmpty();
	
	String loadingStr();
	String isCopyAdsQuestionTtl();
	String isCopyAdsQuestionTtlNone();
	
	String goStr();
	
	String loadingVisualizationsApiStr();
	
	@DefaultStringValue(value="digestbotty") //Change it when switching domains
	String appDomain();
	//urls
	@DefaultStringValue(value="#restored:wave:googlewave.com%252Fw%252B0R06HrZkBhE%252F")
	String discussDigestBottyUrl();
	@DefaultStringValue(value="#restored:wave:googlewave.com%252Fw%252BKNw8wPWXA")
	String installDigestBottyUrl();
	
	@DefaultStringValue(value="UA-13269470-3")
	String trackerUIStr();
	//size
	/** width of the whole gadget*/
	@DefaultStringValue(value="540px")//change both
	String basicWidthStr();
	@DefaultIntValue(value=540)//change both
	int basicWidthInt();
	
	/**  width of scroll panel */
	@DefaultStringValue(value="496px")
	String smallerWidthStr();
	/** widht of create panel*/
	@DefaultStringValue(value="510px")
	String smallerBiggerWidthStr();
	@DefaultStringValue(value="530px")
	String createWidthStr();
	@DefaultStringValue(value="502px")
	String smaller2BiggerWidthStr();
	
	@DefaultIntValue(value=320)
	int basicReportHeightInt();
	@DefaultStringValue(value="72px")
	String basicItemScrollHeightStr();
	/**width of AddRemDefLabel*/
	@DefaultStringValue(value="210px")
	String basicItemWidthStr();
	/** width of input text boxes on admin tab*/
	@DefaultStringValue(value="164px")
	String basicTextBoxWidthStr();
	@DefaultIntValue(value=25)
	int itemCreateHeight();
	@DefaultStringValue(value="238px")
	String aboutTabScrollHeight();
	@DefaultStringValue(value="300px")
	String createPnlQuestionsWidth();
	@DefaultStringValue(value="380px")
	String reportListWidthStr();
	
	
	
}
