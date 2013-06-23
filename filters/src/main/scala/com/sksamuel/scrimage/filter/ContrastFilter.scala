package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter

/** @author Stephen Samuel */
class ContrastFilter(contrast: Double) extends BufferedOpFilter {
    val op = new thirdparty.jhlabs.image.ContrastFilter
    op.setBrightness(1.0f)
    op.setContrast(contrast.toFloat)
}
object ContrastFilter {
    def apply(contrast: Double): ContrastFilter = new ContrastFilter(contrast)
}
