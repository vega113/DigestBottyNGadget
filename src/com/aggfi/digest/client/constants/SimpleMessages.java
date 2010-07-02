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

import com.google.gwt.i18n.client.Messages;

/**
 * Interface used to provide messages with parameters from a properties file.
 *  
 * Instances of this interface can be requested/injected by Gin without an
 * explicit binding: Gin will internallt call GWT.create on the requested type.
 */
public interface SimpleMessages extends Messages {

  String missingCreationParamExcptn(String msg);
  
  String newDigestPostMenuStr(String digestName);
  String addDigestFromToolbarStr(String digestName);
  
  String incorrectFormParamExcptn(String param, String tmplt);
  String removingMsg(String participant);

  String removalSuccessMsg(String participantId);

  String fieldShouldBeAlphaNumericExcptn(String fieldname);
}
