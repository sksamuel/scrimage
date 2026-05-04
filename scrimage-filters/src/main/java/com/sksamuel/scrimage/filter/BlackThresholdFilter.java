package com.sksamuel.scrimage.filter;

import com.sksamuel.scrimage.ImmutableImage;
import thirdparty.jhlabs.image.PixelUtils;

/**
 * Works in a similar way to ThresholdFilter, but only blacks those pixels that are below given threshold.
 */
public class BlackThresholdFilter implements Filter {

   private final double thresholdPercentage;

   public BlackThresholdFilter(double thresholdPercentage) {
      assert (thresholdPercentage >= 0.0 && thresholdPercentage <= 100.0);
      this.thresholdPercentage = thresholdPercentage;
   }

   @Override
   public void apply(ImmutableImage image) {
      int threshold = (int) ((255 * thresholdPercentage) / 100.0);
      int w = image.width;
      int h = image.height;
      int[] argb = image.awt().getRGB(0, 0, w, h, null, 0, w);
      for (int i = 0; i < argb.length; i++) {
         int p = argb[i];
         if (PixelUtils.brightness(p) < threshold) {
            argb[i] = p & 0xff000000;
         }
      }
      image.awt().setRGB(0, 0, w, h, argb, 0, w);
   }
}
