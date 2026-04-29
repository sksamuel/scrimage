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
      assert (channel >= 0 && channel <= 3);
      this.mask = mask;
      this.channel = channel;
   }

   @Override
   public void apply(ImmutableImage image) {

      int w = image.width;
      int h = image.height;
      int[] imagePixels = image.awt().getRGB(0, 0, w, h, null, 0, w);
      int[] maskPixels = mask.awt().getRGB(0, 0, w, h, null, 0, w);

      for (int i = 0; i < imagePixels.length; i++) {
         int color = imagePixels[i] & 0x00ffffff; // Mask preexisting alpha
         int alpha;
         switch (channel) {
            case 1:
               alpha = (maskPixels[i] & 0x00FF0000) << 8; // Shift red to alpha
               break;
            case 2:
               alpha = (maskPixels[i] & 0x0000FF00) << 16; // Shift green to alpha
               break;
            case 3:
               alpha = (maskPixels[i] & 0x000000FF) << 24; // Shift blue to alpha
               break;
            default:
               alpha = (maskPixels[i] & 0xFF000000); // use alpha channel
               break;
         }
         imagePixels[i] = color | alpha;
      }
      image.awt().setRGB(0, 0, w, h, imagePixels, 0, w);
   }
}
