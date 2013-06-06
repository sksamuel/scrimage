package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter
import com.sksamuel.scrimage.filter.RippleType.{Noise, Triangle, Sawtooth, Sine}

/** @author Stephen Samuel */
sealed trait RippleType
object RippleType {
    case object Sine extends RippleType
    case object Sawtooth extends RippleType
    case object Triangle extends RippleType
    case object Noise extends RippleType
}

class RippleFilter(rippleType: RippleType, xAmplitude: Float, yAmplitude: Float, xWavelength: Float, yWavelength: Float)
  extends BufferedOpFilter {
    val op = new thirdparty.jhlabs.image.RippleFilter()
    op.setXAmplitude(xAmplitude)
    op.setYAmplitude(yAmplitude)
    op.setXWavelength(xWavelength)
    op.setYWavelength(yWavelength)
    rippleType match {
        case Sine => op.setWaveType(thirdparty.jhlabs.image.RippleFilter.SINE)
        case Sawtooth => op.setWaveType(thirdparty.jhlabs.image.RippleFilter.SAWTOOTH)
        case Triangle => op.setWaveType(thirdparty.jhlabs.image.RippleFilter.TRIANGLE)
        case Noise => op.setWaveType(thirdparty.jhlabs.image.RippleFilter.NOISE)
    }
}
object RippleFilter {
    def apply(rippleType: RippleType) = new RippleFilter(rippleType, 2f, 2f, 6f, 6f)
}
