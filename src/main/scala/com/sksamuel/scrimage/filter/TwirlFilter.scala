package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter

/** @author Stephen Samuel */
class TwirlFilter(angle: Double, radius: Int, centerX: Float, centerY: Float) extends BufferedOpFilter {
    val op = new thirdparty.jhlabs.image.TwirlFilter()
    op.setCentreX(centerX)
    op.setCentreY(centerY)
    op.setRadius(radius.toFloat)
    op.setAngle(angle.toFloat)
}
object TwirlFilter {
    def apply(radius: Int): TwirlFilter = apply(Math.PI / 1.5, radius)
    def apply(angle: Double, radius: Int, centerX: Float = 0.5f, centerY: Float = 0.5f): TwirlFilter =
        new TwirlFilter(angle, radius, centerX, centerY)
}
