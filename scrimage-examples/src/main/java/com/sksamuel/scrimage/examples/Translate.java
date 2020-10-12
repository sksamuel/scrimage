package com.sksamuel.scrimage.examples;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.nio.JpegWriter;

import java.awt.*;
import java.io.IOException;

public class Translate {

   public static void main(String[] args) throws IOException {
      ImmutableImage image = ImmutableImage.loader().fromResource("/input.jpg").scaleToWidth(640);

      image.translate(120, 80)
         .forWriter(JpegWriter.Default).write("translate_120_80.jpg");

      image.translate(100, 0)
         .forWriter(JpegWriter.Default).write("translate_100_0.jpg");

      image.translate(100, 0, Color.BLUE)
         .forWriter(JpegWriter.Default).write("translate_100_0_blue.jpg");

      image.translate(-120, -80, Color.RED)
         .forWriter(JpegWriter.Default).write("translate_-120_-80.jpg");
   }
}
