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

/** @author Stephen Samuel */
class CrystallizeFilter(scale: Double, edgeThickness: Double, edgeColor: Int, randomness: Double) extends BufferedOpFilter {
    val op = new thirdparty.jhlabs.image.CrystallizeFilter
    op.setEdgeColor(edgeColor)
    op.setEdgeThickness(edgeThickness.toFloat)
    op.setScale(scale.toFloat)
    op.setRandomness(randomness.toFloat)
}
object CrystallizeFilter {
    def apply(scale: Double = 16, edgeThickness: Double = 0.4, edgeColor: Int = 0xff000000, randomness: Double = 0.2): CrystallizeFilter =
        new CrystallizeFilter(scale, edgeThickness, edgeColor, randomness)
}
