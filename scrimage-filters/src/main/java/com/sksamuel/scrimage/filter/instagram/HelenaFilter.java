package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "helena" filter:
 * sepia(.5) contrast(1.05) brightness(1.05) saturate(1.35) + rgba(158,175,30,.25) overlay.
 */
public class HelenaFilter extends InstagramFilter {
   public HelenaFilter() {
      super(Arrays.asList(sepia(0.5f), contrast(1.05f), brightness(1.05f), saturate(1.35f)), Overlay.solid(158, 175, 30, 0.25f, BlendMode.OVERLAY));
   }
}
