package com.sksamuel.scrimage.transform;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.canvas.painters.LinearGradient;
import com.sksamuel.scrimage.color.RGBColor;

import java.io.IOException;

/**
 * Produces a top-to-bottom linear gradient between the two most dominant colours
 * of an image.
 *
 * @deprecated the output keeps the input's dimensions, so this is better
 * expressed as a size-preserving filter. Use
 * {@link com.sksamuel.scrimage.filter.DominantGradientFilter} instead.
 */
@Deprecated
public class DominantGradient implements Transform {

   @Override
   public ImmutableImage apply(ImmutableImage input) throws IOException {

      // get the dominant colours
      RGBColor[] dominant = input.quantize(2);

      // A blank (e.g. all-white) image quantizes to no colours, and a single-colour
      // image to one; in either case there is no second stop, so return a colourless
      // image untouched and fall back to a solid fill of the only dominant colour.
      if (dominant.length == 0) {
         return input;
      }
      RGBColor top = dominant[0];
      RGBColor bottom = dominant.length > 1 ? dominant[1] : dominant[0];

      // create the linear gradient image (top-to-bottom). The previous code
      // called LinearGradient.horizontal(...) which, due to a swapped name
      // bug fixed in the same commit as this file, actually produced a
      // vertical gradient — switching to vertical() preserves the visual
      // output that the existing fixture images depend on.
      return ImmutableImage.create(input.width, input.height)
         .fill(LinearGradient.vertical(top.awt(), bottom.awt()));
   }
}
