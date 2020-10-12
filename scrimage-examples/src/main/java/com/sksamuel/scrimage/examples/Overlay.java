package com.sksamuel.scrimage.examples;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.nio.JpegWriter;

import java.io.IOException;

public class Overlay {

   public static void main(String[] args) throws IOException {
      ImmutableImage image = ImmutableImage.loader().fromResource("/input.jpg").scaleToWidth(640);
      ImmutableImage picard = ImmutableImage.loader().fromResource("/picard.jpg").scaleToWidth(320);

      image.overlay(picard, 25, 25)
         .forWriter(JpegWriter.Default).write("overlay_25_25.jpg");

      image.overlay(picard, -75, 0)
         .forWriter(JpegWriter.Default).write("overlay_-75_0.jpg");
   }
}
