package com.sksamuel.scrimage

import java.awt.image.BufferedImageOp
import java.awt.{Composite, Graphics2D}

/** @author Stephen Samuel */
trait Filter {
    def apply(image: Image): Image
}

/**
 * Extension of Filter that applies its filters using a standard java BufferedImageOp
 */
trait BufferedOpFilter extends Filter {
    val op: BufferedImageOp
    def apply(image: Image): Image = {
        val target = Image._empty(image.awt)
        val g2 = target.getGraphics.asInstanceOf[Graphics2D]
        g2.drawImage(image.awt, op, 0, 0)
        g2.dispose()
        new Image(target)
    }
}

/** @author Stephen Samuel
  *
  *         A filter that works by applying a java.awt.Composite to the entire image.
  *
  *
  **/
abstract class CompositeFilter extends Filter {
    val composite: Composite
    val color = java.awt.Color.RED
    def apply(image: Image): Image = {
        val target = Image._empty(image.awt)
        val g2 = target.getGraphics.asInstanceOf[Graphics2D]
        g2.setColor(color)
        g2.fillRect(0, 0, target.getWidth, target.getHeight)
        g2.setComposite(composite)
        g2.drawImage(image.awt, 0, 0, null)
        g2.dispose()
        new Image(target)
    }
}