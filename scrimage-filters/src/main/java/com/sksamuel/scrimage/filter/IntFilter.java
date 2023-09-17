package com.sksamuel.scrimage.filter;

import java.awt.image.BufferedImage;

public interface IntFilter extends Filter {
   @Override
   default int[] types() {
      return new int[]{BufferedImage.TYPE_INT_ARGB, BufferedImage.TYPE_INT_BGR, BufferedImage.TYPE_INT_RGB};
   }
}
