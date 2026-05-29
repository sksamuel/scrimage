package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "charmes" filter:
 * sepia(.25) contrast(1.25) brightness(1.25) saturate(1.35) hue-rotate(-5deg) + rgba(125,105,24,.25) darken.
 */
public class CharmesFilter extends InstagramFilter {
   public CharmesFilter() {
      super(Arrays.asList(sepia(0.25f), contrast(1.25f), brightness(1.25f), saturate(1.35f), hueRotate(-5f)), Overlay.solid(125, 105, 24, 0.25f, BlendMode.DARKEN));
   }
}
