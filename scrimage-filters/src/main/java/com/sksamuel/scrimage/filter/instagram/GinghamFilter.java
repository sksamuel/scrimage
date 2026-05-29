package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "gingham" filter:
 * contrast(1.1) brightness(1.1) + #e6e6e6 soft-light.
 */
public class GinghamFilter extends InstagramFilter {
   public GinghamFilter() {
      super(Arrays.asList(contrast(1.1f), brightness(1.1f)), Overlay.solid(230, 230, 230, 1.0f, BlendMode.SOFT_LIGHT));
   }
}
