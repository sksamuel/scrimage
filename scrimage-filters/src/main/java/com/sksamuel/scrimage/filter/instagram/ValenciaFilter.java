package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "valencia" filter:
 * sepia(.25) contrast(1.1) brightness(1.1) + rgba(230,193,61,.1) lighten.
 */
public class ValenciaFilter extends InstagramFilter {
   public ValenciaFilter() {
      super(Arrays.asList(sepia(0.25f), contrast(1.1f), brightness(1.1f)), Overlay.solid(230, 193, 61, 0.1f, BlendMode.LIGHTEN));
   }
}
