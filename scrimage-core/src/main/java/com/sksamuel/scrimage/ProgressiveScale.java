package com.sksamuel.scrimage;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ProgressiveScale {

   public static int getType(BufferedImage image) {
      return image.getTransparency() == Transparency.OPAQUE ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
   }

   /**
    * Scales an image progressively, by starting with the original scale and halving it, until the target size is reached.
    *
    * @return the progressively scaled BufferedImage at the target dimensions
    */
   public static BufferedImage scale(BufferedImage image,
                                     int targetWidth,
                                     int targetHeight,
                                     Object interpolation) {

      // Progressive scaling halves each dimension until it reaches the
      // target. It is a downscale-only algorithm; if a target dimension
      // is greater than or equal to the source dimension on that axis,
      // there is nothing to do for that axis. The earlier code never
      // moved w/h toward target when the source was already at or below
      // it on an axis, so callers asking for `(targetWidth >= source.w,
      // targetHeight < source.h)` spun forever, each iteration
      // allocating a new BufferedImage.
      if (targetWidth < 1 || targetHeight < 1)
         throw new IllegalArgumentException("target dimensions must be positive, got "
            + targetWidth + "x" + targetHeight);

      // setRenderingHint(KEY_INTERPOLATION, null) throws an opaque
      // IllegalArgumentException from deep inside AWT, only after we have
      // already allocated intermediate BufferedImages. Fail fast with a
      // clear message instead.
      if (interpolation == null)
         throw new IllegalArgumentException("interpolation hint must not be null");

      int type = getType(image);
      BufferedImage temp = image;
      int w = image.getWidth();
      int h = image.getHeight();
      // Snap each axis that is already at or below the target.
      if (w < targetWidth) w = targetWidth;
      if (h < targetHeight) h = targetHeight;

      // Already at target? Return the original; no progressive step
      // needed, and skipping the unconditional iteration avoids a wasted
      // allocation and Graphics2D round-trip.
      if (w == targetWidth && h == targetHeight) {
         return image;
      }

      do {
         if (w > targetWidth) {
            w /= 2;
            if (w < targetWidth) {
               w = targetWidth;
            }
         }

         if (h > targetHeight) {
            h /= 2;
            if (h < targetHeight) {
               h = targetHeight;
            }
         }

         BufferedImage tmp = new BufferedImage(w, h, type);
         Graphics2D g2 = tmp.createGraphics();
         try {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, interpolation);
            g2.drawImage(temp, 0, 0, w, h, null);
         } finally {
            g2.dispose();
         }

         temp = tmp;
      } while (w != targetWidth || h != targetHeight);
      return temp;
   }
}
