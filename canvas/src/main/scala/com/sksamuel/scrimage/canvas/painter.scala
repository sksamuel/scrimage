package com.sksamuel.scrimage.canvas

import java.awt.{RadialGradientPaint, GradientPaint, Paint}
import com.sksamuel.scrimage.{Color, RGBColor}

/** @author Stephen Samuel */
trait Painter {
  private[scrimage] def paint: Paint
}

object Painter {
  implicit def color2painter(color: java.awt.Color): RGBColor = color
  implicit def int2painter(argb: Int): RGBColor = argb
}

case class LinearGradient(x1: Int, y1: Int, color1: Color, x2: Int, y2: Int, color2: Color) extends Painter {
  private[scrimage] def paint = new GradientPaint(x1, y1, color1, x2, y2, color2)
}

case class RadialGradient(cx: Float,
                          cy: Float,
                          radius: Float,
                          fractions: Array[Float],
                          colors: Array[Color]) extends Painter {
  private[scrimage] def paint = new RadialGradientPaint(cx, cy, radius, fractions, colors.map(c => c.toRGB.toAWT))
}

object LinearGradient {
  def horizontal(color1: Color, color2: Color): LinearGradient = LinearGradient(0, 0, color1, 10, 0, color2)
  def vertical(color1: Color, color2: Color): LinearGradient = LinearGradient(0, 10, color1, 0, 0, color2)
}
