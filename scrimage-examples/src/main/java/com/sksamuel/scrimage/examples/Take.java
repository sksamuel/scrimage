package com.sksamuel.scrimage.examples;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.nio.JpegWriter;

import java.io.IOException;

public class Take {

   public static void main(String[] args) throws IOException {
      ImmutableImage image = ImmutableImage.loader().fromResource("/input.jpg").scaleToWidth(640);

      image.takeLeft(300)
         .forWriter(JpegWriter.Default).write("take_l300.jpg");

      image.takeLeft(300).takeTop(200)
         .forWriter(JpegWriter.Default).write("take_l300_t200.jpg");

      image.takeRight(400).takeBottom(200)
         .forWriter(JpegWriter.Default).write("take_r400_b200.jpg");
   }
}
