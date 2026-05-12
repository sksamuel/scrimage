package com.sksamuel.scrimage.canvas.painters;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Random;

public class RandomPainter implements Painter {

   private final Long seed;

   public RandomPainter() {
      this(null);
   }

   /**
    * Construct a RandomPainter with a fixed seed. With a seed, every
    * invocation of {@link #paint()} produces a deterministic pattern.
    * Without a seed (the default), each invocation freshly seeds
    * the RNG.
    */
   public RandomPainter(Long seed) {
      this.seed = seed;
   }

   @Override
   public Paint paint() {
      // Allocate the RNG once per `paint()` (i.e. per draw) rather than
      // once per device tile. Previously a fresh `new Random()` was
      // built inside getRaster, called many times per fill — output was
      // non-reproducible across runs and adjacent tiles could spawn in
      // the same millisecond and produce correlated seams.
      final Random rng = seed != null ? new Random(seed) : new Random();
      return new Paint() {

         @Override
         public int getTransparency() {
            return Transparency.OPAQUE;
         }

         @Override
         public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
            return new PaintContext() {

               @Override
               public void dispose() {
               }

               @Override
               public ColorModel getColorModel() {
                  return ColorModel.getRGBdefault();
               }

               @Override
               public Raster getRaster(int x, int y, int w, int h) {
                  // Bugs the previous implementation had:
                  //  - the loops incremented x / y (the method parameters),
                  //    not x2 / y2, so only one pixel was ever written and
                  //    the conditions terminated arbitrarily;
                  //  - it called raster.setPixel(x2, y2, ...) using the
                  //    GLOBAL coords rather than raster-local (0..w, 0..h),
                  //    which would have thrown AIOOB had the loops advanced;
                  //  - it built the channel array as [a, r, g, b] but the
                  //    default RGB ColorModel expects [r, g, b, a];
                  //  - it set alpha=0 on every pixel even though the Paint
                  //    advertises Transparency.OPAQUE.
                  WritableRaster raster = getColorModel().createCompatibleWritableRaster(w, h);
                  int[] pixel = new int[4];
                  pixel[3] = 255; // opaque alpha — matches getTransparency()
                  for (int dx = 0; dx < w; dx++) {
                     for (int dy = 0; dy < h; dy++) {
                        pixel[0] = rng.nextInt(256);
                        pixel[1] = rng.nextInt(256);
                        pixel[2] = rng.nextInt(256);
                        raster.setPixel(dx, dy, pixel);
                     }
                  }
                  return raster;
               }
            };
         }
      };
   }
}
