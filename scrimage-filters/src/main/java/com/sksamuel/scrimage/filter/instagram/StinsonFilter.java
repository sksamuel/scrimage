package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "stinson" filter:
 * sepia(.35) contrast(1.25) brightness(1.1) saturate(1.25) + rgba(125,105,24,.45) lighten.
 */
public class StinsonFilter extends InstagramFilter {
   public StinsonFilter() {
      super(Arrays.asList(sepia(0.35f), contrast(1.25f), brightness(1.1f), saturate(1.25f)), Overlay.solid(125, 105, 24, 0.45f, BlendMode.LIGHTEN));
   }
}
