package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter

/** @author Stephen Samuel */
class ThresholdFilter(threshold: Int, white: Int, black: Int) extends BufferedOpFilter {
    val op = new com.jhlabs.image.ThresholdFilter(threshold)
    op.setBlack(black)
    op.setWhite(white)
}

object ThresholdFilter {
    def apply(): ThresholdFilter = apply(127)
    def apply(threshold: Int, white: Int = 0xffffff, black: Int = 0x000000): ThresholdFilter =
        new ThresholdFilter(threshold, white, black)
}