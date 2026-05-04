package com.sksamuel.scrimage.filter;

import com.sksamuel.scrimage.ImmutableImage;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;

/**
 * A filter that just applies one or more filters in sequence.
 */
public class PipelineFilter implements Filter {

   private final Collection<Filter> filters;

   public PipelineFilter(Collection<Filter> filters) {
      this.filters = filters;
   }

   /**
    * The pipeline only operates correctly on TYPE_INT_ARGB / TYPE_INT_RGB
    * images. Declare that here so {@link ImmutableImage#filter(Filter)}
    * converts the image before delegating to {@link #apply(ImmutableImage)}.
    *
    * Previously the conversion was done inside apply() — but it conditionally
    * created a *new* ImmutableImage and discarded it after the filters ran,
    * so for any non-INT-ARGB/RGB source the entire pipeline silently became
    * a no-op (NashvilleFilter on TYPE_4BYTE_ABGR did nothing).
    */
   @Override
   public int[] types() {
      return new int[]{BufferedImage.TYPE_INT_ARGB, BufferedImage.TYPE_INT_RGB};
   }

   @Override
   public void apply(ImmutableImage image) throws IOException {
      for (Filter filter : filters) {
         filter.apply(image);
      }
   }
}
