package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter

/** @author Stephen Samuel */
class LensBlurFilter(radius: Float, bloom: Float, bloomThreshold: Float, sides: Int, angle: Float)
  extends BufferedOpFilter {
    val op = new com.jhlabs.image.LensBlurFilter()
    op.setSides(sides)
    op.setBloomThreshold(bloomThreshold)
    op.setBloom(bloom)
    op.setRadius(radius)
}
object LensBlurFilter {
    def apply() = new LensBlurFilter(5, 2, 255, 5, 0)
}
