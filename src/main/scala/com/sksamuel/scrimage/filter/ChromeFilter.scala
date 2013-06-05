package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter

/** @author Stephen Samuel */
class ChromeFilter(amount: Float, exposure: Float) extends BufferedOpFilter {
    val op = new com.jhlabs.image.ChromeFilter()
    op.setAmount(amount)
    op.setExposure(exposure)
}
object ChromeFilter {
    def apply(): ChromeFilter = apply(0.5f, 1.0f)
    def apply(amount: Float, exposure: Float): ChromeFilter = new ChromeFilter(amount, exposure)
}