package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.{Image, Filter}
import java.awt.Graphics2D
import thirdparty.romainguy.BlendComposite

/** @author Stephen Samuel */
object SnowFilter extends Filter {

    val snow = Image(getClass.getResourceAsStream("/com/sksamuel/scrimage/filter/snow1.jpg"))

    def apply(image: Image) {
        val g2 = image.awt.getGraphics.asInstanceOf[Graphics2D]
        g2.setComposite(BlendComposite.Screen)
        g2.drawImage(snow.scaleTo(image.width, image.height).awt, 0, 0, null)
        g2.dispose()
    }
}