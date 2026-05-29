package com.sksamuel.scrimage.filter.instagram;

import java.util.Arrays;

import static com.sksamuel.scrimage.filter.instagram.CssFilters.*;

/**
 * Clean-room implementation of the Instagram "xpro_ii" filter:
 * sepia(.45) contrast(1.25) brightness(1.75) saturate(1.3) hue-rotate(-5deg) + radial rgba(0,91,154,.35)->rgba(0,0,0,.65) multiply.
 */
public class Xpro2Filter extends InstagramFilter {
   public Xpro2Filter() {
      super(Arrays.asList(sepia(0.45f), contrast(1.25f), brightness(1.75f), saturate(1.3f), hueRotate(-5f)), Overlay.radial(BlendMode.MULTIPLY, 0f, 0, 91, 154, 0.35f, 1f, 0, 0, 0, 0.65f));
   }
}
