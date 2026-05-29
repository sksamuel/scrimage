package com.sksamuel.scrimage.filter;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.canvas.painters.LinearGradient;
import com.sksamuel.scrimage.color.RGBColor;

import java.io.IOException;

/**
 * Replaces the image with a top-to-bottom linear gradient between its two most
 * dominant colours.
 *
 * <p>This is the same implementation as the (deprecated)
 * {@link com.sksamuel.scrimage.transform.DominantGradient} transform; because
 * the output keeps the input's dimensions it is expressed here as a size
 * preserving {@link Filter}.
 */
public class DominantGradientFilter implements Filter {

   @Override
   public void apply(ImmutableImage image) throws IOException {
      RGBColor[] dominant = image.quantize(2);
      ImmutableImage gradient = ImmutableImage.create(image.width, image.height)
         .fill(LinearGradient.vertical(dominant[0].awt(), dominant[1].awt()));
      int w = image.width;
      int h = image.height;
      int[] pixels = gradient.awt().getRGB(0, 0, w, h, null, 0, w);
      image.awt().setRGB(0, 0, w, h, pixels, 0, w);
   }
}
