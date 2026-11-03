package com.sksamuel.scrimage.scaling;

import java.awt.image.BufferedImage;

public class AwtNearestNeighbourScale implements Scale {

   @Override
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
      int[] dst = new int[w * h];
      double xRatio = (double) srcW / w;
      double yRatio = (double) srcH / h;
      // Only fetch the source rows that are actually sampled: a downscale reads
      // h rows rather than converting the whole source image to ARGB.
      int[] srcRow = new int[srcW];
      int lastSy = -1;
      int k = 0;
      for (int y = 0; y < h; y++) {
         int sy = (int) (y * yRatio);
         if (sy != lastSy) {
            in.getRGB(0, sy, srcW, 1, srcRow, 0, srcW);
            lastSy = sy;
         }
         for (int x = 0; x < w; x++) {
            dst[k++] = srcRow[(int) (x * xRatio)];
         }
      }
      // TYPE_CUSTOM (0) cannot be passed to the BufferedImage constructor
      // (e.g. 16-bit PNGs decode to a custom type); fall back to ARGB.
      int type = in.getType() == BufferedImage.TYPE_CUSTOM ? BufferedImage.TYPE_INT_ARGB : in.getType();
      BufferedImage target = new BufferedImage(w, h, type);
      target.setRGB(0, 0, w, h, dst, 0, w);
      return target;
   }
}
