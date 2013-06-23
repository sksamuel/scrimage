package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter

/** @author Stephen Samuel */
object LensFlareFilter extends BufferedOpFilter {
    val op = new thirdparty.jhlabs.image.FlareFilter()
    op.setRadius(70f)
    op.setRayAmount(2.2f)
    op.setRingWidth(3f)
    op.setRingAmount(0.2f)
    op.setBaseAmount(1.1f)
}

