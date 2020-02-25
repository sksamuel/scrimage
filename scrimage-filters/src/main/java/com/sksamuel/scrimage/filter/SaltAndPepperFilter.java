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

   @Override
   public void apply(ImmutableImage img) {
      for (int i = 0; i < img.width; i++) {
         for (int j = 0; j < img.height; j++) {
            if (Math.random() < pepper) {
               img.setColor(i, j, RGBColor.fromARGBInt(0));
            } else if (Math.random() < salt) {
               int gray = 255;
               int rgb = ((gray * 256) + gray) * 256 + gray;
               img.setColor(i, j, RGBColor.fromARGBInt(rgb));
            }
         }
      }
   }
}
