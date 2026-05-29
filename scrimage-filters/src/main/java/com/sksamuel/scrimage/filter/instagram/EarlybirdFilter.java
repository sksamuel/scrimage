package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "earlybird" filter:
 * sepia(.25) contrast(1.25) brightness(1.15) saturate(.9) hue-rotate(-5deg) + radial transparent->rgba(125,105,24,.2) multiply.
 */
public class EarlybirdFilter extends InstagramFilter {
   public EarlybirdFilter() {
      super(Arrays.asList(sepia(0.25f), contrast(1.25f), brightness(1.15f), saturate(0.9f), hueRotate(-5f)), Overlay.radial(BlendMode.MULTIPLY, 0f, 125, 105, 24, 0f, 1f, 125, 105, 24, 0.2f));
   }
}
