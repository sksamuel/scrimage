package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.{MutableAwtImage, AwtImage, Image, Color, Filter, RGBColor}

/**
 * Created by guw on 23/09/14.
 */
case class ErrorSpotterFilter(base: Image, ratio: Int = 50) extends Filter {

  def apply(src: Image): Unit = {
    assert(src.width == base.width)
    assert(src.height == base.height)

    for (x <- 0 until src.width; y <- 0 until src.height) {
      val delta = error(base.awt.getRGB(x, y), src.awt.getRGB(x, y))
      src.awt.setRGB(x, y, delta)
    }
  }

  def error(rgb1: RGBColor, rgb2: RGBColor): Int = {
    var red, blue, delta = 0
    delta = rgb1.red - rgb2.red
    if (delta > 0) red += delta
    else blue -= delta
    delta = rgb1.blue - rgb2.blue
    if (delta > 0) red += delta
    else blue -= delta
    delta = rgb1.green - rgb2.green
    if (delta > 0) red += delta
    else blue -= delta
    delta = rgb1.alpha - rgb2.alpha
    if (delta > 0) red += delta
    else blue -= delta
    Color(math.min(ratio * red, 255), 0, math.min(ratio * blue, 255)).toInt
  }

  def truncate(a: Int): Int = {
    if (a < 0) 0
    else if (a > 255) 255
    else a
  }
}