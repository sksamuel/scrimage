package com.sksamuel.scrimage.canvas

import java.awt.font.FontRenderContext
import java.awt.{Font => JFont}

object FontBounds {
  def apply(text: String, font: JFont): (Int, Int) = {
    val bounds = font.getStringBounds(text, new FontRenderContext(font.getTransform, false, false)).getBounds
    (bounds.width, bounds.height)
  }
}
