package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.{Image, Filter}
import thirdparty.misc.ThistleFilter

/** @author Stephen Samuel */
object VintageFilter extends Filter {
    def apply(image: Image) {
        val thistle = new ThistleFilter()
        val filtered = thistle.filter(image.awt)
        image._draw(filtered)
    }
}