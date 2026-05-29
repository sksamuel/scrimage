package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "willow" filter:
 * brightness(1.2) contrast(.85) saturate(.05) sepia(.2) (no overlay).
 */
public class WillowFilter extends InstagramFilter {
   public WillowFilter() {
      super(Arrays.asList(brightness(1.2f), contrast(0.85f), saturate(0.05f), sepia(0.2f)));
   }
}
