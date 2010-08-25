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

package com.aggfi.digest.client.inject;
import org.cobogw.gwt.waveapi.gadget.client.WaveFeature;

import com.aggfi.digest.client.DigestBottyGadget;
import com.aggfi.digest.client.service.DigestServiceImpl;
import com.aggfi.digest.client.service.DigestService;
import com.google.gwt.gadgets.client.DynamicHeightFeature;
import com.google.gwt.gadgets.client.GoogleAnalyticsFeature;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import com.vegalabs.features.client.feature.minimessages.MiniMessagesFeature;
import com.vegalabs.features.client.feature.views.ViewsFeature;
import com.vegalabs.features.client.request.WaveRequestServiceImpl;
import com.vegalabs.features.client.utils.WaveVegaUtilsImpl;
import com.vegalabs.general.client.objects.GoogleAnalyticsId;
import com.vegalabs.general.client.request.RequestService;
import com.vegalabs.general.client.utils.VegaUtils;

/**
 * This gin module binds an implementation for the
 * {@link com.google.gwt.inject.example.simple.client.SimpleService} used in
 * this example application. Note that we don't have to bind implementations
 * for {@link com.google.gwt.inject.DigestConstants.simple.client.SimpleConstants} and
 * {@link com.google.gwt.inject.DigestMessages.simple.client.SimpleMessages} - they
 * are constructed by Gin through GWT.create.
 */
public class DigestGinModule extends AbstractGinModule {

	protected void configure() {

		bind(DigestService.class).to(DigestServiceImpl.class);
		bind(VegaUtils.class).to(WaveVegaUtilsImpl.class);
		bind(RequestService.class).to(WaveRequestServiceImpl.class).in(Singleton.class);

		bind(WaveFeature.class).toProvider(DigestBottyGadget.WaveFeatureProvider.class).in(Singleton.class);
		bind(GoogleAnalyticsFeature.class).toProvider(DigestBottyGadget.AnalyticsFeatureProvider.class).in(Singleton.class);
		bind(MiniMessagesFeature.class).toProvider(DigestBottyGadget.MiniMessagesFeatureProvider.class).in(Singleton.class);
		bind(DynamicHeightFeature.class).toProvider(DigestBottyGadget.DynamicHeightFeatureProvider.class).in(Singleton.class);
		bind(ViewsFeature.class).toProvider(DigestBottyGadget.ViewsFeatureProvider.class).in(Singleton.class);
		bind(GoogleAnalyticsId.class).toProvider(DigestBottyGadget.AnalyticsIdFeatureProvider.class).in(Singleton.class);
	}
}
