/*
 * Copyright 2013, Morten Nobel-Joergensen
 *
 * License: The BSD 3-Clause License
 * http://opensource.org/licenses/BSD-3-Clause
 */
package thirdparty.mortennobel;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;

/**
 * @author Morten Nobel-Joergensen
 */
public abstract class AdvancedResizeOp implements BufferedImageOp {

  protected final int destWidth;
  protected final int destHeight;

  public AdvancedResizeOp(final int destWidth, final int destHeight) {
    this.destWidth = destWidth;
    this.destHeight = destHeight;
  }

  public final BufferedImage filter(BufferedImage src, BufferedImage dest) {
    BufferedImage bufferedImage = doFilter(src, dest, destWidth, destHeight);
    return bufferedImage;
  }

  protected abstract BufferedImage doFilter(BufferedImage src, BufferedImage dest, int dstWidth, int dstHeight);

  /**
   * {@inheritDoc}
   */
  public final Rectangle2D getBounds2D(BufferedImage src) {
    return new Rectangle(0, 0, src.getWidth(), src.getHeight());
  }

  /**
   * {@inheritDoc}
   */
  public final BufferedImage createCompatibleDestImage(BufferedImage src,
                                                       ColorModel destCM) {
    if (destCM == null) {
      destCM = src.getColorModel();
    }
    return new BufferedImage(destCM,
        destCM.createCompatibleWritableRaster(
            src.getWidth(), src.getHeight()),
        destCM.isAlphaPremultiplied(), null);
  }

  /**
   * {@inheritDoc}
   */
  public final Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
    return (Point2D) srcPt.clone();
  }

  /**
   * {@inheritDoc}
   */
  public final RenderingHints getRenderingHints() {
    return null;
  }
}