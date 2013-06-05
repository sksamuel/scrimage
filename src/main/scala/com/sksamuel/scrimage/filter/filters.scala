package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.{Image, Filter, BufferedOpFilter}
import java.awt.image._
import java.awt.{RenderingHints, Toolkit}

/** @author Stephen Samuel */
class BrightenFilter(amount: Float) extends BufferedOpFilter {
    val op = new RescaleOp(amount, 0, new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY))
}

object BrightenFilter {
    def apply(amount: Double) = new BrightenFilter(amount.toFloat)
    def apply(amount: Float) = new BrightenFilter(amount)
}

class RedFilter extends Filter {

    object _RedFilter extends RGBImageFilter {
        def filterRGB(x: Int, y: Int, rgb: Int) = rgb & 0xffff0000
    }

    def apply(image: Image): Image = {
        val filteredSrc = new FilteredImageSource(image.awt.getSource, _RedFilter)
        val awt = Toolkit.getDefaultToolkit.createImage(filteredSrc)
        Image(awt)
    }
}













