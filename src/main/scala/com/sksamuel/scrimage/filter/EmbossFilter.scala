package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.MarvinFilter
import thirdparty.marvin.image.color.Emboss

/** @author Stephen Samuel */
object EmbossFilter extends MarvinFilter {
    val plugin = new Emboss()
}