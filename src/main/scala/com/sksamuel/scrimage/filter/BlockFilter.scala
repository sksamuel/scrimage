package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter
import java.awt.image.BufferedImageOp

/** @author Stephen Samuel */
class BlockFilter(blockSize: Int) extends BufferedOpFilter {
    val op: BufferedImageOp = new com.jhlabs.image.BlockFilter(blockSize)
}
object BlockFilter {
    def apply(): BlockFilter = new BlockFilter(2)
    def apply(blockSize: Int): BlockFilter = new BlockFilter(blockSize)
}
