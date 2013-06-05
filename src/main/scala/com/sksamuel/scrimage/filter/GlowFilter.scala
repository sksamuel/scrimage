package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter

/** @author Stephen Samuel */
class GlowFilter(amount: Float) extends BufferedOpFilter {
    val op = new com.jhlabs.image.GlowFilter()
    op.setAmount(amount)
}
object GlowFilter {
    def apply(): GlowFilter = apply(0.5f)
    def apply(amount: Float): GlowFilter = new GlowFilter(amount)
}