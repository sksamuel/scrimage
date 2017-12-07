package com.sksamuel.scrimage

import java.awt.Graphics2D
import java.awt.image.BufferedImageOp

/**
 * Extension of Filter that applies its filters using a standard java BufferedImageOp.
 *
 * Filters that wish to provide an awt BufferedImageOp need to simply extend this class.
 */
abstract class BufferedOpFilter extends Filter {
  def op: BufferedImageOp
  def apply(image: Image): Unit = {
    val g2 = image.awt.getGraphics.asInstanceOf[Graphics2D]
    g2.drawImage(image.awt, op, 0, 0)
    g2.dispose()
  }
}
