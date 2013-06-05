package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter

/** @author Stephen Samuel */
object EdgeFilter extends BufferedOpFilter {
    val op = new com.jhlabs.image.EdgeFilter()
}