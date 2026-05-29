package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "ashby" filter:
 * sepia(.5) contrast(1.2) saturate(1.8) + rgba(125,105,24,.35) lighten.
 */
public class AshbyFilter extends InstagramFilter {
   public AshbyFilter() {
      super(Arrays.asList(sepia(0.5f), contrast(1.2f), saturate(1.8f)), Overlay.solid(125, 105, 24, 0.35f, BlendMode.LIGHTEN));
   }
}
