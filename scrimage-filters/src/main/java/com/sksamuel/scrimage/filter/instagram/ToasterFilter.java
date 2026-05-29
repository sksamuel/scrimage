package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "toaster" filter:
 * sepia(.25) contrast(1.5) brightness(.95) hue-rotate(-15deg) + radial #804e0f->rgba(0,0,0,.25) screen.
 */
public class ToasterFilter extends InstagramFilter {
   public ToasterFilter() {
      super(Arrays.asList(sepia(0.25f), contrast(1.5f), brightness(0.95f), hueRotate(-15f)), Overlay.radial(BlendMode.SCREEN, 0f, 128, 78, 15, 1.0f, 1f, 0, 0, 0, 0.25f));
   }
}
