package com.sksamuel.scrimage.color;

public class LumaGrayscale implements GrayscaleMethod {
   @Override
   public Grayscale toGrayscale(Color color) {
      RGBColor rgb = color.toRGB();
      double red = 0.2126 * rgb.red;
      double green = 0.7152 * rgb.green;
      double blue = 0.0722 * rgb.blue;
      double gray = red + green + blue;
      return new Grayscale((int) Math.round(gray), rgb.alpha);
   }
}
