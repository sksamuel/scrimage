package com.sksamuel.scrimage.canvas

import com.sksamuel.scrimage.{Color, X11Colorlist}

case class TextStyle(size: Int = 18,
                     font: Font = Font.as(java.awt.Font.SANS_SERIF),
                     alpha: Double = 0.1d,
                     antiAlias: Boolean = true,
                     color: Color = X11Colorlist.White) {
}
