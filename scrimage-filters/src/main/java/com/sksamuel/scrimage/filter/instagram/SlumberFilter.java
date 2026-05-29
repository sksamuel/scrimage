package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "slumber" filter:
 * sepia(.35) contrast(1.25) saturate(1.25) + rgba(125,105,24,.2) darken.
 */
public class SlumberFilter extends InstagramFilter {
   public SlumberFilter() {
      super(Arrays.asList(sepia(0.35f), contrast(1.25f), saturate(1.25f)), Overlay.solid(125, 105, 24, 0.2f, BlendMode.DARKEN));
   }
}
