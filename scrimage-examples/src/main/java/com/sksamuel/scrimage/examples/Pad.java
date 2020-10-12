package com.sksamuel.scrimage.examples;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.nio.JpegWriter;

import java.awt.*;
import java.io.IOException;

public class Pad {

   public static void main(String[] args) throws IOException {
      ImmutableImage image = ImmutableImage.loader().fromResource("/input.jpg").scaleToWidth(640);

      image
         .scale(0.5)
         .pad(10, Color.DARK_GRAY)
         .forWriter(JpegWriter.Default).write("pad_10.jpg");

      image
         .scale(0.5)
         .padRight(25, Color.BLUE)
         .padBottom(40, Color.RED)
         .forWriter(JpegWriter.Default).write("pad_r25_b40.jpg");
   }
}
