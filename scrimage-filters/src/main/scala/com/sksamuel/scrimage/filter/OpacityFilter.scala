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

import com.sksamuel.scrimage.{ ARGBIntPixel, Filter, Image, Pixel }

/** @author Stephen Samuel */
class OpacityFilter(amount: Double) extends Filter {

  def filter(x: Int, y: Int, pixel: Pixel): Pixel = {
    val _r = (pixel.red + (255 - pixel.red) * amount).toInt
    val _g = (pixel.green + (255 - pixel.green) * amount).toInt
    val _b = (pixel.blue + (255 - pixel.blue) * amount).toInt
    ARGBIntPixel(_r, _g, _b, pixel.alpha)
  }

  def apply(image: Image) {
    image.mapInPlace((x, y, p) => filter(x, y, p))
  }
}

object OpacityFilter {
  def apply(amount: Double): OpacityFilter = new OpacityFilter(amount)
}
