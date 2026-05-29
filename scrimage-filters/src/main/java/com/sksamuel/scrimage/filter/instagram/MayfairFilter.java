package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "mayfair" filter:
 * contrast(1.1) brightness(1.15) saturate(1.1) + radial transparent->rgba(175,105,24,.4) multiply.
 */
public class MayfairFilter extends InstagramFilter {
   public MayfairFilter() {
      super(Arrays.asList(contrast(1.1f), brightness(1.15f), saturate(1.1f)), Overlay.radial(BlendMode.MULTIPLY, 0f, 175, 105, 24, 0f, 1f, 175, 105, 24, 0.4f));
   }
}
