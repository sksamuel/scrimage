package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "ludwig" filter:
 * sepia(.25) contrast(1.05) brightness(1.05) saturate(2) + rgba(125,105,24,.1) overlay.
 */
public class LudwigFilter extends InstagramFilter {
   public LudwigFilter() {
      super(Arrays.asList(sepia(0.25f), contrast(1.05f), brightness(1.05f), saturate(2f)), Overlay.solid(125, 105, 24, 0.1f, BlendMode.OVERLAY));
   }
}
