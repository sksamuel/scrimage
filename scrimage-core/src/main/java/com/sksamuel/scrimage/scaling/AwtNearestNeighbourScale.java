package com.sksamuel.scrimage.scaling;

import java.awt.image.BufferedImage;

public class AwtNearestNeighbourScale implements Scale {

   public BufferedImage scale(BufferedImage in, int w, int h) {
      // Bulk getRGB → manual nearest-neighbour map → bulk setRGB. The
      // previous Graphics2D.drawImage path with NEAREST_NEIGHBOR composited
      // via SrcOver, which premultiplied alpha and rounded away ±1 from
      // each colour channel for non-255 alpha — same root cause as
      // #414/#416. Doing the map ourselves is bit-exact for the standard
      // image types (TYPE_INT_ARGB, TYPE_INT_RGB, TYPE_4BYTE_ABGR, …)
      // because getRGB / setRGB round-trip cleanly through the colour model.
      int srcW = in.getWidth();
      int srcH = in.getHeight();
      int[] src = in.getRGB(0, 0, srcW, srcH, null, 0, srcW);
      int[] dst = new int[w * h];
      double xRatio = (double) srcW / w;
      double yRatio = (double) srcH / h;
      int k = 0;
      for (int y = 0; y < h; y++) {
         int srcRow = ((int) (y * yRatio)) * srcW;
         for (int x = 0; x < w; x++) {
            dst[k++] = src[srcRow + (int) (x * xRatio)];
         }
      }
      BufferedImage target = new BufferedImage(w, h, in.getType());
      target.setRGB(0, 0, w, h, dst, 0, w);
      return target;
   }
}
