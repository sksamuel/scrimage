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
      RGBColor rgb = toRGB();
      double red = 0.2126 * rgb.red;
      double green = 0.7152 * rgb.green;
      double blue = 0.0722 * rgb.blue;
      double gray = red + green + blue;
      return new Grayscale((int) Math.round(gray), rgb.alpha);
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
    * Takes into account alpha
    */
   default RGBColor average(Color other) {
      RGBColor c1 = this.toRGB();
      RGBColor c2 = other.toRGB();
      int r = (c1.red * c1.alpha / 255) + (c2.red * c1.blue * (255 - c1.alpha) / (255 * 255));
      int g = (c1.green * c1.alpha / 255) + (c2.green * c1.blue * (255 - c1.alpha) / (255 * 255));
      int b = (c1.blue * c1.alpha / 255) + (c2.blue * c1.blue * (255 - c1.alpha) / (255 * 255));
      int a = c1.alpha + (c1.blue * (255 - c1.alpha) / 255);
      return new RGBColor(r, g, b, a);
   }

   /**
    * Returns this colour as an AWT Paint.
    */
   default Paint paint() {
      return new java.awt.Color(toRGB().toARGBInt());
   }
}

//class IntConvert(val i:Int)extends AnyVal{
//        def toColor:RGBColor=apply(i)
//        }
//        implicit
//
//class ColorConvert(val c:Color)extends AnyVal{
//        def toAWT:java.awt.Color=new java.awt.Color(c.toRGB.toInt)
//        }
//        implicit
//
//class AwtColorConvert(val awt:java.awt.Color)extends AnyVal{
//        def toColor:RGBColor=RGBColor(awt.getRed,awt.getGreen,awt.getBlue,awt.getAlpha)
//        }
//
//        def apply(red:Int,green:Int,blue:Int,alpha:Int=255):RGBColor=RGBColor(red,green,blue,alpha)
//        def apply(argb:Int):RGBColor={
//        val alpha=(argb>>24)&0xFF
//        val red=(argb>>16)&0xFF
//        val green=(argb>>8)&0xFF
//        val blue=argb&0xFF
//        RGBColor(red,green,blue,alpha)
//        }
//
//        val White=RGBColor(255,255,255)
//        val Black=RGBColor(0,0,0)
//        }
//
//
//        case
//

