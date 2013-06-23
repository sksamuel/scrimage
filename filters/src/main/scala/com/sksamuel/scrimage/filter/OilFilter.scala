package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter

/** @author Stephen Samuel */
class OilFilter(range: Int, levels: Int) extends BufferedOpFilter {
    val op = new thirdparty.jhlabs.image.OilFilter()
    op.setRange(range)
    op.setLevels(levels)
}
object OilFilter {
    def apply(range: Int = 3, levels: Int = 256): OilFilter = new OilFilter(range, levels)
}
