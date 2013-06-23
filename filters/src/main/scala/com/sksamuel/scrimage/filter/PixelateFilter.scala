package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter
import java.awt.image.BufferedImageOp

/** @author Stephen Samuel */
class PixelateFilter(blockSize: Int) extends BufferedOpFilter {
    val op: BufferedImageOp = new thirdparty.jhlabs.image.BlockFilter(blockSize)
}
object PixelateFilter {
    def apply(): PixelateFilter = apply(2)
    def apply(blockSize: Int): PixelateFilter = new PixelateFilter(blockSize)
}
