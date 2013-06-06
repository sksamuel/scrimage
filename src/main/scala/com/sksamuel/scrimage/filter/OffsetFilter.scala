package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter

/** @author Stephen Samuel */
class OffsetFilter(x: Int, y: Int) extends BufferedOpFilter {
    val op = new thirdparty.jhlabs.image.OffsetFilter()
    op.setWrap(true)
    op.setXOffset(x)
    op.setYOffset(y)
}
object OffsetFilter {
    def apply(x: Int, y: Int): OffsetFilter = new OffsetFilter(x, y)
}