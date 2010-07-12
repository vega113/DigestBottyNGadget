package com.aggfi.digest.client.feature.minimessages;

import com.google.gwt.gadgets.client.GadgetFeature.FeatureName;


/**
 * Indicates that a Gadget requires the Google Finance feature.
 */
@FeatureName("minimessage")
public interface NeedsMiniMessages {
  /**
   * Entry point that gets called back to handle wave feature initialization.
   *
   * @param feature
   *          an instance of the feature to use to invoke feature specific
   *          methods.
   */
  void initializeFeature(MiniMessagesFeature feature);
}
