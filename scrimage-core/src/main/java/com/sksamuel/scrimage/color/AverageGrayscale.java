package com.sksamuel.scrimage.color;

/**
 * Returns a Grayscale copy of this using the average method.
 * The Average method takes the average value of R, G, and B as the grayscale value.
 */
public class AverageGrayscale implements GrayscaleMethod {

   @Override
   public Grayscale toGrayscale(Color color) {
      RGBColor rgb = color.toRGB();
      int average = rgb.average();
      return new Grayscale(average, rgb.alpha);
   }
}
