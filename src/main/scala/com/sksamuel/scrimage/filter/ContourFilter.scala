package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter

/** @author Stephen Samuel */
class ContourFilter(levels: Int) extends BufferedOpFilter {
    val op = new thirdparty.jhlabs.image.ContourFilter
    op.setLevels(levels)
}
object ContourFilter {
    def apply(): ContourFilter = apply(3)
    def apply(levels: Int): ContourFilter = new ContourFilter(levels)
}
