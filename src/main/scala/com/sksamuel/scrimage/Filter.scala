package com.sksamuel.scrimage

import java.awt.image.BufferedImageOp
import java.awt.{Composite, Graphics2D}
import thirdparty.marvin.image.{MarvinImageMask, MarvinAttributes, MarvinAbstractImagePlugin, MarvinImage}

/** @author Stephen Samuel */
trait Filter {
    def apply(image: Image)
}

/**
 * Extension of Filter that applies its filters using a standard java BufferedImageOp
 */
abstract class BufferedOpFilter extends Filter {
    val op: BufferedImageOp
    def apply(image: Image) {
        val g2 = image.awt.getGraphics.asInstanceOf[Graphics2D]
        g2.drawImage(image.awt, op, 0, 0)
        g2.dispose()
    }
}

/**
 * Implementation of Filter that provides filtering through delegation
 * to a plugin from the Marvin framework. The plugins are modified
 * so that the dependancy on the marvin gui is removed.
 *
 **/
abstract class MarvinFilter extends Filter {
    val plugin: MarvinAbstractImagePlugin
    def apply(image: Image) {

        val input = new MarvinImage(image.awt)
        val output = input.clone()

        plugin.process(input, output, new MarvinAttributes(), MarvinImageMask.NULL_MASK, false)

        input.setIntColorArray(output.getIntColorArray)
        input.update()
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
    def apply(image: Image) {
        val target = Image._empty(image.awt)
        val g2 = target.getGraphics.asInstanceOf[Graphics2D]
        g2.setColor(color)
        g2.fillRect(0, 0, target.getWidth, target.getHeight)
        g2.setComposite(composite)
        g2.drawImage(image.awt, 0, 0, null)
        g2.dispose()
    }
}