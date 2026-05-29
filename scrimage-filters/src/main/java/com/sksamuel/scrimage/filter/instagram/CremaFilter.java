package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "crema" filter:
 * sepia(.5) contrast(1.25) brightness(1.15) saturate(.9) hue-rotate(-2deg) + rgba(125,105,24,.2) multiply.
 */
public class CremaFilter extends InstagramFilter {
   public CremaFilter() {
      super(Arrays.asList(sepia(0.5f), contrast(1.25f), brightness(1.15f), saturate(0.9f), hueRotate(-2f)), Overlay.solid(125, 105, 24, 0.2f, BlendMode.MULTIPLY));
   }
}
