package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.Filter
import java.awt.image.BufferedImageOp
import com.sksamuel.scrimage.filter.RippleType.{Noise, Triangle, Sawtooth, Sine}

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

sealed trait RippleType
object RippleType {
    case object Sine extends RippleType
    case object Sawtooth extends RippleType
    case object Triangle extends RippleType
    case object Noise extends RippleType
}

class RippleFilter(rippleType: RippleType, xAmplitude: Float = 5.0f, yAmplitude: Float = 0.0f) extends Filter {
    val op: BufferedImageOp = {
        val javaOp = new com.jhlabs.image.RippleFilter()
        javaOp.setXAmplitude(xAmplitude)
        javaOp.setYAmplitude(yAmplitude)
        rippleType match {
            case Sine => javaOp.setWaveType(com.jhlabs.image.RippleFilter.SINE)
            case Sawtooth => javaOp.setWaveType(com.jhlabs.image.RippleFilter.SAWTOOTH)
            case Triangle => javaOp.setWaveType(com.jhlabs.image.RippleFilter.TRIANGLE)
            case Noise => javaOp.setWaveType(com.jhlabs.image.RippleFilter.NOISE)
        }
    }

}

class LensBlurFilter extends Filter {

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