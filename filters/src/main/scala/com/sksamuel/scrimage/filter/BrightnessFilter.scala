package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter

/** @author Stephen Samuel */
class BrightnessFilter(brightness: Double) extends BufferedOpFilter {
    val op = new thirdparty.jhlabs.image.ContrastFilter
    op.setBrightness(brightness.toFloat)
    op.setContrast(1.0f)
}

object BrightnessFilter {
    def apply(amount: Double) = new BrightnessFilter(amount)
}














