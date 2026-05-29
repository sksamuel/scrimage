package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "moon" filter:
 * brightness(1.4) contrast(.95) saturate(0) sepia(.35) (no overlay).
 */
public class MoonFilter extends InstagramFilter {
   public MoonFilter() {
      super(Arrays.asList(brightness(1.4f), contrast(0.95f), saturate(0f), sepia(0.35f)));
   }
}
