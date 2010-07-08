

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

	String createTabStr();
	String adminTabStr();
	String reportTabStr();
	String aboutTabStr();
	
	String aboutCreateStr();
	String aboutDigestBottyStr();
	String aboutAdminStr();
	String aboutReportStr();
	
	String aboutDigestBottyCaptionStr();
	String aboutCreateCaptionStr();
	String aboutHlpCaptionStr();
	String contactInfoCaptionStr();
	
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
	
	@DefaultStringValue(value="Manager Wave Id")
	String managerWaveIdFieldName();
	@DefaultStringValue(value="Default Participant Wave Id")
	String defaultParticipantWaveIdFieldName();
	@DefaultStringValue(value="The field should be in the form of \"your_wave_id@googlewave.com\"")
	String waveIdFromExmpl();

	String isPublicQuestion();

	String isPublicQuestionTtl();
	
	String exampleWord();

	/**
	 * Google Apps domain Id of the DigestBotty
	 * @return digestbotty.appspot.com
	 */
	
	@DefaultStringValue(value="Request Status Info")
	String createDigestRequestStatus();

	//ui titles
	
	String removeMeStr();
	@DefaultStringValue(value="add")
	String addBtnName();
	
	String selectDigestStr();
	String selectReportTypeStr();
	String setUpDefaultParticipantsStr();
	String setUpDefaultTagsStr();
	String setUpAutoTagging();
	String setUpDigestManagers();
	
	String tagStr();
	String regexStr();
	
	String newWavesLast14Days();
	String breakDown4AllTags();
	
	String retrieveingDataStr();
	String waitingForResponseStr();
	
	String defaultTagExpl();
	String autoTagExpl();
	String managersExpl();
	String defaultParticipantExpl();
	
	//urls
	String discussDigestBottyUrl();
	String installDigestBottyUrl();
	
	//size
	@DefaultStringValue(value="520px")//change both
	String basicWidthStr();
	@DefaultIntValue(value=520)//change both
	int basicWidthInt();
	
	/**  width of scroll panel */
	@DefaultStringValue(value="496px")
	String smallerWidthStr();
	/** widht of create panel*/
	@DefaultStringValue(value="510px")
	String smallerBiggerWidthStr();
	
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
	@DefaultStringValue(value="90px")
	String aboutTabScrollHeight();
	
}
