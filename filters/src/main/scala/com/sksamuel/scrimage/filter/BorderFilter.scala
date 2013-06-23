package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.{Image, Filter}
import java.awt.{Color, Graphics2D}

/** @author Stephen Samuel */
class BorderFilter(width: Int, color: Color = Color.BLACK) extends Filter {
    def apply(image: Image) {
        val g2 = image.awt.getGraphics.asInstanceOf[Graphics2D]
        g2.setColor(color)
        g2.fillRect(0, 0, width, image.height) // left
        g2.fillRect(image.width - width, 0, width, image.height) // right
        g2.fillRect(0, 0, image.width, width) // top
        g2.fillRect(0, image.height - width, image.width, width) // bottom
    }
}
object BorderFilter {
    def apply(width: Int, color: Color = Color.BLACK) = new BorderFilter(width, color)
}