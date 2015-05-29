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
import java.awt.{Color, Graphics2D}

/** @author Stephen Samuel */
class BorderFilter(width: Int, color: Color = Color.BLACK) extends Filter {
  def apply(image: Image) {
    val g2 = image.awt.getGraphics.asInstanceOf[Graphics2D]
    g2.setColor(color)
    g2.fillRect(0, 0, width, image.height) // left
    g2.fillRect(image.width - width, 0, width, image.height) // right
    g2.fillRect(0, 0, image.width, width) // top
    g2.fillRect(0, image.height - width, image.width, width) // bottom
    //image.updateFromAWT()
  }
}
object BorderFilter {
  def apply(width: Int, color: Color = Color.BLACK): BorderFilter = new BorderFilter(width, color)
}
