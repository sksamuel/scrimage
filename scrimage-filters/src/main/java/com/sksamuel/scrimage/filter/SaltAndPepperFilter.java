package com.sksamuel.scrimage.filter;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.color.RGBColor;

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
      for (int i = 0; i < img.width; i++) {
         for (int j = 0; j < img.height; j++) {
            if (Math.random() < pepper) {
               img.setColor(i, j, RGBColor.fromARGBInt(OPAQUE_BLACK));
            } else if (Math.random() < salt) {
               img.setColor(i, j, RGBColor.fromARGBInt(OPAQUE_WHITE));
            }
         }
      }
   }
}
