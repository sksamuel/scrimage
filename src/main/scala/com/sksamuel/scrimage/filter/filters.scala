package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.Filter
import java.awt.image.BufferedImageOp

/** @author Stephen Samuel */
object BlurFilter extends Filter {
    val op: BufferedImageOp = new com.jhlabs.image.BlurFilter()
}
class GaussianBlueFilter(radius: Int = 2) extends Filter {
    val op: BufferedImageOp = new com.jhlabs.image.GaussianFilter()
}
object GrayscaleFilter extends Filter {
    val op: BufferedImageOp = new com.jhlabs.image.GrayscaleFilter()
}
object RippleFilter extends Filter {
    val op: BufferedImageOp = new com.jhlabs.image.RippleFilter()
}
class SmearFilter extends Filter {
    val op: BufferedImageOp = new com.jhlabs.image.SmearFilter()
}
object BumpFilter extends Filter {
    val op: BufferedImageOp = new com.jhlabs.image.BumpFilter()
}
object DespeckleFilter extends Filter {
    val op: BufferedImageOp = new com.jhlabs.image.DespeckleFilter()
}