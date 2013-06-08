package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter

/** @author Stephen Samuel */
class PosterizeFilter(radius: Float, bloom: Float, bloomThreshold: Float, sides: Int, angle: Float)
  extends BufferedOpFilter {
    val op = new thirdparty.jhlabs.image.PosterizeFilter()
    op.setNumLevels(8)
}
object PosterizeFilter {
    def apply(): PosterizeFilter = new PosterizeFilter(5, 2, 255, 5, 0)
}