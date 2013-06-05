package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter

/** @author Stephen Samuel */
class GaussianBlurFilter(radius: Int = 2) extends BufferedOpFilter {
    val op = new com.jhlabs.image.GaussianFilter(radius)
}
object GaussianBlurFilter {
    def apply() = new GaussianBlurFilter()
    def apply(radius: Int) = new GaussianBlurFilter(radius)
}

