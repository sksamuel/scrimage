package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter

/** @author Stephen Samuel */
class GlowFilter(amount: Float = 0.5f) extends BufferedOpFilter {
    val op = new com.jhlabs.image.GlowFilter()
    op.setAmount(amount)
}
