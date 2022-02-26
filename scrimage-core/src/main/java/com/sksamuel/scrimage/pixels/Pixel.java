package com.sksamuel.scrimage.pixels;

import com.sksamuel.scrimage.color.RGBColor;

import java.util.Objects;
import java.util.function.Function;

/**
 * A pixel is an encoding of a color value at a specific location in a Raster.
 * The pixel is encoded using an ARGB packed int.
 */
public class Pixel {

   public final int argb;
   public final int x;
   public final int y;

   public Pixel(int x, int y, int argb) {
      this.x = x;
      this.y = y;
      this.argb = argb;
   }

   public Pixel(int x, int y, int r, int g, int b, int alpha) {
      this.x = x;
      this.y = y;
      this.argb = PixelTools.argb(alpha, r, g, b);
   }

   public int alpha() {
      return PixelTools.alpha(argb);
   }

   public int red() {
      return PixelTools.red(argb);
   }

   public int green() {
      return PixelTools.green(argb);
   }

   public int blue() {
      return PixelTools.blue(argb);
   }

   public int average() {
      return (red() + green() + blue()) / 3;
   }

   public int x() {
      return x;
   }

   public int y() {
      return y;
   }

   // use toARGBInt() or .argb
   @Deprecated
   public int toInt() {
      return toARGBInt();
   }

   /**
    * Returns this pixel as a packed ARGB int.
    *
    * @return
    */
   public int toARGBInt() {
      return argb;
   }

   /**
    * Returns a copy of this pixel, with the same red, green, blue values, but with alpha
    * set to the given value.
    */
   public Pixel alpha(int a) {
      return new Pixel(x, y, red(), green(), blue(), a);
   }

   /**
    * Returns a copy of this pixel, with the same red, green, blue values, but with alpha
    * set to fully opaque (255).
    */
   public Pixel noalpha() {
      return alpha(255);
   }

   /**
    * Returns a copy of this pixel, with the same red, green, blue values, but with alpha
    * set to fully transparent (0).
    */
   public Pixel fullalpha() {
      return alpha(0);
   }

   /**
    * Returns a copy of this pixel with the red component set to the given value.
    */
   public Pixel red(int r) {
      return new Pixel(x, y, r, green(), blue(), alpha());
   }

   /**
    * Returns a copy of this pixel with the green component set to the given value.
    */
   public Pixel green(int g) {
      return new Pixel(x, y, red(), g, blue(), alpha());
   }

   /**
    * Returns a copy of this pixel with the blue component set to the given value.
    */
   public Pixel blue(int b) {
      return new Pixel(x, y, red(), green(), b, alpha());
   }

   /**
    * Returns this pixel as an array of ARGB values.
    */
   public int[] toARGB() {
      return new int[]{alpha(), red(), green(), blue()};
   }

   /**
    * Returns this pixel as an array of RGB values, ignoring any alpha value.
    */
   public int[] toRGB() {
      return new int[]{red(), green(), blue()};
   }

   public RGBColor toColor() {
      return new RGBColor(red(), green(), blue(), alpha());
   }

   public Pixel mapByComponent(Function<Integer, Integer> f) {
      return new Pixel(x, y, f.apply(red()), f.apply(blue()), f.apply(green()), f.apply(alpha()));
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Pixel pixel = (Pixel) o;
      return argb == pixel.argb &&
         x == pixel.x &&
         y == pixel.y;
   }

   @Override
   public int hashCode() {
      return Objects.hash(argb, x, y);
   }

   @Override
   public String toString() {
      final StringBuffer sb = new StringBuffer("Pixel{");
      sb.append("argb=").append(argb);
      sb.append(", x=").append(x);
      sb.append(", y=").append(y);
      sb.append('}');
      return sb.toString();
   }
}
