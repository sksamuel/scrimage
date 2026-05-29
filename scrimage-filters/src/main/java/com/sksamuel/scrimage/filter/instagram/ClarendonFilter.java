package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "clarendon" filter:
 * sepia(.15) contrast(1.25) brightness(1.25) hue-rotate(5deg) + rgba(127,187,227,.4) overlay.
 */
public class ClarendonFilter extends InstagramFilter {
   public ClarendonFilter() {
      super(Arrays.asList(sepia(0.15f), contrast(1.25f), brightness(1.25f), hueRotate(5f)), Overlay.solid(127, 187, 227, 0.4f, BlendMode.OVERLAY));
   }
}
