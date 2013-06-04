package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter
import com.sksamuel.scrimage.filter.RippleType.{Noise, Triangle, Sawtooth, Sine}
import com.sksamuel.scrimage.filter.SmearType.{Squares, Lines, Circles, Crosses}
import java.awt.image.{BufferedImage, RescaleOp}

/** @author Stephen Samuel */
object BlurFilter extends BufferedOpFilter {
    val op = new com.jhlabs.image.BlurFilter()
}

class GaussianBlueFilter(radius: Int = 2) extends BufferedOpFilter {
    val op = new com.jhlabs.image.GaussianFilter()
}

object GrayscaleFilter extends BufferedOpFilter {
    val op = new com.jhlabs.image.GrayscaleFilter()
}

class BrightenFilter(amount: Float) extends BufferedOpFilter {
    val src = new BufferedImage(200, 200, BufferedImage.TYPE_BYTE_INDEXED)
    val op = new RescaleOp(amount, 0, null)
    val result = op.filter(src, null)
}

sealed trait RippleType
object RippleType {
    case object Sine extends RippleType
    case object Sawtooth extends RippleType
    case object Triangle extends RippleType
    case object Noise extends RippleType
}

class RippleFilter(rippleType: RippleType, xAmplitude: Float = 5.0f, yAmplitude: Float = 0.0f) extends BufferedOpFilter {
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

object EdgeFilter extends BufferedOpFilter {
    val op = new com.jhlabs.image.EdgeFilter()
}

class LensBlurFilter(radius: Float, bloom: Float = 2, bloomThreshold: Float = 255, sides: Int = 5, angle: Float = 0)
  extends BufferedOpFilter {
    val op = new com.jhlabs.image.LensBlurFilter()
    op.setSides(sides)
    op.setBloomThreshold(bloomThreshold)
    op.setBloom(bloom)
    op.setRadius(radius)
}

sealed trait SmearType
object SmearType {
    case object Circles extends SmearType
    case object Crosses extends SmearType
    case object Lines extends SmearType
    case object Squares extends SmearType
}

class SmearFilter(smearType: SmearType, angle: Float = 0, density: Float = 0.5f, scatter: Float = 0.0f) extends BufferedOpFilter {
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

object BumpFilter extends BufferedOpFilter {
    val op = new com.jhlabs.image.BumpFilter()
}

object DespeckleFilter extends BufferedOpFilter {
    val op = new com.jhlabs.image.DespeckleFilter()
}

class GlowFilter(amount: Float = 0.5f) extends BufferedOpFilter {
    val op = new com.jhlabs.image.GlowFilter()
    op.setAmount(amount)
}