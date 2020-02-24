package com.sksamuel.scrimage.canvas.painters;

import com.sksamuel.scrimage.color.RGBColor;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Random;

public class RandomPainter implements Painter {
   @Override
   public Paint paint() {
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
                  WritableRaster raster = getColorModel().createCompatibleWritableRaster(w, h);
                  Random r = new Random();
                  for (int x2 = x; x2 < x + w; x++) {
                     for (int y2 = y; y2 < y + h; y++) {
                        RGBColor color = new RGBColor(r.nextInt(256), r.nextInt(256), r.nextInt(256), 0);
                        raster.setPixel(x2, y2, color.toArray());
                     }
                  }
                  return raster;
               }
            };
         }
      };
   }
}
