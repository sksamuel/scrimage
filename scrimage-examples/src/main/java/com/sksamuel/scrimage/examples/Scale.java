package com.sksamuel.scrimage.examples;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.ScaleMethod;
import com.sksamuel.scrimage.nio.JpegWriter;

import java.io.IOException;

public class Scale {

   public static void main(String[] args) throws IOException {
      ImmutableImage image = ImmutableImage.loader().fromResource("/input.jpg").scaleToWidth(640);

      image.scaleToWidth(400)
         .forWriter(JpegWriter.Default).write("scale_w400.jpg");

      image.scaleToHeight(200)
         .forWriter(JpegWriter.Default).write("scale_h200.jpg");

      image.scaleTo(400, 400)
         .forWriter(JpegWriter.Default).write("scale_400_400.jpg");

      image.scaleTo(400, 400, ScaleMethod.FastScale)
         .forWriter(JpegWriter.Default).write("scale_400_400_fast.jpg");

      image.scale(0.5)
         .forWriter(JpegWriter.Default).write("scale_0.5.jpg");
   }
}
