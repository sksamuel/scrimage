package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.hueRotate;
import static com.sksamuel.scrimage.filter.instagram.CssFilters.saturate;
import static com.sksamuel.scrimage.filter.instagram.CssFilters.sepia;

/**
 * Clean-room implementation of the Instagram "1977" filter:
 * {@code sepia(.5) hue-rotate(-30deg) saturate(1.4)} with no overlay.
 */
public class Filter1977 extends InstagramFilter {
   public Filter1977() {
      super(Arrays.asList(sepia(0.5f), hueRotate(-30f), saturate(1.4f)));
   }
}
