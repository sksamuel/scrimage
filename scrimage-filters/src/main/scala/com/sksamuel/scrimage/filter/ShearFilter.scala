package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter

class ShearFilter(xAngle: Double, yAngle: Double) extends BufferedOpFilter {
  val op = new thirdparty.jhlabs.image.ShearFilter()
  op.setXAngle(xAngle.toFloat)
  op.setYAngle(yAngle.toFloat)
  op.setResize(false)
}

object ShearFilter {
  def apply(xAngle: Double, yAngle: Double) = new ShearFilter(xAngle, yAngle)
}