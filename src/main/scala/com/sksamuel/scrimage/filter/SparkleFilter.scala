package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter

/** @author Stephen Samuel */
class SparkleFilter(x: Int, y: Int, rays: Int, radius: Int, amount: Int) extends BufferedOpFilter {
    val op = new com.jhlabs.image.SparkleFilter()
    op.setRays(rays)
    op.setRadius(radius)
    op.setAmount(amount)
    op.setDimensions(x, y)
}
object SparkleFilter {
    def apply(x: Int = 0, y: Int = 0, rays: Int = 50, radius: Int = 25, amount: Int = 50) =
        new SparkleFilter(x, y, rays, radius, amount)
}