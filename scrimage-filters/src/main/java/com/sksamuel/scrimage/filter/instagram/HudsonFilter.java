package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "hudson" filter:
 * sepia(.25) contrast(1.2) brightness(1.2) saturate(1.05) hue-rotate(-15deg) + radial transparent 25%->rgba(25,62,167,.25) multiply.
 */
public class HudsonFilter extends InstagramFilter {
   public HudsonFilter() {
      super(Arrays.asList(sepia(0.25f), contrast(1.2f), brightness(1.2f), saturate(1.05f), hueRotate(-15f)), Overlay.radial(BlendMode.MULTIPLY, 0.25f, 25, 62, 167, 0f, 1f, 25, 62, 167, 0.25f));
   }
}
