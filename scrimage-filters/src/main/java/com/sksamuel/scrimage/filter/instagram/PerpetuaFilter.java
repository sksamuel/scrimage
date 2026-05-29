package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "perpetua" filter:
 * contrast(1.1) brightness(1.25) saturate(1.1) + linear rgba(0,91,154,.25)->rgba(230,193,61,.25) multiply.
 */
public class PerpetuaFilter extends InstagramFilter {
   public PerpetuaFilter() {
      super(Arrays.asList(contrast(1.1f), brightness(1.25f), saturate(1.1f)), Overlay.linear(BlendMode.MULTIPLY, 0f, 0, 91, 154, 0.25f, 1f, 230, 193, 61, 0.25f));
   }
}
