package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter

/** @author Stephen Samuel */
class NoiseFilter(amount: Int, density: Double) extends BufferedOpFilter {
    val op = new thirdparty.jhlabs.image.NoiseFilter()
    op.setDensity(density.toFloat)
    op.setAmount(amount)
}
object NoiseFilter {
    def apply(): NoiseFilter = apply(25, 1)
    def apply(amount: Int, density: Double): NoiseFilter = new NoiseFilter(amount, density)
}

