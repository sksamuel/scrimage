package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "reyes" filter:
 * sepia(.75) contrast(.75) brightness(1.25) saturate(1.4) (no overlay).
 */
public class ReyesFilter extends InstagramFilter {
   public ReyesFilter() {
      super(Arrays.asList(sepia(0.75f), contrast(0.75f), brightness(1.25f), saturate(1.4f)));
   }
}
