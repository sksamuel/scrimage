package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter

/** @author Stephen Samuel */
class QuantizeFilter(colors: Int, dither: Boolean) extends BufferedOpFilter {
    val op = new com.jhlabs.image.QuantizeFilter
    op.setNumColors(colors)
    op.setDither(dither)
}
object QuantizeFilter {
    def apply(): QuantizeFilter = apply(256, false)
    def apply(colors: Int, dither: Boolean = false): QuantizeFilter = new QuantizeFilter(colors, dither)
}

