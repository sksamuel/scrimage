package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter

/** @author Stephen Samuel */
class QuantizeFilter(colors: Int, dither: Boolean) extends BufferedOpFilter {
  val op = new thirdparty.jhlabs.image.QuantizeFilter
  op.setNumColors(colors)
  op.setDither(dither)
}
object QuantizeFilter {
  def apply(colors: Int = 256, dither: Boolean = false): QuantizeFilter = new QuantizeFilter(colors, dither)
}

