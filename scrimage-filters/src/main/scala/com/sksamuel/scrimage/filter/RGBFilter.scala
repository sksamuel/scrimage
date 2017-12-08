package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter

class RGBFilter(r: Double = 0, g: Double = 0, b: Double = 0) extends BufferedOpFilter {
  require(r <= 1)
  require(g <= 1)
  require(b <= 1)
  val op = new thirdparty.jhlabs.image.RGBAdjustFilter()
  op.setBFactor(b.toFloat)
  op.setRFactor(r.toFloat)
  op.setGFactor(g.toFloat)
}

object RGBFilter {
  def apply(r: Double = 0, g: Double = 0, b: Double = 0): RGBFilter = new RGBFilter(r, g, b)
}
