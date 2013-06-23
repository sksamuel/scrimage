package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter

/** @author Stephen Samuel */
class GaussianBlurFilter(radius: Int) extends BufferedOpFilter {
    val op = new thirdparty.jhlabs.image.GaussianFilter(radius)
}
object GaussianBlurFilter {
    def apply(radius: Int = 2): GaussianBlurFilter = new GaussianBlurFilter(radius)
}

