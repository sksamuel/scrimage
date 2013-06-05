package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter

/** @author Stephen Samuel */
class DiffuseFilter(scale: Float) extends BufferedOpFilter {
    val op = new com.jhlabs.image.DiffuseFilter()
    op.setScale(scale)
}
object DiffuseFilter {
    def apply(): DiffuseFilter = apply(4)
    def apply(scale: Float): DiffuseFilter = new DiffuseFilter(scale)
}