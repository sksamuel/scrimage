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

import java.awt.{Color, Graphics2D}

import com.sksamuel.scrimage.{Filter, Image}

class ColorizeFilter(color: Color) extends Filter {
  def apply(image: Image) {
    val g2 = image.awt.getGraphics.asInstanceOf[Graphics2D]
    g2.setColor(color)
    g2.fillRect(0, 0, image.width, image.height)
    g2.dispose()
    //image.updateFromAWT()
  }
}

object ColorizeFilter {
  def apply(color: Color): ColorizeFilter = new ColorizeFilter(color)
  def apply(r: Int, g: Int, b: Int, a: Int = 255): ColorizeFilter = new ColorizeFilter(new Color(r, g, b, a))
}
