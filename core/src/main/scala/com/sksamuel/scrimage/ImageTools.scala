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

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

/** @author Stephen Samuel */
object ImageTools {

  val BG_COLOR = java.awt.Color.WHITE
  val SCALING_METHOD = java.awt.Image.SCALE_AREA_AVERAGING

  // write out the image to bytes
  def toBytes(image: BufferedImage, format: String) = {
    require(format != null)
    val output = new ByteArrayOutputStream
    ImageIO.write(image, format, output)
    output.toByteArray
  }

  def dimensionsToCover(target: (Int, Int), source: (Int, Int)): (Int, Int) = {

    val xscale = target._1 / source._1.toDouble
    val yscale = target._2 / source._2.toDouble

    if (xscale > yscale) {
      ((source._1 * xscale).toInt, (source._2 * xscale).toInt)
    } else {
      ((source._1 * yscale).toInt, (source._2 * yscale).toInt)
    }
  }

  /**
   * Returns width and height that allow the given source width, height to fit inside the target width, height
   * without losing aspect ratio
   */
  def dimensionsToFit(target: (Int, Int), source: (Int, Int)): (Int, Int) = {

    // if target width/height is zero then we have no preference for that, so set it to the original value,
    // since it cannot be any larger
    val maxWidth = if (target._1 == 0) source._1 else target._1
    val maxHeight = if (target._2 == 0) source._2 else target._2

    val wscale = maxWidth / source._1.toDouble
    val hscale = maxHeight / source._2.toDouble

    if (wscale < hscale)
      ((source._1 * wscale).toInt, (source._2 * wscale).toInt)
    else
      ((source._1 * hscale).toInt, (source._2 * hscale).toInt)
  }
}
