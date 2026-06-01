package com.sksamuel.scrimage.filter;

import com.sksamuel.scrimage.ImmutableImage;

public class SaltAndPepperFilter implements Filter {

   private final double salt;
   private final double pepper;

   public SaltAndPepperFilter(double salt, double pepper) {
      this.salt = salt;
      this.pepper = pepper;
   }

   // Opaque black and opaque white as packed ARGB ints. The previous
   // values (0 and 0x00FFFFFF) had alpha=0, so on TYPE_INT_ARGB images
   // the salt and pepper came out fully transparent rather than the
   // intended opaque white / opaque black.
   private static final int OPAQUE_BLACK = 0xFF000000;
   private static final int OPAQUE_WHITE = 0xFFFFFFFF;

   @Override
   public void apply(ImmutableImage img) {
      // Single draw per pixel partitioned across the two outcomes.
      // Using `Math.random() < pepper` followed by `else if
      // (Math.random() < salt)` would give each pixel a probability
      // of being salt of (1 - pepper) * salt rather than the
      // documented `salt` — e.g. SaltAndPepperFilter(0.5, 0.5)
      // produced 50% pepper but only 25% salt.
      int w = img.width;
      int h = img.height;
      int[] argb = img.awt().getRGB(0, 0, w, h, null, 0, w);
      for (int i = 0; i < argb.length; i++) {
         double r = Math.random();
         if (r < pepper) {
            argb[i] = OPAQUE_BLACK;
         } else if (r < pepper + salt) {
            argb[i] = OPAQUE_WHITE;
         }
      }
      img.awt().setRGB(0, 0, w, h, argb, 0, w);
   }
}
