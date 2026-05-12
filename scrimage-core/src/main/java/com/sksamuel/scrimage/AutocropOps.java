package com.sksamuel.scrimage;

import com.sksamuel.scrimage.pixels.PixelTools;
import com.sksamuel.scrimage.pixels.PixelsExtractor;

import java.awt.*;

public class AutocropOps {

   // Each scan*() walks one axis until it hits the first non-matching
   // strip or the image edge. The earlier implementations were
   // unbounded tail recursion; the JVM does not eliminate Java tail
   // calls, so a fully-cropable wide/tall image blew the stack on
   // sufficiently large inputs (e.g. a 5000-px wide image with the
   // entire border matching cropped colour recursed 5000 times). Use
   // a plain while loop.

   /**
    * Starting with the given column index, will return the first column index
    * which contains a colour that does not match the given color.
    */
   public static int scanright(Color color, int height, int width, int col, PixelsExtractor f, int tolerance) {
      while (col < width && PixelTools.colorMatches(color, tolerance, f.apply(new Rectangle(col, 0, 1, height))))
         col++;
      return col;
   }

   public static int scanleft(Color color, int height, int col, PixelsExtractor f, int tolerance) {
      while (col > 0 && PixelTools.colorMatches(color, tolerance, f.apply(new Rectangle(col, 0, 1, height))))
         col--;
      return col;
   }

   public static int scandown(Color color, int height, int width, int row, PixelsExtractor f, int tolerance) {
      while (row < height && PixelTools.colorMatches(color, tolerance, f.apply(new Rectangle(0, row, width, 1))))
         row++;
      return row;
   }

   public static int scanup(Color color, int width, int row, PixelsExtractor f, int tolerance) {
      while (row > 0 && PixelTools.colorMatches(color, tolerance, f.apply(new Rectangle(0, row, width, 1))))
         row--;
      return row;
   }
}
