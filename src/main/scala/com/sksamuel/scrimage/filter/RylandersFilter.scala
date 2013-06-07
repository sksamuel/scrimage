package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.MarvinFilter
import thirdparty.marvin.image.halftone.Rylanders

/** @author Stephen Samuel */
object RylandersFilter extends MarvinFilter {
    val plugin = new Rylanders()
}