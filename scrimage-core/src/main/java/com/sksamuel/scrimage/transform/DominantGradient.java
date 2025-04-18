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

      // create the linear gradient image
      return ImmutableImage.create(input.width, input.height)
         .fill(LinearGradient.horizontal(dominant[0].awt(), dominant[1].awt()));
   }
}
