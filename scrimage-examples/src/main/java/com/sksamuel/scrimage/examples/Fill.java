package com.sksamuel.scrimage.examples;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.canvas.painters.LinearGradient;
import com.sksamuel.scrimage.nio.JpegWriter;

import java.awt.*;
import java.io.IOException;

public class Fill {

   public static void main(String[] args) throws IOException {
      ImmutableImage image = ImmutableImage.loader().fromResource("/input.jpg").scaleToWidth(640);

      image.fill(Color.BLUE)
         .forWriter(JpegWriter.Default).write("fill_blue.jpg");

      image.fill(LinearGradient.vertical(Color.BLACK, Color.WHITE))
         .forWriter(JpegWriter.Default).write("fill_linear_gradient_vertical.jpg");

      image.fill(LinearGradient.horizontal(Color.BLACK, Color.WHITE))
         .forWriter(JpegWriter.Default).write("fill_linear_gradient_horizontal.jpg");
   }
}
