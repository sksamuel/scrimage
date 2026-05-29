package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "skyline" filter:
 * sepia(.15) contrast(1.25) brightness(1.25) saturate(1.2) (no overlay).
 */
public class SkylineFilter extends InstagramFilter {
   public SkylineFilter() {
      super(Arrays.asList(sepia(0.15f), contrast(1.25f), brightness(1.25f), saturate(1.2f)));
   }
}
