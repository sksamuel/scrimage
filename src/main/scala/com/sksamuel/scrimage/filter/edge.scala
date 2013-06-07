package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.MarvinFilter
import thirdparty.marvin.image.edge.{Sobel, Roberts, Prewitt}

/** @author Stephen Samuel */
object SobelsFilter extends MarvinFilter {
    val plugin = new Sobel()
}

object PrewittFilter extends MarvinFilter {
    val plugin = new Prewitt()
}

object RobertsFilter extends MarvinFilter {
    val plugin = new Roberts()
}