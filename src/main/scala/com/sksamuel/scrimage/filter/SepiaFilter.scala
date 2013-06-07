package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.MarvinFilter
import thirdparty.marvin.image.color.Sepia

/** @author Stephen Samuel */
object SepiaFilter extends MarvinFilter {
    val plugin = new Sepia(20)
}