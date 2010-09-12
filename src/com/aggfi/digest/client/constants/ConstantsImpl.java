

package com.aggfi.digest.client.constants;

import com.google.gwt.i18n.client.Constants;
import com.google.gwt.i18n.client.Constants.DefaultIntValue;
import com.google.gwt.i18n.client.Constants.DefaultStringValue;

/**
 * Interface that can be used to read constants from a properties file.
 *
 * Instances of this interface can be requested/injected by Gin without an
 * explicit binding: Gin will internallt call GWT.create on the requested type.
 */
public interface ConstantsImpl extends Constants {


	String postsByViews14Days();
	String viewsCountLast14Days();
	
	//-------------------------------------
	String newWavesLast14Days();
	String breakDown4AllTags();
	String reportTabStr();
	String noForumSelectedWarning();
	String selectReportTypeStr();
	String selectDigestStr();
	String newBlipsLast14Days();
	String activeContributors14Days();
	String influenceContributors14Days();
	String postsByActivity14Days();
	
	
	@DefaultStringValue(value="digestbotty")
	String appDomain();
	//urls
	@DefaultStringValue(value="#restored:wave:googlewave.com%252Fw%252BVIL2ZrZkBSb")
	String discussDigestBottyUrl();
	@DefaultStringValue(value="#restored:search:group%253Agoogle-wave-extension-gallery-all%2540googlegroups.com,restored:wave:googlewave.com%252Fw%252B0R06HrZkCAQ")
	String installDigestBottyUrl();
	
	@DefaultStringValue(value="UA-13269470-3")
	String trackerUIStr();
	//size
	/** width of the whole gadget*/
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
	@DefaultStringValue(value="238px")
	String aboutTabScrollHeight();
	
	@DefaultStringValue(value="UA-13269470-4")
	String ANALYTICS_ID();
	
	
	
	
	
	
	

}
