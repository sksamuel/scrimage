package com.sksamuel.scrimage.transform;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.Position;
import com.sksamuel.scrimage.canvas.painters.LinearGradient;
import com.sksamuel.scrimage.color.RGBColor;

import java.io.IOException;

/**
 * A transform that produces a linear gradient background underlay for
 * the two most dominent colours of an image.
 */
public class BackgroundGradient implements Transform {

   private final int width;
   private final int height;

   /**
    * Creates a filter that will output an image with the specified dimensions.
    * The source image will be centred in the output image, so it is expected that the
    * source image is pre-scaled to the required dimensions.
    */
   public BackgroundGradient(int width, int height) {
      this.width = width;
      this.height = height;
   }

   @Override
   public ImmutableImage apply(ImmutableImage image) throws IOException {
      // get the dominant colours
      RGBColor[] dominant = image.quantize(2);
      // create the linear gradient
      ImmutableImage bg = ImmutableImage.create(width, height)
         .fill(LinearGradient.horizontal(dominant[0].awt(), dominant[1].awt()));
      // write the input over
      return bg.overlay(image, Position.Center);
   }
}
