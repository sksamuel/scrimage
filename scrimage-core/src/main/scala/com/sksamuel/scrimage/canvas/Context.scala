package com.sksamuel.scrimage.canvas

import java.awt.{RenderingHints, AlphaComposite, Graphics2D, Composite}

import com.sksamuel.scrimage.Color

case class Context(composite: Composite = AlphaComposite.getInstance(AlphaComposite.SRC),
                   color: Color = Color.White,
                   antiAlias: Boolean = true,
                   font: Option[Font] = None,
                   textSize: Int = 12,
                   painter: Option[Painter] = None) {

  def configure(g: Graphics2D): Unit = {
    g.setComposite(composite)
    g.setColor(color)
    font.map(_.name).map(new java.awt.Font(_, 0, textSize)).foreach(g.setFont)
    painter.map(_.paint).foreach(g.setPaint)
    if (antiAlias) {
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
      g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
    } else {
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF)
      g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF)
    }
  }
}

object Context {
  val Default = Context()
  def painter(painter: Painter): Context = Context(painter = Option(painter))
  def painter(painter: Color): Context = Context(painter = Option(painter))
  def composite(composite: Composite): Context = Context(composite = composite)
}