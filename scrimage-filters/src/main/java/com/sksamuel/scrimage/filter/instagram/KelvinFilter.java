package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "kelvin" filter:
 * sepia(.15) contrast(1.5) brightness(1.1) hue-rotate(-10deg) + radial rgba(128,78,15,.25)->.5 overlay.
 */
public class KelvinFilter extends InstagramFilter {
   public KelvinFilter() {
      super(Arrays.asList(sepia(0.15f), contrast(1.5f), brightness(1.1f), hueRotate(-10f)), Overlay.radial(BlendMode.OVERLAY, 0f, 128, 78, 15, 0.25f, 1f, 128, 78, 15, 0.5f));
   }
}
