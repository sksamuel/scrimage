package com.sksamuel.scrimage.canvas

import com.sksamuel.scrimage.Color
import java.awt._

@deprecated("use function to configure g2", "3.0.0")
case class Context(composite: Composite = AlphaComposite.getInstance(AlphaComposite.SRC),
                   color: Color = Color.Black,
                   antiAlias: Boolean = false,
                   font: Option[Font] = None,
                   textSize: Int = 12,
                   painter: Option[Painter] = None) {

  @deprecated("use function to configure g2", "3.0.0")
  def withAntiAlias(b: Boolean): Context = copy(antiAlias = b)

  @deprecated("use function to configure g2", "3.0.0")
  def withFont(font: Font): Context = copy(font = Some(font))

  @deprecated("use function to configure g2", "3.0.0")
  def configure(g: Graphics2D): Unit = {
    g.setComposite(composite)
    g.setColor(color.toAWT)
    font.map(_.getName).map(new java.awt.Font(_, 0, textSize)).foreach(g.setFont)
    painter.map(_.paint).foreach(g.setPaint)
    if (antiAlias) {
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
      g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
    } else {
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF)
      g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF)
    }
  }

  @deprecated("use function to configure g2", "3.0.0")
  implicit def toFn: Graphics2D => Unit = { g2 => configure(g2) }
}

@deprecated("use function to configure g2", "3.0.0")
object Context {
  @deprecated("use function to configure g2", "3.0.0")
  val Default = Context()
  @deprecated("use function to configure g2", "3.0.0")
  def painter(painter: Painter): Context = Context(painter = Option(painter))
  @deprecated("use function to configure g2", "3.0.0")
  def painter(painter: Color): Context = Context(painter = Option(painter))
  @deprecated("use function to configure g2", "3.0.0")
  def composite(composite: Composite): Context = Context(composite = composite)
  @deprecated("use function to configure g2", "3.0.0")
  val Aliased = Default.withAntiAlias(true)
}