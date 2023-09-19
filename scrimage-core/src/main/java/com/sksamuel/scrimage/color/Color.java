package com.sksamuel.scrimage.color;

import com.sksamuel.scrimage.pixels.Pixel;

import java.awt.*;

public interface Color {

   /**
    * Returns a conversion of this Color into an RGBColor.
    * If this colour is already an instance of RGBColor then the same instance will be returned.
    */
   RGBColor toRGB();

   /**
    * Returns a conversion of this color into a CYMK color.
    * If this colour is already a CYMK then the same instance will be returned.
    */
   default CMYKColor toCMYK() {
      return toRGB().toCMYK();
   }

   default HSVColor toHSV() {
      return toRGB().toHSV();
   }

   default HSLColor toHSL() {
      return toRGB().toHSL();
   }

   default Grayscale toGrayscale() {
      return new LumaGrayscale().toGrayscale(this);
   }

   default Grayscale toGrayscale(GrayscaleMethod method) {
      return method.toGrayscale(this);
   }

   /**
    * Returns a HEX String of this colour. Eg for 0,255,0, this method will return 00FF00.
    */
   default String toHex() {
      StringBuilder hex = new StringBuilder(Integer.toHexString(toRGB().toARGBInt() & 0xffffff).toUpperCase());
      while (hex.length() < 6) {
         hex.insert(0, "0");
      }
      return hex.toString();
   }

   default java.awt.Color awt() {
      return toAWT();
   }

   /**
    * Returns this instance as a java.awt.Color.
    * AWT Colors use the RGB color model.
    */
   default java.awt.Color toAWT() {
      RGBColor rgb = toRGB();
      return new java.awt.Color(rgb.red, rgb.green, rgb.blue, rgb.alpha);
   }

   default Pixel toPixel(int x, int y) {
      RGBColor rgb = toRGB();
      return new Pixel(x, y, rgb.red, rgb.green, rgb.blue, rgb.alpha);
   }

   /**
    * Returns the average of this Color merged with another Color.
    * Takes into account alpha and returns the average as an RGB value.
    * <p>
    * See https://stackoverflow.com/questions/1944095/how-to-mix-two-argb-pixels
    */
   default RGBColor average(Color other) {
      RGBColor c1 = this.toRGB();
      RGBColor c2 = other.toRGB();
      int r = (c1.red * c1.alpha / 255) + (c2.red * c2.alpha * (255 - c1.alpha) / (255 * 255));
      int g = (c1.green * c1.alpha / 255) + (c2.green * c2.alpha * (255 - c1.alpha) / (255 * 255));
      int b = (c1.blue * c1.alpha / 255) + (c2.blue * c2.alpha * (255 - c1.alpha) / (255 * 255));
      int a = c1.alpha + (c2.alpha * (255 - c1.alpha) / 255);
      return new RGBColor(r, g, b, a);
   }

   /**
    * Returns this colour as an AWT Paint.
    */
   default Paint paint() {
      return new java.awt.Color(toRGB().toARGBInt());
   }
}
