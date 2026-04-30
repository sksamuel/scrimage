package com.sksamuel.scrimage;

import com.sksamuel.scrimage.color.RGBColor;
import com.sksamuel.scrimage.pixels.PixelTools;
import com.sksamuel.scrimage.pixels.PixelsExtractor;

import java.awt.*;

public class AutocropOps {

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

   /**
    * Variant of scanright that operates over a pre-fetched ARGB int[] of the whole image
    * (row-major, length width*height), avoiding a per-column Pixel[] allocation.
    */
   public static int scanright(Color color, int width, int height, int col, int[] argb, int tolerance) {
      while (col < width && columnMatches(argb, width, height, col, color, tolerance)) col++;
      return col;
   }

   public static int scanleft(Color color, int width, int height, int col, int[] argb, int tolerance) {
      while (col > 0 && columnMatches(argb, width, height, col, color, tolerance)) col--;
      return col;
   }

   public static int scandown(Color color, int width, int height, int row, int[] argb, int tolerance) {
      while (row < height && rowMatches(argb, width, row, color, tolerance)) row++;
      return row;
   }

   public static int scanup(Color color, int width, int height, int row, int[] argb, int tolerance) {
      while (row > 0 && rowMatches(argb, width, row, color, tolerance)) row--;
      return row;
   }

   private static boolean columnMatches(int[] argb, int width, int height, int col, Color color, int tolerance) {
      int target = RGBColor.fromAwt(color).toARGBInt();
      if (tolerance == 0) {
         int targetA = (target >>> 24) & 0xFF;
         for (int y = 0; y < height; y++) {
            int p = argb[y * width + col];
            int a = (p >>> 24) & 0xFF;
            if (!((a == 0 && targetA == 0) || p == target)) return false;
         }
         return true;
      } else {
         int refR = (target >> 16) & 0xFF, refG = (target >> 8) & 0xFF, refB = target & 0xFF;
         int minR = Math.max(refR - tolerance, 0), maxR = Math.min(refR + tolerance, 255);
         int minG = Math.max(refG - tolerance, 0), maxG = Math.min(refG + tolerance, 255);
         int minB = Math.max(refB - tolerance, 0), maxB = Math.min(refB + tolerance, 255);
         for (int y = 0; y < height; y++) {
            int p = argb[y * width + col];
            int r = (p >> 16) & 0xFF, g = (p >> 8) & 0xFF, b = p & 0xFF;
            if (r < minR || r > maxR || g < minG || g > maxG || b < minB || b > maxB) return false;
         }
         return true;
      }
   }

   private static boolean rowMatches(int[] argb, int width, int row, Color color, int tolerance) {
      int target = RGBColor.fromAwt(color).toARGBInt();
      int base = row * width;
      if (tolerance == 0) {
         int targetA = (target >>> 24) & 0xFF;
         for (int x = 0; x < width; x++) {
            int p = argb[base + x];
            int a = (p >>> 24) & 0xFF;
            if (!((a == 0 && targetA == 0) || p == target)) return false;
         }
         return true;
      } else {
         int refR = (target >> 16) & 0xFF, refG = (target >> 8) & 0xFF, refB = target & 0xFF;
         int minR = Math.max(refR - tolerance, 0), maxR = Math.min(refR + tolerance, 255);
         int minG = Math.max(refG - tolerance, 0), maxG = Math.min(refG + tolerance, 255);
         int minB = Math.max(refB - tolerance, 0), maxB = Math.min(refB + tolerance, 255);
         for (int x = 0; x < width; x++) {
            int p = argb[base + x];
            int r = (p >> 16) & 0xFF, g = (p >> 8) & 0xFF, b = p & 0xFF;
            if (r < minR || r > maxR || g < minG || g > maxG || b < minB || b > maxB) return false;
         }
         return true;
      }
   }
}
