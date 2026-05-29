package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "brannan" filter:
 * sepia(.4) contrast(1.25) brightness(1.1) saturate(.9) hue-rotate(-2deg) (no overlay).
 */
public class BrannanFilter extends InstagramFilter {
   public BrannanFilter() {
      super(Arrays.asList(sepia(0.4f), contrast(1.25f), brightness(1.1f), saturate(0.9f), hueRotate(-2f)));
   }
}
