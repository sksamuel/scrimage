package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "lark" filter:
 * sepia(.25) contrast(1.2) brightness(1.3) saturate(1.25) (no overlay).
 */
public class LarkFilter extends InstagramFilter {
   public LarkFilter() {
      super(Arrays.asList(sepia(0.25f), contrast(1.2f), brightness(1.3f), saturate(1.25f)));
   }
}
