/*
 * Copyright 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.aggfi.digest.client.constants;

import com.google.gwt.i18n.client.Constants;

/**
 * Interface that can be used to read constants from a properties file.
 *
 * Instances of this interface can be requested/injected by Gin without an
 * explicit binding: Gin will internallt call GWT.create on the requested type.
 */
public interface SimpleConstants extends Constants {

	String instructionsHeaderStr();
	String instrutctionsStr();

	
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
	
	

	String isPublicQuestion();

	String isPublicQuestionTtl();
	
	String exampleWord();

	/**
	 * Google Apps domain Id of the DigestBotty
	 * @return digestbotty.appspot.com
	 */
	@DefaultStringValue(value="aggfiwave.appspot.com")
	String appDomain();
	
	@DefaultIntValue(value=450)
	public int valueWidgetSize();
	
	@DefaultStringValue(value="Request Status Info")
	String createDigestRequestStatus();

	//ui titles
	
	String removeMeStr();
}
