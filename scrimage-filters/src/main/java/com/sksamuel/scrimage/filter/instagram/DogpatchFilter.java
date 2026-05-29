package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "dogpatch" filter:
 * sepia(.35) saturate(1.1) contrast(1.5) (no overlay).
 */
public class DogpatchFilter extends InstagramFilter {
   public DogpatchFilter() {
      super(Arrays.asList(sepia(0.35f), saturate(1.1f), contrast(1.5f)));
   }
}
