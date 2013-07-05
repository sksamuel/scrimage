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

import com.sksamuel.scrimage.{Image, Filter}

/** @author Stephen Samuel */
class OpacityFilter(amount: Double) extends Filter {

    def filter(x: Int, y: Int, argb: Int): Int = {
        val a = argb & 0xff000000
        val r = argb >> 16 & 0xff
        val g = argb >> 8 & 0xff
        val b = argb & 0xff
        val _r = (r + (255 - r) * amount).toInt
        val _g = (g + (255 - g) * amount).toInt
        val _b = (b + (255 - b) * amount).toInt
        a | _r << 16 | _g << 8 | _b
    }

    def apply(image: Image) {
        image._mapInPlace((x, y, p) => filter(x, y, p))
    }
}

object OpacityFilter {
    def apply(amount: Double) = new OpacityFilter(amount)
}
