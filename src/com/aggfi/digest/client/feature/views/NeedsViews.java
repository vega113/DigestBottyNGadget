package com.aggfi.digest.client.feature.views;

import com.google.gwt.gadgets.client.GadgetFeature.FeatureName;


/**
 * Indicates that a Gadget requires the Views feature.
 */
@FeatureName("views")
public interface NeedsViews {
  /**
   * Entry point that gets called back to handle wave feature initialization.
   *
   * @param feature
   *          an instance of the feature to use to invoke feature specific
   *          methods.
   */
  void initializeFeature(ViewsFeature feature);
}
