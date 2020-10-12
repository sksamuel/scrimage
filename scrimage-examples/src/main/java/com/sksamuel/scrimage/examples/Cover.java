package com.sksamuel.scrimage.examples;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.Position;
import com.sksamuel.scrimage.nio.JpegWriter;

import java.io.IOException;

public class Cover {

   public static void main(String[] args) throws IOException {
      ImmutableImage image = ImmutableImage.loader().fromResource("/input.jpg").scaleToWidth(640);

      image.cover(400, 300)
         .forWriter(JpegWriter.Default).write("cover_400_300.jpg");

      image.cover(500, 200)
         .forWriter(JpegWriter.Default).write("cover_500_200.jpg");

      image.cover(500, 200, Position.TopLeft)
         .forWriter(JpegWriter.Default).write("cover_500_200_top_left.jpg");

      image.cover(400, 400)
         .forWriter(JpegWriter.Default).write("cover_400_400.jpg");

      image.cover(400, 400, Position.CenterRight)
         .forWriter(JpegWriter.Default).write("cover_400_400_center_right.jpg");
   }
}
