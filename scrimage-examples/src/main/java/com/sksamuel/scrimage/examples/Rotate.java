package com.sksamuel.scrimage.examples;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.angles.Radians;
import com.sksamuel.scrimage.nio.JpegWriter;

import java.io.IOException;

public class Rotate {

   public static void main(String[] args) throws IOException {
      ImmutableImage image = ImmutableImage.loader().fromResource("/input.jpg").scaleToWidth(640);

      image.rotateLeft()
         .forWriter(JpegWriter.Default).write("rotate_left.jpg");

      image.rotateRight()
         .forWriter(JpegWriter.Default).write("rotate_right.jpg");

      image.rotate(new Radians(1.15))
         .forWriter(JpegWriter.Default).write("rotate_1.15_rads.jpg");
   }
}
