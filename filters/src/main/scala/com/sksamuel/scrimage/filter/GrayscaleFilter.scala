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

import com.sksamuel.scrimage.{ PixelTools, Image, Filter }

/** @author Stephen Samuel */
object GrayscaleFilter extends Filter {
  def apply(image: Image) {
    image._mapInPlace((x, y, p) => {
      val red = 0.21 * PixelTools.red(p)
      val green = 0.71 * PixelTools.green(p)
      val blue = 0.07 * PixelTools.blue(p)
      val gray = red + green + blue
      PixelTools.rgb(gray.toInt, gray.toInt, gray.toInt)
    })
  }
}