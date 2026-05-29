package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "amaro" filter:
 * sepia(.35) contrast(1.1) brightness(1.2) saturate(1.3) + rgba(125,105,24,.2) overlay.
 */
public class AmaroFilter extends InstagramFilter {
   public AmaroFilter() {
      super(Arrays.asList(sepia(0.35f), contrast(1.1f), brightness(1.2f), saturate(1.3f)), Overlay.solid(125, 105, 24, 0.2f, BlendMode.OVERLAY));
   }
}
