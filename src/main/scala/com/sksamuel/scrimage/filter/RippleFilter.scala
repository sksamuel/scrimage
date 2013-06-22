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
