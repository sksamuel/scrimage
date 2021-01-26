package com.sksamuel.scrimage;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ProgressiveScale {

   public static int getType(BufferedImage image) {
      return image.getTransparency() == Transparency.OPAQUE ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
   }

   /**
    * Scales an image progressively, by starting with the original scale and halving it, until the target size is reached.
    */
   public static BufferedImage scale(BufferedImage image,
                                     int targetWidth,
                                     int targetHeight,
                                     Object interpolation) {

      int type = getType(image);
      BufferedImage temp = image;
      int w, h;
      w = image.getWidth();
      h = image.getHeight();

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
         g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, interpolation);
         g2.drawImage(temp, 0, 0, w, h, null);
         g2.dispose();

         temp = tmp;
      } while (w != targetWidth || h != targetHeight);
      return temp;
   }
}
