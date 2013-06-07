package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.MarvinFilter
import thirdparty.marvin.image.television.Television

/** @author Stephen Samuel */
object TelevisionFilter extends MarvinFilter {
    val plugin = new Television()
}

