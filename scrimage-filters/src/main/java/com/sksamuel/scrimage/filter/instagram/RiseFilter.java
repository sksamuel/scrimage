package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "rise" filter:
 * sepia(.25) contrast(1.25) brightness(1.2) saturate(.9) + radial transparent->rgba(230,193,61,.25) lighten.
 */
public class RiseFilter extends InstagramFilter {
   public RiseFilter() {
      super(Arrays.asList(sepia(0.25f), contrast(1.25f), brightness(1.2f), saturate(0.9f)), Overlay.radial(BlendMode.LIGHTEN, 0f, 230, 193, 61, 0f, 1f, 230, 193, 61, 0.25f));
   }
}
