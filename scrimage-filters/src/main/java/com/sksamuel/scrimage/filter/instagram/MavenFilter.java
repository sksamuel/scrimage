package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "maven" filter:
 * sepia(.35) contrast(1.05) brightness(1.05) saturate(1.75) + rgba(158,175,30,.25) darken.
 */
public class MavenFilter extends InstagramFilter {
   public MavenFilter() {
      super(Arrays.asList(sepia(0.35f), contrast(1.05f), brightness(1.05f), saturate(1.75f)), Overlay.solid(158, 175, 30, 0.25f, BlendMode.DARKEN));
   }
}
