package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "walden" filter:
 * sepia(.35) contrast(.8) brightness(1.25) saturate(1.4) + rgba(229,240,128,.5) darken.
 */
public class WaldenFilter extends InstagramFilter {
   public WaldenFilter() {
      super(Arrays.asList(sepia(0.35f), contrast(0.8f), brightness(1.25f), saturate(1.4f)), Overlay.solid(229, 240, 128, 0.5f, BlendMode.DARKEN));
   }
}
