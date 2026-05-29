package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "poprocket" filter:
 * sepia(.15) brightness(1.2) + radial rgba(206,39,70,.75) 40%->black 80% screen.
 */
public class PoprocketFilter extends InstagramFilter {
   public PoprocketFilter() {
      super(Arrays.asList(sepia(0.15f), brightness(1.2f)), Overlay.radial(BlendMode.SCREEN, 0.4f, 206, 39, 70, 0.75f, 0.8f, 0, 0, 0, 1.0f));
   }
}
