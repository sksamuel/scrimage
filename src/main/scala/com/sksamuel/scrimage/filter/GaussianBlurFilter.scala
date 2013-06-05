package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter

/** @author Stephen Samuel */
class GaussianBlurFilter(radius: Int) extends BufferedOpFilter {
    val op = new com.jhlabs.image.GaussianFilter(radius)
}
object GaussianBlurFilter {
    def apply(): GaussianBlurFilter = apply(2)
    def apply(radius: Int): GaussianBlurFilter = new GaussianBlurFilter(radius)
}

