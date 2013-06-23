package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter

/** @author Stephen Samuel */
class RaysFilter(opacity: Float, threshold: Float, strength: Float) extends BufferedOpFilter {
    val op = new thirdparty.jhlabs.image.RaysFilter()
    op.setOpacity(opacity)
    op.setThreshold(threshold)
    op.setStrength(strength)
}
object RaysFilter {
    def apply(opacity: Float = 1.0f, threshold: Float = 0, strength: Float = 0.5f) = new RaysFilter(opacity, threshold, strength)
}
