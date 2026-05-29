package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "inkwell" filter:
 * brightness(1.25) contrast(.85) grayscale(1) (no overlay).
 */
public class InkwellFilter extends InstagramFilter {
   public InkwellFilter() {
      super(Arrays.asList(brightness(1.25f), contrast(0.85f), grayscale(1f)));
   }
}
