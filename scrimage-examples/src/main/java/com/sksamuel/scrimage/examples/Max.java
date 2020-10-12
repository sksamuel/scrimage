package com.sksamuel.scrimage.examples;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.nio.JpegWriter;

import java.io.IOException;

public class Max {

   public static void main(String[] args) throws IOException {
      ImmutableImage image = ImmutableImage.loader().fromResource("/input.jpg").scaleToWidth(640);

      image.max(400, 300)
         .forWriter(JpegWriter.Default).write("max_400_300.jpg");

      image.max(300, 300)
         .forWriter(JpegWriter.Default).write("max_300_300.jpg");

      image.max(200, 400)
         .forWriter(JpegWriter.Default).write("max_200_400.jpg");
   }
}
