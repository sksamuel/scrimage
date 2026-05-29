package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "brooklyn" filter:
 * sepia(.25) contrast(1.25) brightness(1.25) hue-rotate(5deg) + rgba(127,187,227,.2) overlay.
 */
public class BrooklynFilter extends InstagramFilter {
   public BrooklynFilter() {
      super(Arrays.asList(sepia(0.25f), contrast(1.25f), brightness(1.25f), hueRotate(5f)), Overlay.solid(127, 187, 227, 0.2f, BlendMode.OVERLAY));
   }
}
