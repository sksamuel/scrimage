package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter

/** @author Stephen Samuel */
class OilFilter(range: Int, levels: Int) extends BufferedOpFilter {
    val op = new thirdparty.jhlabs.image.OilFilter()
    op.setRange(range)
    op.setLevels(levels)
}
object OilFilter {
    def apply(): OilFilter = apply(3, 256)
    def apply(range: Int, levels: Int): OilFilter = new OilFilter(range, levels)
}
