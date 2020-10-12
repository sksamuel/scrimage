package com.sksamuel.scrimage.examples;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.nio.JpegWriter;

import java.awt.*;
import java.io.IOException;

public class Fit {

   public static void main(String[] args) throws IOException {
      ImmutableImage image = ImmutableImage.loader().fromResource("/input.jpg").scaleToWidth(640);

      image.fit(400, 300, Color.DARK_GRAY)
         .forWriter(JpegWriter.Default).write("fit_400_300.jpg");

      image.fit(300, 300, Color.BLUE)
         .forWriter(JpegWriter.Default).write("fit_300_300.jpg");

      image.fit(400, 100, Color.RED)
         .forWriter(JpegWriter.Default).write("fit_400_100.jpg");
   }
}
