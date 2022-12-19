package com.sksamuel.scrimage.color;

public interface GrayscaleMethod {

   public static final GrayscaleMethod AVERAGE = new AverageGrayscale();
   public static final GrayscaleMethod LUMA = new LumaGrayscale();
   public static final GrayscaleMethod WEIGHTED = new WeightedGrayscale();

   Grayscale toGrayscale(Color color);
}

