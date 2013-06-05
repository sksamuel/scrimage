package com.sksamuel.scrimage

import java.awt.image.BufferedImageOp
import java.awt.Graphics2D

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