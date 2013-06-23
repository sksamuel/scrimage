package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.{PixelTools, Image, Filter}

/** @author Stephen Samuel */
object GrayscaleFilter extends Filter {
    def apply(image: Image) {
        image._mapInPlace((x, y, p) => {
            val red = 0.21 * PixelTools.red(p)
            val green = 0.71 * PixelTools.green(p)
            val blue = 0.07 * PixelTools.blue(p)
            val gray = red + green + blue
            PixelTools.rgb(gray.toInt, gray.toInt, gray.toInt)
        })
    }
}