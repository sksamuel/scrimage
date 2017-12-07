package com.sksamuel.scrimage;

import java.awt.*;
import java.awt.image.BufferedImageOp;

/**
 * Extension of Filter that applies its filters using a standard java BufferedImageOp.
 *
 * Filters that wish to provide an awt BufferedImageOp need to simply extend this class.
 */
public abstract class BufferedOpFilter implements Filter {

  public abstract BufferedImageOp op();

  public void apply(Image image) {
    Graphics2D g2 = (Graphics2D) image.awt().getGraphics();
    g2.drawImage(image.awt(), op(), 0, 0);
    g2.dispose();
  }
}
