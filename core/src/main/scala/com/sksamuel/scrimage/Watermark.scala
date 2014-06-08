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

package com.sksamuel.scrimage

import java.awt._
import java.awt.geom.AffineTransform

/** @author Stephen Samuel */
class Watermark(text: String, fontSize: Int, alpha: Double) extends Filter {

  def apply(image: Image): Unit = {

    val g2 = image.awt.getGraphics.asInstanceOf[Graphics2D]
    g2.setColor(java.awt.Color.white)
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

    val alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha.toFloat)
    g2.setComposite(alphaComposite)

    val f1 = new Font("Arial", Font.BOLD, fontSize)
    g2.setFont(f1)

    val fontMetrics = g2.getFontMetrics
    val bounds = fontMetrics.getStringBounds(text, g2)

    g2.translate(image.width / 2.0, image.height / 2.0)

    val rotation = new AffineTransform()
    val opad = image.height / image.width.toDouble
    val angle = Math.toDegrees(Math.atan(opad))
    val idegrees = -1 * angle
    val theta = (2 * Math.PI * idegrees) / 360
    rotation.rotate(theta)
    g2.transform(rotation)

    val x1 = bounds.getWidth / 2.0f * -1
    val y1 = bounds.getHeight / 2.0f
    g2.translate(x1, y1)

    g2.drawString(text, 0.0f, 0.0f)
    g2.dispose()
  }
}
