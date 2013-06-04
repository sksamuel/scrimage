package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.Filter
import com.sksamuel.scrimage.filter.RippleType.{Noise, Triangle, Sawtooth, Sine}
import com.sksamuel.scrimage.filter.SmearType.{Squares, Lines, Circles, Crosses}

/** @author Stephen Samuel */
object BlurFilter extends Filter {
    val op = new com.jhlabs.image.BlurFilter()
}

class GaussianBlueFilter(radius: Int = 2) extends Filter {
    val op = new com.jhlabs.image.GaussianFilter()
}

object GrayscaleFilter extends Filter {
    val op = new com.jhlabs.image.GrayscaleFilter()
}

sealed trait RippleType
object RippleType {
    case object Sine extends RippleType
    case object Sawtooth extends RippleType
    case object Triangle extends RippleType
    case object Noise extends RippleType
}

class RippleFilter(rippleType: RippleType, xAmplitude: Float = 5.0f, yAmplitude: Float = 0.0f) extends Filter {
    val op = new com.jhlabs.image.RippleFilter()
    op.setXAmplitude(xAmplitude)
    op.setYAmplitude(yAmplitude)
    rippleType match {
        case Sine => op.setWaveType(com.jhlabs.image.RippleFilter.SINE)
        case Sawtooth => op.setWaveType(com.jhlabs.image.RippleFilter.SAWTOOTH)
        case Triangle => op.setWaveType(com.jhlabs.image.RippleFilter.TRIANGLE)
        case Noise => op.setWaveType(com.jhlabs.image.RippleFilter.NOISE)
    }
}

class LensBlurFilter(radius: Float, bloom: Float = 2, bloomThreshold: Float = 255, sides: Int = 5, angle: Float = 0) extends Filter {
    val op = new com.jhlabs.image.LensBlurFilter()
    op.setSides(sides)
    op.setBloomThreshold(bloomThreshold)
    op.setBloom(bloom)
    op.setRadius(radius)
}

sealed trait SmearType
object SmearType {
    case object Circles extends RippleType
    case object Crosses extends RippleType
    case object Lines extends RippleType
    case object Squares extends RippleType
}

class SmearFilter(smearType: SmearType, angle: Float = 0, density: Float = 0.5f, scatter: Float = 0.0f) extends Filter {
    val op = new com.jhlabs.image.SmearFilter()
    op.setDensity(density)
    op.setAngle(angle)
    op.setScatter(scatter)
    smearType match {
        case Circles => op.setShape(com.jhlabs.image.SmearFilter.CIRCLES)
        case Crosses => op.setShape(com.jhlabs.image.SmearFilter.CROSSES)
        case Lines => op.setShape(com.jhlabs.image.SmearFilter.LINES)
        case Squares => op.setShape(com.jhlabs.image.SmearFilter.SQUARES)
    }
}

object BumpFilter extends Filter {
    val op = new com.jhlabs.image.BumpFilter()
}

object DespeckleFilter extends Filter {
    val op = new com.jhlabs.image.DespeckleFilter()
}