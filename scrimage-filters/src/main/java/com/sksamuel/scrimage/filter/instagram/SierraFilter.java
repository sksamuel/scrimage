package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "sierra" filter:
 * sepia(.25) contrast(1.5) brightness(.9) hue-rotate(-15deg) + radial rgba(128,78,15,.5)->rgba(0,0,0,.65) screen.
 */
public class SierraFilter extends InstagramFilter {
   public SierraFilter() {
      super(Arrays.asList(sepia(0.25f), contrast(1.5f), brightness(0.9f), hueRotate(-15f)), Overlay.radial(BlendMode.SCREEN, 0f, 128, 78, 15, 0.5f, 1f, 0, 0, 0, 0.65f));
   }
}
