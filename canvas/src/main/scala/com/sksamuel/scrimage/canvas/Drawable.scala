package com.sksamuel.scrimage.canvas

import java.awt.Graphics2D
import com.sksamuel.scrimage.Color

trait Drawable {
    def draw(g: Graphics2D) : Unit
    def withPainter(color: Color) = ColoredDrawable(color, this)
    def withPainter(painter: Painter) = ColoredDrawable(painter, this)
}

object Drawable {
    def apply(draw: Graphics2D => ()) = new Drawable{
        def draw(g: Graphics2D) = draw(g)
    }
}

class ColoredDrawable(painter: Painter, drawable: D) extends Drawable {
    def draw(g: Graphics2D) = {
        val previousPaint = g.getPaint
        g.setPaint(painter.paint)
        drawable.draw(g)
        g.setPaint(previousPaint)
    }
}


case class DRect(x: Int, y: Int, width: Int, height: Int) extends Drawable {
    def draw(g: Graphics2D) = g.drawRect(x, y, width, height)
    def fill = FillableDRect(x, y, width, height)
    def rounded(arcWidth: Int, arcHeight: Int) =
        RoundedDRect(x, y, width, height, arcWidth, arcHeight)
}

case class FillableDRect(x: Int, y: Int, width: Int, height: Int) extends Drawable {
    def draw(g: Graphics2D) = g.fillRect(x, y, width, height)
    def rounded(arcWidth: Int, arcHeight: Int) =
        FilledRoundedDRect(x, y, width, height, arcWidth, arcHeight)
}

case class RoundedDRect(x: Int, y: Int, width: Int, height: Int,
                               arcWidth: Int, arcHeight: Int) extends Drawable {
    def draw(g: Graphics2D) = g.drawRoundRect(x, y, width, height, arcWidth, arcHeight)
    def fill = FillableRoundedRect(x, y, width, height, arcWidth, arcHeight)
}

case class FilledRoundedDRect(x: Int, y: Int, width: Int, height: Int,
                               arcWidth: Int, arcHeight: Int) extends Drawable {
    def draw(g: Graphics2D) = g.fillRoundRect(x, y, width, height, arcWidth, arcHeight)
}

case class DLine(x0: Int, y0: Int, x1: Int, y1: Int) extends Drawable {
    def draw(g: Graphics2D) = g.drawLine(x0, y0, x1, y1)
}

case class DImage(x: Int, y: Int, imageToDraw: Image) extends Drawable {
    def draw(g: Graphics2D) = g.drawImage(imageToDraw.awt, x, y, null)
}
