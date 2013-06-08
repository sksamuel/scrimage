package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter

/** @author Stephen Samuel */
class HSBFilter(hue: Float, saturation: Float, brightness: Float) extends BufferedOpFilter {
    val op = new thirdparty.jhlabs.image.HSBAdjustFilter()
    op.setHFactor(hue)
    op.setSFactor(saturation)
    op.setBFactor(brightness)
}
object HSBFilter {
    def apply(hue: Float, saturation: Float, brightness: Float): HSBFilter = new HSBFilter(hue, saturation, brightness)
}