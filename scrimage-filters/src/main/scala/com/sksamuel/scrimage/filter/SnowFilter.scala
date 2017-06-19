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

import java.awt.Graphics2D
import java.awt.image.BufferedImage

import com.sksamuel.scrimage.{Filter, Image}
import thirdparty.romainguy.{BlendComposite, BlendingMode}

import scala.concurrent.ExecutionContext

/** @author Stephen Samuel */
class SnowFilter(implicit executor: ExecutionContext) extends Filter {

  val snow = Image.fromResource("/com/sksamuel/scrimage/filter/snow1.jpg")

  def apply(image: Image) {
    val scaled = Image.wrapAwt(snow.scaleTo(image.width, image.height).awt, BufferedImage.TYPE_INT_ARGB)
    val g2 = image.awt.getGraphics.asInstanceOf[Graphics2D]
    g2.setComposite(new BlendComposite(BlendingMode.SCREEN, 1.0f))
    g2.drawImage(scaled.awt, 0, 0, null)
    g2.dispose()
  }
}

object SnowFilter {
  def apply()(implicit executor: ExecutionContext): SnowFilter = new SnowFilter()
}
