package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter

/** @author Stephen Samuel */
class MotionBlurFilter(angle: Double, distance: Double, rotation: Double, zoom: Double) extends BufferedOpFilter {
    val op = new thirdparty.jhlabs.image.MotionBlurFilter()
    op.setAngle(angle.toFloat)
    op.setDistance(distance.toFloat)
    op.setRotation(rotation.toFloat)
    op.setZoom(zoom.toFloat)
}
object MotionBlurFilter {
    def apply(angle: Double, distance: Double, rotation: Double = 0, zoom: Double = 0) =
        new MotionBlurFilter(angle, distance, rotation, zoom)
}