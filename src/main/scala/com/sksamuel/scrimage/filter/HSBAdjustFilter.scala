package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter

/** @author Stephen Double */
class HSBFilter(hue: Double, saturation: Double, brightness: Double) extends BufferedOpFilter {
    require(hue <= 1)
    require(brightness <= 1)
    require(saturation <= 1)
    val op = new thirdparty.jhlabs.image.HSBAdjustFilter()
    op.setHFactor(hue.toFloat)
    op.setSFactor(saturation.toFloat)
    op.setBFactor(brightness.toFloat)
}
object HSBFilter {
    def apply(hue: Double = 0, saturation: Double = 0, brightness: Double = 0): HSBFilter = new HSBFilter(hue, saturation, brightness)
}