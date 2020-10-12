package com.sksamuel.scrimage.examples;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.nio.JpegWriter;

import java.io.IOException;

public class Contrast {

   public static void main(String[] args) throws IOException {
      ImmutableImage.loader().fromResource("/input.jpg")
         .contrast(2.0)
         .forWriter(JpegWriter.Default).write("contrast_2.0.jpg");

      ImmutableImage.loader().fromResource("/input.jpg")
         .contrast(0.5)
         .forWriter(JpegWriter.Default).write("contrast_0.5.jpg");
   }
}
