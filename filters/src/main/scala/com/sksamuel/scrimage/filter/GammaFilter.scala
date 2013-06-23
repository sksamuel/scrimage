package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter

/** @author Stephen Samuel */
class GammaFilter(gamma: Float) extends BufferedOpFilter {
    val op = new thirdparty.jhlabs.image.GammaFilter()
    op.setGamma(gamma)
}
object GammaFilter {
    def apply(): GammaFilter = apply(1.0f)
    def apply(gamma: Float): GammaFilter = new GammaFilter(gamma)
}