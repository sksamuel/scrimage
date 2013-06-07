package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.{Image, BufferedOpFilter}
import java.awt.image._

/** @author Stephen Samuel */
class BrightenFilter(amount: Double) extends BufferedOpFilter {
    val op = new RescaleOp(amount.toFloat, 0, null)
    override def apply(image: Image) {
        op.filter(image.awt, image.awt)
    }
}

object BrightenFilter {
    def apply(amount: Double) = new BrightenFilter(amount)
}














