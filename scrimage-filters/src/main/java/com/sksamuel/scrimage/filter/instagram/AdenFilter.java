package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.brightness;
import static com.sksamuel.scrimage.filter.instagram.CssFilters.saturate;
import static com.sksamuel.scrimage.filter.instagram.CssFilters.sepia;

/**
 * Clean-room implementation of the Instagram "Aden" filter:
 * {@code sepia(.2) brightness(1.15) saturate(1.4)} with an
 * {@code rgba(125, 105, 24, .1)} multiply overlay.
 */
public class AdenFilter extends InstagramFilter {
   public AdenFilter() {
      super(
         Arrays.asList(sepia(0.2f), brightness(1.15f), saturate(1.4f)),
         Overlay.solid(125, 105, 24, 0.1f, BlendMode.MULTIPLY)
      );
   }
}
