package com.sksamuel.scrimage

object ScaleMethod {
  object FastScale extends ScaleMethod
  object Lanczos3 extends ScaleMethod
  object BSpline extends ScaleMethod
  object Bilinear extends ScaleMethod
  object Bicubic extends ScaleMethod
}

sealed trait ScaleMethod