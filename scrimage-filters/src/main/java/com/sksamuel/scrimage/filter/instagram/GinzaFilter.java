package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "ginza" filter:
 * sepia(.25) contrast(1.15) brightness(1.2) saturate(1.35) hue-rotate(-5deg) + rgba(125,105,24,.15) darken.
 */
public class GinzaFilter extends InstagramFilter {
   public GinzaFilter() {
      super(Arrays.asList(sepia(0.25f), contrast(1.15f), brightness(1.2f), saturate(1.35f), hueRotate(-5f)), Overlay.solid(125, 105, 24, 0.15f, BlendMode.DARKEN));
   }
}
