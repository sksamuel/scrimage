package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.MarvinFilter
import thirdparty.marvin.image.halftone.ErrorDiffusion

/** @author Stephen Samuel */
class ErrorDiffusionHalftoneFilter(threshold: Int) extends MarvinFilter {
    val plugin = new ErrorDiffusion(threshold: Int)
}
object ErrorDiffusionHalftoneFilter {
    def apply(): ErrorDiffusionHalftoneFilter = apply(127)
    def apply(threshold: Int = 0): ErrorDiffusionHalftoneFilter = new ErrorDiffusionHalftoneFilter(threshold)
}