package com.sksamuel.scrimage.examples;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.ScaleMethod;
import com.sksamuel.scrimage.nio.JpegWriter;

import java.io.IOException;

public class ScaleMethods {

   public static void main(String[] args) throws IOException {
      ImmutableImage image = ImmutableImage.loader().fromResource("/input.jpg");

      image.scaleToWidth(1600, ScaleMethod.FastScale)
         .forWriter(JpegWriter.Default).write("scale_fast_scale.jpg");

      image.scaleToWidth(1600, ScaleMethod.Bicubic)
         .forWriter(JpegWriter.Default).write("scale_bicubic.jpg");

      image.scaleToWidth(1600, ScaleMethod.Bilinear)
         .forWriter(JpegWriter.Default).write("scale_bilinear.jpg");

      image.scaleToWidth(1600, ScaleMethod.BSpline)
         .forWriter(JpegWriter.Default).write("scale_bspline.jpg");

      image.scaleToWidth(1600, ScaleMethod.Lanczos3)
         .forWriter(JpegWriter.Default).write("scale_lanczos3.jpg");
   }
}
