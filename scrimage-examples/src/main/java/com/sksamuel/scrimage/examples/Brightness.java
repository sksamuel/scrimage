package com.sksamuel.scrimage.examples;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.nio.JpegWriter;

import java.io.IOException;

public class Brightness {

   public static void main(String[] args) throws IOException {
      ImmutableImage.loader().fromResource("/input.jpg")
         .brightness(2.0)
         .forWriter(JpegWriter.Default).write("brightness_output_2.0.jpg");

      ImmutableImage.loader().fromResource("/input.jpg")
         .brightness(0.5)
         .forWriter(JpegWriter.Default).write("brightness_output_0.5.jpg");
   }
}
