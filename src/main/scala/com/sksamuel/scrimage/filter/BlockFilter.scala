package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter
import java.awt.image.BufferedImageOp

/** @author Stephen Samuel */
class BlockFilter(blockSize: Int) extends BufferedOpFilter {
    val op: BufferedImageOp = new thirdparty.jhlabs.image.BlockFilter(blockSize)
}
object BlockFilter {
    def apply(): BlockFilter = apply(2)
    def apply(blockSize: Int): BlockFilter = new BlockFilter(blockSize)
}
