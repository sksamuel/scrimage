package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "juno" filter:
 * sepia(.35) contrast(1.15) brightness(1.15) saturate(1.8) + rgba(127,187,227,.2) overlay.
 */
public class JunoFilter extends InstagramFilter {
   public JunoFilter() {
      super(Arrays.asList(sepia(0.35f), contrast(1.15f), brightness(1.15f), saturate(1.8f)), Overlay.solid(127, 187, 227, 0.2f, BlendMode.OVERLAY));
   }
}
