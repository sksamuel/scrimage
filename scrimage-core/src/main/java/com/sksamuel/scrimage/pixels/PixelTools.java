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
import java.util.Arrays;

public class PixelTools {

   /**
    * Returns an encoded rgb int, with alpha 255, from the given components.
    */
   public static int rgb(int r, int g, int b) {
      return argb(255, r, g, b);
   }

   /**
    * Returns an encoded argb int from the given components.
    */
   public static int argb(int a, int r, int g, int b) {
      return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
   }

   /**
    * Returns the alpha component of a pixel as a value between 0 and 255.
    */
   public static int alpha(int pixel) {
      return pixel >> 24 & 0xFF;
   }

   /**
    * Returns the red component of a pixel as a value between 0 and 255.
    */
   public static int red(int pixel) {
      return pixel >> 16 & 0xFF;
   }

   /**
    * Returns the green component of a pixel as a value between 0 and 255.
    */
   public static int green(int pixel) {
      return pixel >> 8 & 0xFF;
   }

   /**
    * Returns the blue component of a pixel as a value between 0 and 255.
    */
   public static int blue(int pixel) {
      return pixel & 0xFF;
   }

   /**
    * Returns the gray value of a pixel as a value between 0 and 255.
    */
   public static int gray(int pixel) {
      return (red(pixel) + green(pixel) + blue(pixel)) / 3;
   }

   public static int truncate(Double value) {
      if (value < 0) return 0;
      else if (value > 255) return 255;
      else return value.intValue();
   }

   /**
    * Returns true if all pixels in the array have the same color
    */
   public static boolean uniform(Color color, Pixel[] pixels) {
      return Arrays.stream(pixels).allMatch(p -> p.toARGBInt() == RGBColor.fromAwt(color).toARGBInt());
   }

   /**
    * Scales the brightness of a pixel.
    */
   public static int scale(Double factor, int pixel) {
      return rgb(
         (int) Math.round(factor * red(pixel)),
         (int) Math.round(factor * green(pixel)),
         (int) Math.round(factor * blue(pixel))
      );
   }

   /**
    * Returns true if the colors of all pixels in the array are within the given tolerance
    * compared to the referenced color
    */
   public static boolean approx(Color color, int tolerance, Pixel[] pixels) {

      RGBColor refColor = RGBColor.fromAwt(color);

      RGBColor minColor = new RGBColor(
         Math.max(refColor.red - tolerance, 0),
         Math.max(refColor.green - tolerance, 0),
         Math.max(refColor.blue - tolerance, 0),
         refColor.alpha
      );

      RGBColor maxColor = new RGBColor(
         Math.min(refColor.red + tolerance, 255),
         Math.min(refColor.green + tolerance, 255),
         Math.min(refColor.blue + tolerance, 255),
         refColor.alpha
      );

      return Arrays.stream(pixels).allMatch(p -> p.toARGBInt() >= minColor.toARGBInt() && p.toARGBInt() <= maxColor.toARGBInt());
   }

   /**
    * Returns true if the colors of all pixels in the array are within the given tolerance
    * compared to the referenced color
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
    */
   public static Point offsetToPoint(int offset, int width) {
      return new Point(offset % width, offset / width);
   }

   /**
    * Returns a new Pixel with the transparency in the given pixel replaced by the given color.
    */
   public static Pixel replaceTransparencyWithColor(Pixel p, Color color) {
      int r = (p.red() * p.alpha() + color.getRed() * color.getAlpha() * (255 - p.alpha()) / 255) / 255;
      int g = (p.green() * p.alpha() + color.getGreen() * color.getAlpha() * (255 - p.alpha()) / 255) / 255;
      int b = (p.blue() * p.alpha() + color.getBlue() * color.getAlpha() * (255 - p.alpha()) / 255) / 255;
      return new Pixel(p.x, p.y, r, g, b, 255);
   }
}
