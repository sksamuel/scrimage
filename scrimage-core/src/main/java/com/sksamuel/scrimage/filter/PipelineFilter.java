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

   @Override
   public void apply(ImmutableImage image) throws IOException {

      ImmutableImage copy;
      if (image.getType() == BufferedImage.TYPE_INT_ARGB || image.getType() == BufferedImage.TYPE_INT_RGB) {
         copy = image;
      } else {
         copy = image.copy(BufferedImage.TYPE_INT_ARGB);
      }

      for (Filter filter : filters) {
         filter.apply(copy);
      }
   }
}
