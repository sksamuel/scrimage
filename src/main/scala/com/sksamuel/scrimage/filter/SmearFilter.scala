package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter
import com.sksamuel.scrimage.filter.SmearType.{Circles, Crosses, Lines, Squares}

/** @author Stephen Samuel */
sealed trait SmearType
object SmearType {
    case object Circles extends SmearType
    case object Crosses extends SmearType
    case object Lines extends SmearType
    case object Squares extends SmearType
}

class SmearFilter(smearType: SmearType, angle: Float, density: Float, scatter: Float, distance: Int, mix: Float) extends BufferedOpFilter {
    val op = new thirdparty.jhlabs.image.SmearFilter()
    op.setDensity(density)
    op.setAngle(angle)
    op.setScatter(scatter)
    op.setDistance(distance)
    op.setMix(mix)
    smearType match {
        case Circles => op.setShape(thirdparty.jhlabs.image.SmearFilter.CIRCLES)
        case Crosses => op.setShape(thirdparty.jhlabs.image.SmearFilter.CROSSES)
        case Lines => op.setShape(thirdparty.jhlabs.image.SmearFilter.LINES)
        case Squares => op.setShape(thirdparty.jhlabs.image.SmearFilter.SQUARES)
    }
}
object SmearFilter {
    def apply(smearType: SmearType, angle: Float = 0, density: Float = 0.3f, scatter: Float = 0, distance: Int = 3, mix: Float = 0.4f) =
        new SmearFilter(smearType, angle, density, scatter, distance, mix)
}