package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter
import java.awt.Color

/** @author Stephen Samuel */
class TritoneFilter(shadows: Int, midtones: Int, highlights: Int) extends BufferedOpFilter {
    val op = new thirdparty.jhlabs.image.TritoneFilter()
    op.setShadowColor(shadows)
    op.setMidColor(midtones)
    op.setHighColor(highlights)
}

object TritoneFilter {
    def apply(shadows: Color, midtones: Color, highlights: Color): TritoneFilter = apply(shadows.getRGB, midtones.getRGB, highlights.getRGB)
    def apply(shadows: Int, midtones: Int, highlights: Int): TritoneFilter = new TritoneFilter(shadows, midtones, highlights)
}