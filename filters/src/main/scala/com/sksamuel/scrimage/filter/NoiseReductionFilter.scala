package com.sksamuel.scrimage.filter

import thirdparty.marvin.image.restoration.NoiseReduction

/** @author Stephen Samuel */
object NoiseReductionFilter extends MarvinFilter {
    val plugin = new NoiseReduction()
}