package com.sksamuel.scrimage.filter;

import com.sksamuel.scrimage.ImmutableImage;

/**
 * Returns a new ImmutableImage with the given alpha mask applied to this image.
 * The channel is an int which indicates which argb channel to use from the mask image.
 * For example to use the red channel set channel to 1 (0123 = argb)
 */
public class AlphaMaskFilter implements Filter {

   private final ImmutableImage mask;
   private final int channel;

   public AlphaMaskFilter(ImmutableImage mask) {
      this(mask, 0);
   }

   public AlphaMaskFilter(ImmutableImage mask, int channel) {
      // Asserts are disabled by default in production JVMs, so a channel
      // outside [0, 3] used to silently fall through to the apply()
      // switch's default branch (alpha channel). Validate explicitly with
      // a useful diagnostic.
      if (channel < 0 || channel > 3) {
         throw new IllegalArgumentException(
            "channel must be in [0, 3] (0=alpha, 1=red, 2=green, 3=blue); got " + channel);
      }
      this.mask = mask;
      this.channel = channel;
   }

   @Override
   public void apply(ImmutableImage image) {

      int w = image.width;
      int h = image.height;
      // The previous code passed (w, h) as the region for mask.awt().getRGB
      // even when the mask had different dimensions. If the mask was
      // smaller this throws ArrayIndexOutOfBoundsException deep inside
      // BufferedImage.getRGB; if it was larger it silently sampled only
      // the top-left corner. Require explicit dimension match so callers
      // know up front.
      if (mask.width != w || mask.height != h) {
         throw new IllegalArgumentException(
            "AlphaMaskFilter mask must match the image dimensions: mask is "
               + mask.width + "x" + mask.height + ", image is " + w + "x" + h);
      }
      int[] imagePixels = image.awt().getRGB(0, 0, w, h, null, 0, w);
      int[] maskPixels = mask.awt().getRGB(0, 0, w, h, null, 0, w);

      // The chosen source channel is fixed for the whole call, so resolve the
      // mask bits and left shift once instead of branching per pixel. The
      // default (alpha) case is a shift of 0, identical to masking the alpha
      // channel in place.
      int maskBits;
      int shift;
      switch (channel) {
         case 1:
            maskBits = 0x00FF0000; shift = 8;  break; // Shift red to alpha
         case 2:
            maskBits = 0x0000FF00; shift = 16; break; // Shift green to alpha
         case 3:
            maskBits = 0x000000FF; shift = 24; break; // Shift blue to alpha
         default:
            maskBits = 0xFF000000; shift = 0;  break; // use alpha channel
      }

      for (int i = 0; i < imagePixels.length; i++) {
         int color = imagePixels[i] & 0x00ffffff; // Mask preexisting alpha
         int alpha = (maskPixels[i] & maskBits) << shift;
         imagePixels[i] = color | alpha;
      }
      image.awt().setRGB(0, 0, w, h, imagePixels, 0, w);
   }
}
