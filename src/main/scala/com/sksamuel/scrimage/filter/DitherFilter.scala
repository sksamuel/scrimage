package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.MarvinFilter
import thirdparty.marvin.image.halftone.Dithering

/** @author Stephen Samuel */
object DitherFilter extends MarvinFilter {
    val plugin = new Dithering()
}