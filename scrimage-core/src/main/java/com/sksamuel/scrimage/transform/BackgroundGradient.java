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

   public BackgroundGradient(int width, int height) {
      this.width = width;
      this.height = height;
   }

   @Override
   public ImmutableImage apply(ImmutableImage input) throws IOException {
      // get the dominant colours
      RGBColor[] dominant = input.quantize(2);
      ImmutableImage bg = ImmutableImage.create(width, height);
      // A blank (e.g. all-white) image quantizes to no colours, and a single-colour
      // image may quantize to one; in either case there is no second stop. Mirror
      // DominantGradient / DominantGradientFilter: leave the canvas untouched
      // (transparent) when there is no colour at all, and fall back to a solid fill
      // of the only dominant colour when there is just one.
      if (dominant.length > 0) {
         RGBColor top = dominant[0];
         RGBColor bottom = dominant.length > 1 ? dominant[1] : dominant[0];
         // Create the vertical (top-to-bottom) linear gradient. The previous
         // code called LinearGradient.horizontal(...) which, due to a swapped
         // name bug fixed in the same commit as this file, actually produced
         // a vertical gradient — switching to vertical() preserves the visual
         // output that the existing fixture images depend on.
         bg = bg.fill(LinearGradient.vertical(top.awt(), bottom.awt()));
      }
      // write the input over
      return bg.overlay(input, Position.Center);
   }
}
