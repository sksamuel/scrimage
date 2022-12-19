package com.sksamuel.scrimage.color;

public class WeightedGrayscale implements GrayscaleMethod {
   @Override
   public Grayscale toGrayscale(Color color) {
      RGBColor rgb = color.toRGB();
      double red = 0.299 * rgb.red;
      double green = 0.587 * rgb.green;
      double blue = 0.114 * rgb.blue;
      double gray = red + green + blue;
      return new Grayscale((int) Math.round(gray), rgb.alpha);
   }
}
