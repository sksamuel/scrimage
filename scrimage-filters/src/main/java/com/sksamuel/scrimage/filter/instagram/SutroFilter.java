package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "sutro" filter:
 * sepia(.4) contrast(1.2) brightness(.9) saturate(1.4) hue-rotate(-10deg) + radial transparent 50%->rgba(0,0,0,.5) 90% darken.
 */
public class SutroFilter extends InstagramFilter {
   public SutroFilter() {
      super(Arrays.asList(sepia(0.4f), contrast(1.2f), brightness(0.9f), saturate(1.4f), hueRotate(-10f)), Overlay.radial(BlendMode.DARKEN, 0.5f, 0, 0, 0, 0f, 0.9f, 0, 0, 0, 0.5f));
   }
}
