package com.sksamuel.scrimage.canvas

import java.awt.GraphicsEnvironment
import java.awt.font.FontRenderContext
import java.awt.geom.Rectangle2D
import java.io.InputStream
import java.nio.file.{Files, Path}
import java.awt.{Font => JFont}

case class Font(wrapped: JFont, bold: Boolean = false, italic: Boolean = false) {
  def name: String = wrapped.getName
  def bounds(text: String, size: Int, g: FontRenderContext): Rectangle2D = {
    new JFont(name, wrapped.getStyle, size).getStringBounds(text, g)
  }
  def asBold: Font = copy(bold = true, wrapped = new JFont(name, wrapped.getStyle | java.awt.Font.BOLD, 12))
  def asItalic: Font = copy(italic = true, wrapped = new JFont(name, wrapped.getStyle | java.awt.Font.ITALIC, 12))
  def asPlain: Font = copy(italic = false, bold = false, wrapped = new JFont(name, 0, 12))
  def style: Int = {
    var style = JFont.PLAIN
    if (bold) style = style | JFont.BOLD
    if (italic) style = style | JFont.ITALIC
    style
  }
}

object Font {

  def as(name: String): Font = Font(new JFont(name, JFont.PLAIN, 12))

  def available: IndexedSeq[String] = {
    GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()
  }

  def createTrueType(in: InputStream): Font = {
    require(in != null, "Cannot read from null stream")
    val font = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, in)
    val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
    ge.registerFont(font)
    Font(new JFont(font.getName, JFont.PLAIN, 12))
  }

  def createTrueType(path: Path): Font = createTrueType(Files.newInputStream(path))
}

case class Padding(left: Int, right: Int, top: Int, bottom: Int)
object Padding {
  def apply(padding: Int): Padding = Padding(padding, padding, padding, padding)
}