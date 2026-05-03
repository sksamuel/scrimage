package com.sksamuel.scrimage.filter;

import com.sksamuel.scrimage.ImmutableImage;
import thirdparty.jhlabs.image.PixelUtils;

/**
 * Works in a similar way to ThresholdFilter, but only blacks those pixels that are below given threshold.
 */
public class BlackThresholdFilter implements Filter {

   private final double thresholdPercentage;

   public BlackThresholdFilter(double thresholdPercentage) {
      // Asserts are disabled by default in production JVMs, so the previous
      // `assert` here was a no-op and out-of-range values produced silently
      // wrong output: pct > 100 made every pixel fall below the threshold
      // (so the whole image was blackened), pct < 0 made no pixel match
      // (so the filter was a no-op), and NaN propagated as 0 with the same
      // no-op result.
      if (!(thresholdPercentage >= 0.0 && thresholdPercentage <= 100.0)) {
         throw new IllegalArgumentException(
            "thresholdPercentage must be in [0, 100]; got " + thresholdPercentage);
      }
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
