package com.sksamuel.scrimage.transform;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.canvas.painters.LinearGradient;
import com.sksamuel.scrimage.color.RGBColor;

import java.io.IOException;

public class DominantGradient implements Transform {

   @Override
   public ImmutableImage apply(ImmutableImage input) throws IOException {

      // get the dominant colours
      RGBColor[] dominant = input.quantize(2);

      // create the linear gradient image (top-to-bottom). The previous code
      // called LinearGradient.horizontal(...) which, due to a swapped name
      // bug fixed in the same commit as this file, actually produced a
      // vertical gradient — switching to vertical() preserves the visual
      // output that the existing fixture images depend on.
      return ImmutableImage.create(input.width, input.height)
         .fill(LinearGradient.vertical(dominant[0].awt(), dominant[1].awt()));
   }
}
