package com.sksamuel.scrimage.examples;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.nio.JpegWriter;

import java.io.IOException;

public class Flip {

   public static void main(String[] args) throws IOException {
      ImmutableImage image = ImmutableImage.loader().fromResource("/input.jpg").scaleToWidth(620);

      image.flipX()
         .forWriter(JpegWriter.Default).write("flip_x.jpg");

      image.flipY()
         .forWriter(JpegWriter.Default).write("flip_y.jpg");
   }
}
