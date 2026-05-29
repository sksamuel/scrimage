package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "vesper" filter:
 * sepia(.35) contrast(1.15) brightness(1.2) saturate(1.3) + rgba(125,105,24,.25) overlay.
 */
public class VesperFilter extends InstagramFilter {
   public VesperFilter() {
      super(Arrays.asList(sepia(0.35f), contrast(1.15f), brightness(1.2f), saturate(1.3f)), Overlay.solid(125, 105, 24, 0.25f, BlendMode.OVERLAY));
   }
}
