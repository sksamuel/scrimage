package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter

/** @author Stephen Samuel */
object MinimumFilter extends BufferedOpFilter {
    val op = new thirdparty.jhlabs.image.MinimumFilter()
}