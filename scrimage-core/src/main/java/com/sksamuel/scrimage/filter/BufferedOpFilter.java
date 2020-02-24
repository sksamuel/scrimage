package com.sksamuel.scrimage.filter;

import com.sksamuel.scrimage.ImmutableImage;

import java.awt.image.BufferedImageOp;

/**
 * Extension of Filter that applies its filters using a standard java BufferedImageOp.
 *
 * Filters that wish to provide an awt BufferedImageOp need to simply extend this class.
 */
public abstract class BufferedOpFilter implements Filter {

  public abstract BufferedImageOp op();

  public void apply(ImmutableImage image) {
    op().filter(image.awt(), image.awt());
  }
}
