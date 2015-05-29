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
import java.awt.{ RenderingHints, RadialGradientPaint, Color, Graphics2D }
import java.awt.geom.Point2D
import thirdparty.romainguy.BlendComposite
import thirdparty.romainguy.BlendComposite.BlendingMode

/** @author Stephen Samuel */
class VignetteFilter(start: Double, end: Double, blur: Double, color: Color = Color.BLACK) extends Filter {
  require(start >= 0)
  require(start <= 1)
  require(blur >= 0)
  require(blur <= 1)

  private def background(image: Image): Image = {
    val target = image.empty
    val g2 = target.awt.getGraphics.asInstanceOf[Graphics2D]
    val radius = image.radius * end
    val p = new RadialGradientPaint(new Point2D.Float(target.center._1, target.center._2),
      radius.toInt,
      Array(0.0f, if (start == 0) 0.01f else if (start == 1) 0.999f else start.toFloat, 1f),
      Array(Color.WHITE, Color.WHITE, color))
    g2.setPaint(p)
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE)
    g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY)
    g2.fillRect(0, 0, target.width, target.height)
    g2.dispose()
    target
  }

  def apply(image: Image) {
    val bg = background(image)
    bg.filter(GaussianBlurFilter((image.radius * blur).toInt))

    val g3 = image.awt.getGraphics.asInstanceOf[Graphics2D]
    g3.setComposite(new BlendComposite(BlendingMode.MULTIPLY, 1.0f))
    g3.drawImage(bg.awt, 0, 0, null)
    g3.dispose()
  }
}

object VignetteFilter {
  def apply(): VignetteFilter = apply(0.85f, 0.95f, 0.3)
  def apply(start: Double, end: Double, blur: Double, color: Color = Color.BLACK): VignetteFilter = {
    new VignetteFilter(start, end, blur, color)
  }
}
