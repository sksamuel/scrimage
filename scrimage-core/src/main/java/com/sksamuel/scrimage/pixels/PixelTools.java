/*
   Copyright 2013 Stephen K Samuel

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.sksamuel.scrimage.pixels;

import com.sksamuel.scrimage.color.RGBColor;

import java.awt.*;

public class PixelTools {

   /**
    * Returns an encoded rgb int, with alpha 255, from the given components.
    *
    * @return the encoded rgb int with alpha 255
    */
   public static int rgb(int r, int g, int b) {
      return argb(255, r, g, b);
   }

   /**
    * Returns an encoded argb int from the given components.
    *
    * @return the encoded argb int
    */
   public static int argb(int a, int r, int g, int b) {
      return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
   }

   /**
    * Returns the alpha component of a pixel as a value between 0 and 255.
    *
    * @return the alpha component of the pixel, between 0 and 255
    */
   public static int alpha(int pixel) {
      return pixel >> 24 & 0xFF;
   }

   /**
    * Returns the red component of a pixel as a value between 0 and 255.
    *
    * @return the red component of the pixel, between 0 and 255
    */
   public static int red(int pixel) {
      return pixel >> 16 & 0xFF;
   }

   /**
    * Returns the green component of a pixel as a value between 0 and 255.
    *
    * @return the green component of the pixel, between 0 and 255
    */
   public static int green(int pixel) {
      return pixel >> 8 & 0xFF;
   }

   /**
    * Returns the blue component of a pixel as a value between 0 and 255.
    *
    * @return the blue component of the pixel, between 0 and 255
    */
   public static int blue(int pixel) {
      return pixel & 0xFF;
   }

   /**
    * Returns the gray value of a pixel as a value between 0 and 255.
    *
    * @return the gray value of the pixel, between 0 and 255
    */
   public static int gray(int pixel) {
      // round, rather than truncate, for consistency with LumaGrayscale and WeightedGrayscale
      return Math.round((red(pixel) + green(pixel) + blue(pixel)) / 3f);
   }

   public static int truncate(Double value) {
      return truncate(value.doubleValue());
   }

   /**
    * Clamps the given value to [0, 255] and truncates it to an int, avoiding the
    * autoboxing the {@link #truncate(Double)} overload incurs when called in a
    * per-pixel loop.
    *
    * @return the value clamped to [0, 255] and truncated to an int
    */
   public static int truncate(double value) {
      if (value < 0) return 0;
      else if (value > 255) return 255;
      else return (int) value;
   }

   /**
    * Returns true if all pixels in the array have the same color, or both are fully transparent.
    *
    * @return true if all pixels match the given color, false otherwise
    */
   public static boolean uniform(Color color, Pixel[] pixels) {
      RGBColor target = RGBColor.fromAwt(color);
      int targetArgb = target.toARGBInt();
      boolean targetTransparent = target.alpha == 0;
      for (Pixel p : pixels) {
         if (!((p.alpha() == 0 && targetTransparent) || p.toARGBInt() == targetArgb)) return false;
      }
      return true;
   }

   /**
    * Scales the brightness of a pixel.
    *
    * @return the encoded rgb int of the brightness-scaled pixel
    */
   public static int scale(Double factor, int pixel) {
      // Clamp each channel to [0, 255]. Without clamping, a factor > 1 (or
      // < 0) yielded an out-of-range channel value that argb() then masked
      // with & 0xFF, wrapping the channel around — so a "brightening" call
      // could silently produce a darker pixel.
      return rgb(
         clampChannel((int) Math.round(factor * red(pixel))),
         clampChannel((int) Math.round(factor * green(pixel))),
         clampChannel((int) Math.round(factor * blue(pixel)))
      );
   }

   private static int clampChannel(int value) {
      if (value < 0) return 0;
      if (value > 255) return 255;
      return value;
   }

   /**
    * Returns true if the colors of all pixels in the array are within the given tolerance
    * compared to the referenced color
    *
    * @return true if all pixels are within tolerance of the given color, false otherwise
    */
   public static boolean approx(Color color, int tolerance, Pixel[] pixels) {

      RGBColor ref = RGBColor.fromAwt(color);
      int minR = Math.max(ref.red - tolerance, 0),   maxR = Math.min(ref.red + tolerance, 255);
      int minG = Math.max(ref.green - tolerance, 0), maxG = Math.min(ref.green + tolerance, 255);
      int minB = Math.max(ref.blue - tolerance, 0),  maxB = Math.min(ref.blue + tolerance, 255);

      for (Pixel p : pixels) {
         if (!(p.red()   >= minR && p.red()   <= maxR &&
               p.green() >= minG && p.green() <= maxG &&
               p.blue()  >= minB && p.blue()  <= maxB)) return false;
      }
      return true;
   }

   /**
    * Returns true if the colors of all pixels in the array are within the given tolerance
    * compared to the referenced color.
    * <p>
    * If the given colour and target colour are both fully transparent, then they will match.
    *
    * @return true if all pixels match the given color within tolerance, false otherwise
    */
   public static boolean colorMatches(Color color, int tolerance, Pixel[] pixels) {
      if (tolerance < 0 || tolerance > 255)
         throw new RuntimeException("Tolerance value must be between 0 and 255 inclusive");
      if (tolerance == 0)
         return uniform(color, pixels);
      else
         return approx(color, tolerance, pixels);
   }

   public static int pointToOffset(Point point, int w) {
      return point.y * w + point.x;
   }

   public static int coordsToOffset(int x, int y, int w) {
      return y * w + x;
   }

   /**
    * Given a width and an offset returns the coordinate for that offset.
    * In other words, starting at 0,0 and moving along each row before starting the next row,
    * it gives the coordinate that is kth from the start.
    *
    * @return the point corresponding to the given offset
    */
   public static Point offsetToPoint(int offset, int width) {
      return new Point(offset % width, offset / width);
   }

   /**
    * Returns a new Pixel with the transparency in the given pixel replaced by the given color.
    *
    * @return a new Pixel with its transparency replaced by the given color
    */
   public static Pixel replaceTransparencyWithColor(Pixel p, Color color) {
      int argb = replaceTransparencyWithColor(p.argb, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
      return new Pixel(p.x, p.y, argb);
   }

   /**
    * Returns the packed ARGB int for the given pixel with its transparency replaced by the
    * given colour components. Equivalent to {@link #replaceTransparencyWithColor(Pixel, Color)}
    * but works on packed ints, avoiding per-pixel Pixel allocation when used in a loop. The
    * colour components are passed in so callers can hoist the java.awt.Color accessors out of
    * their loop.
    *
    * @return the packed ARGB int with its transparency replaced by the given colour components
    */
   public static int replaceTransparencyWithColor(int pixel, int colorRed, int colorGreen, int colorBlue, int colorAlpha) {
      int a = alpha(pixel);
      int r = (red(pixel) * a + colorRed * colorAlpha * (255 - a) / 255) / 255;
      int g = (green(pixel) * a + colorGreen * colorAlpha * (255 - a) / 255) / 255;
      int b = (blue(pixel) * a + colorBlue * colorAlpha * (255 - a) / 255) / 255;
      return argb(255, r, g, b);
   }
}
