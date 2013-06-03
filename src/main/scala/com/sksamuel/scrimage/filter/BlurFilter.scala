package com.sksamuel.scrimage.filter

import java.awt.image.Kernel

/** @author Stephen Samuel
  *
  *         A 3x3 convolution kernel for a simple blur.
  *
  **/
object BlurFilter
  extends ConvolveFilter(new Kernel(3, 3, Array[Float](1 / 14f, 2 / 14f, 1 / 14f, 2 / 14f, 2 / 14f, 2 / 14f, 1 / 14f, 2 / 14f, 1 / 14f)))


