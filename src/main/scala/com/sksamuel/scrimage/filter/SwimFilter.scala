package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter

/** @author Stephen Samuel */
class SwimFilter(amount: Double, stretch: Double) extends BufferedOpFilter {
    val op = new thirdparty.jhlabs.image.SwimFilter()
    op.setAmount(amount.toFloat)
    op.setStretch(stretch.toFloat)
}
object SwimFilter {
    def apply(): SwimFilter = apply(6f, 2f)
    def apply(amount: Double, stretch: Double): SwimFilter = new SwimFilter(amount, stretch)
}
