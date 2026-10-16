package com.sksamuel.scrimage.filter;

import java.awt.image.BufferedImage;

/**
 * Marker interface for filters that composite with {@link thirdparty.romainguy.BlendComposite}.
 * <p>
 * BlendComposite only accepts rasters backed by int data buffers, and it reads the alpha
 * channel from the raw data elements. For TYPE_INT_RGB and TYPE_INT_BGR those elements carry
 * no alpha byte, so every pixel was treated as fully transparent and the blend contributed
 * nothing. The only type on which the composite behaves correctly is TYPE_INT_ARGB, so that
 * is the only type declared here; {@link com.sksamuel.scrimage.ImmutableImage#filter(Filter)}
 * converts other images before applying the filter.
 */
public interface IntFilter extends Filter {
   @Override
   default int[] types() {
      return new int[]{BufferedImage.TYPE_INT_ARGB};
   }
}
