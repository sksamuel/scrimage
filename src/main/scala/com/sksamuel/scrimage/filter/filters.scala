package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.{Image, Filter, BufferedOpFilter}
import com.sksamuel.scrimage.filter.RippleType.{Noise, Triangle, Sawtooth, Sine}
import com.sksamuel.scrimage.filter.SmearType.{Squares, Lines, Circles, Crosses}
import java.awt.image._
import java.awt.{RenderingHints, Toolkit}

/** @author Stephen Samuel */
object BlurFilter extends BufferedOpFilter {
    val op = new com.jhlabs.image.BlurFilter()
}

class QuantizeFilter(colors: Int, dither: Boolean) extends BufferedOpFilter {
    val op = new com.jhlabs.image.QuantizeFilter
    op.setNumColors(colors)
    op.setDither(dither)
}
object QuantizeFilter {
    def apply(): QuantizeFilter = new QuantizeFilter(256, false)
    def apply(colors: Int, dither: Boolean = false): QuantizeFilter = new QuantizeFilter(colors, dither)
}

class GaussianBlurFilter(radius: Int = 2) extends BufferedOpFilter {
    val op = new com.jhlabs.image.GaussianFilter(radius)
}
object GaussianBlurFilter {
    def apply() = new GaussianBlurFilter()
    def apply(radius: Int) = new GaussianBlurFilter(radius)
}

object GrayscaleFilter extends BufferedOpFilter {
    val op = new com.jhlabs.image.GrayscaleFilter()
}

class BrightenFilter(amount: Float) extends BufferedOpFilter {
    val op = new RescaleOp(amount, 0, new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY))
}

object BrightenFilter {
    def apply(amount: Double) = new BrightenFilter(amount.toFloat)
    def apply(amount: Float) = new BrightenFilter(amount)
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
object RippleFilter {
    def apply(rippleType: RippleType) = new RippleFilter(rippleType, 0.5f, 0.0f)
}

class RedFilter extends Filter {

    object _RedFilter extends RGBImageFilter {
        def filterRGB(x: Int, y: Int, rgb: Int) = rgb & 0xffff0000
    }

    def apply(image: Image): Image = {
        val filteredSrc = new FilteredImageSource(image.awt.getSource, _RedFilter)
        val awt = Toolkit.getDefaultToolkit.createImage(filteredSrc)
        Image(awt)
    }
}

object EdgeFilter extends BufferedOpFilter {
    val op = new com.jhlabs.image.EdgeFilter()
}

class LensBlurFilter(radius: Float, bloom: Float, bloomThreshold: Float, sides: Int, angle: Float)
  extends BufferedOpFilter {
    val op = new com.jhlabs.image.LensBlurFilter()
    op.setSides(sides)
    op.setBloomThreshold(bloomThreshold)
    op.setBloom(bloom)
    op.setRadius(radius)
}
object LensBlurFilter {
    def apply() = new LensBlurFilter(5, 2, 255, 5, 0)
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
object SmearFilter {
    def apply(smearType: SmearType) = new SmearFilter(smearType, 0, 0.5f, 0.0f)
}

object SolarizeFilter extends BufferedOpFilter {
    val op = new com.jhlabs.image.SolarizeFilter()
}

object InvertFilter extends BufferedOpFilter {
    val op = new com.jhlabs.image.InvertFilter()
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