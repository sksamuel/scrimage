package com.sksamuel.scrimage.canvas

import java.awt._
import com.sksamuel.scrimage.Color
import java.awt.image.{ Raster, ColorModel }
import java.awt.geom.{ AffineTransform, Rectangle2D }
import com.sksamuel.scrimage.RGBColor
import sun.awt.image.IntegerComponentRaster
import scala.util.Random

/** @author Stephen Samuel */
trait Painter {
  private[scrimage] def paint: Paint
}

object Painter {
  implicit def color2painter(color: java.awt.Color): RGBColor = {
    Color(color.getRed, color.getGreen, color.getBlue, color.getAlpha)
  }
  implicit def color2painter(color: Color): ColorPainter = ColorPainter(color)
  implicit def int2painter(argb: Int): ColorPainter = ColorPainter(argb)
}

case class ColorPainter(color: Color) extends Painter {
  private[scrimage] def paint: Paint = color.toRGB.toAWT
}

object RandomPainter extends Painter {
  override private[scrimage] def paint: Paint = new Paint {
    override def createContext(cm: ColorModel,
                               deviceBounds: Rectangle,
                               userBounds: Rectangle2D,
                               xform: AffineTransform,
                               hints: RenderingHints): PaintContext = new PaintContext {
      override def getRaster(x: Int,
                             y: Int,
                             w: Int,
                             h: Int): Raster = {
        val raster = getColorModel.createCompatibleWritableRaster(w, h).asInstanceOf[IntegerComponentRaster]
        for (x <- x until x + w; y <- y until y + h) {
          val color = Color(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256)).toInt
          raster.getDataStorage()(x + x * y) = color
        }
        raster
      }
      override def getColorModel: ColorModel = ColorModel.getRGBdefault
      override def dispose(): Unit = ()
    }
    override def getTransparency: Int = Transparency.OPAQUE
  }
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
