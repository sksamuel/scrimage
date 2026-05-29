package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "hefe" filter:
 * sepia(.4) contrast(1.5) brightness(1.2) saturate(1.4) hue-rotate(-10deg) + radial transparent->rgba(0,0,0,.25) multiply.
 */
public class HefeFilter extends InstagramFilter {
   public HefeFilter() {
      super(Arrays.asList(sepia(0.4f), contrast(1.5f), brightness(1.2f), saturate(1.4f), hueRotate(-10f)), Overlay.radial(BlendMode.MULTIPLY, 0f, 0, 0, 0, 0f, 1f, 0, 0, 0, 0.25f));
   }
}
