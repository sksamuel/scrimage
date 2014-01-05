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

import com.sksamuel.scrimage.{ Image, Filter }
import java.awt.Graphics2D
import thirdparty.romainguy.BlendComposite
import thirdparty.romainguy.BlendComposite.BlendingMode

/** @author Stephen Samuel */
object SnowFilter extends Filter {

  val snow = Image(getClass.getResourceAsStream("/com/sksamuel/scrimage/filter/snow1.jpg"))

  def apply(image: Image) {
    val g2 = image.awt.getGraphics.asInstanceOf[Graphics2D]
    g2.setComposite(new BlendComposite(BlendingMode.SCREEN, 1.0f))
    g2.drawImage(snow.scaleTo(image.width, image.height).awt, 0, 0, null)
    g2.dispose()
  }
}