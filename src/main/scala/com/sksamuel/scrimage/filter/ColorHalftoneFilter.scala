package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter

/** @author Stephen Samuel */
class ColorHalftoneFilter(radius: Double) extends BufferedOpFilter {
    val op = new thirdparty.jhlabs.image.ColorHalftoneFilter()
    op.setdotRadius(radius.toFloat)
}
object ColorHalftoneFilter {
    def apply(radius: Double = 1.2): ColorHalftoneFilter = new ColorHalftoneFilter(radius)
}

