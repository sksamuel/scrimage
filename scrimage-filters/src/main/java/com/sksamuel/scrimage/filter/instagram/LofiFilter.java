package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "lofi" filter:
 * saturate(1.1) contrast(1.5) (no overlay).
 */
public class LofiFilter extends InstagramFilter {
   public LofiFilter() {
      super(Arrays.asList(saturate(1.1f), contrast(1.5f)));
   }
}
