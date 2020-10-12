package com.sksamuel.scrimage.examples;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.nio.JpegWriter;

import java.io.IOException;

public class Trim {

   public static void main(String[] args) throws IOException {
      ImmutableImage image = ImmutableImage.loader().fromResource("/input.jpg").scaleToWidth(640);

      image.trim(50)
         .forWriter(JpegWriter.Default).write("trim_50.jpg");

      image.trimLeft(100)
         .forWriter(JpegWriter.Default).write("trim_l100.jpg");

      image.trimBottom(200)
         .forWriter(JpegWriter.Default).write("trim_b200.jpg");
   }
}
