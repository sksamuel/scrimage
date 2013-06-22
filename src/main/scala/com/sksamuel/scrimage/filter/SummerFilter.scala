package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.{Image, Filter}
import java.awt.Graphics2D
import thirdparty.romainguy.BlendComposite

/** @author Stephen Samuel */
class SummerFilter(vignette: Boolean) extends Filter {

    val summer = Image(getClass.getResourceAsStream("/com/sksamuel/scrimage/filter/summer1.jpg"))

    def apply(image: Image) {
        val g2 = image.awt.getGraphics.asInstanceOf[Graphics2D]
        g2.setComposite(BlendComposite.getInstance(BlendComposite.BlendingMode.INVERSE_COLOR_BURN, 0.5f))
        g2.drawImage(summer.scaleTo(image.width, image.height).awt, 0, 0, null)
        g2.dispose()
        if (vignette)
            VignetteFilter(0.92f, 0.98f, 0.3).apply(image)
    }
}

object SummerFilter {
    def apply(vignette: Boolean = true) = new SummerFilter(true)
}