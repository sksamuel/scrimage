package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.{Image, Filter}
import java.awt.{Color, Graphics2D}

/** @author Stephen Samuel */
class ColorizeFilter(a: Int, r: Int, g: Int, b: Int) extends Filter {
    require(a < 256 && a >= 0)
    require(r < 256 && r >= 0)
    require(g < 256 && g >= 0)
    require(b < 256 && b >= 0)

    def apply(image: Image) {
        val g2 = image.awt.getGraphics.asInstanceOf[Graphics2D]
        g2.setColor(new Color(r, g, b, a))
        g2.fillRect(0, 0, image.width, image.height)
        g2.dispose()
    }
}

object ColorizeFilter {
    def apply(a: Int, r: Int, g: Int, b: Int) = new ColorizeFilter(a, r, g, b)
}
