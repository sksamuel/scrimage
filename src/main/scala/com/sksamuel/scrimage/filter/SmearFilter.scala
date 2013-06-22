/*
   Copyright 2013 Stephen K Samuel

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
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