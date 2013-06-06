package com.sksamuel.scrimage

import java.awt.image.{AffineTransformOp, BufferedImage}
import java.io.{File, InputStream}
import java.awt.geom.AffineTransform

/** @author Stephen Samuel */
class MutableImage(awt: BufferedImage) extends Image(awt) {

    override def _flip(tx: AffineTransform): MutableImage = {
        val op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR)
        op.filter(awt, awt)
        this
    }

    override def filter(filter: Filter): Image = {
        filter.apply(this)
        this
    }

    override def filled(color: java.awt.Color): Image = super._fill(color)
}

object MutableImage {
    def apply(in: InputStream): MutableImage = Image(in).toMutable
    def apply(file: File): MutableImage = Image(file).toMutable
    def apply(awt: java.awt.Image): MutableImage = Image(awt).toMutable
    def apply(image: Image): MutableImage = image.toMutable
}