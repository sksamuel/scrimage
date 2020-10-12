package com.sksamuel.scrimage.examples;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.ScaleMethod;
import com.sksamuel.scrimage.nio.JpegWriter;

import java.io.IOException;

public class Zoom {

   public static void main(String[] args) throws IOException {
      ImmutableImage image = ImmutableImage.loader().fromResource("/input.jpg").scaleToWidth(640);

      image.zoom(1.3)
         .forWriter(JpegWriter.Default).write("zoom.jpg");

      image.zoom(1.3, ScaleMethod.FastScale)
         .forWriter(JpegWriter.Default).write("zoom_fastscale.jpg");
   }
}
