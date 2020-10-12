package com.sksamuel.scrimage.examples;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.nio.JpegWriter;

import java.io.IOException;

public class Bound {

   public static void main(String[] args) throws IOException {
      ImmutableImage image = ImmutableImage.loader().fromResource("/input.jpg").scaleToWidth(640);

      image.bound(400, 300)
         .forWriter(JpegWriter.Default).write("bound_400_300.jpg");

      image.bound(500, 200)
         .forWriter(JpegWriter.Default).write("bound_500_200.jpg");

      image.bound(300, 500)
         .forWriter(JpegWriter.Default).write("bound_300_500.jpg");
   }
}
