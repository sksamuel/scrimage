package com.sksamuel.scrimage.examples;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.Position;
import com.sksamuel.scrimage.nio.JpegWriter;

import java.awt.*;
import java.io.IOException;

public class Resize {

   public static void main(String[] args) throws IOException {
      ImmutableImage image = ImmutableImage.loader().fromResource("/input.jpg").scaleToWidth(640);

      image.resize(0.75)
         .forWriter(JpegWriter.Default).write("resize_0.75.jpg");

      image.resizeTo(400, 400, Color.MAGENTA)
         .forWriter(JpegWriter.Default).write("resize_400_400.jpg");

      image.resizeTo(400, 300, Position.BottomRight)
         .forWriter(JpegWriter.Default).write("resize_400_300_br.jpg");

      image.resizeToWidth(400)
         .forWriter(JpegWriter.Default).write("resize_400.jpg");

      image.resizeToHeight(200)
         .forWriter(JpegWriter.Default).write("resize_200.jpg");
   }
}
