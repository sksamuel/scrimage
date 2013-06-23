package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter

/** @author Stephen Samuel */
class CrystallizeFilter(scale: Double, edgeThickness: Double, edgeColor: Int, randomness: Double) extends BufferedOpFilter {
    val op = new thirdparty.jhlabs.image.CrystallizeFilter
    op.setEdgeColor(edgeColor)
    op.setEdgeThickness(edgeThickness.toFloat)
    op.setScale(scale.toFloat)
    op.setRandomness(randomness.toFloat)
}
object CrystallizeFilter {
    def apply(scale: Double = 16, edgeThickness: Double = 0.4, edgeColor: Int = 0xff000000, randomness: Double = 0.2): CrystallizeFilter =
        new CrystallizeFilter(scale, edgeThickness, edgeColor, randomness)
}
