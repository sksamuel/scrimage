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

import java.awt.Graphics2D
import java.awt.image.{AffineTransformOp, BufferedImage}
import java.io.{File, InputStream}
import java.awt.geom.AffineTransform

/** @author Stephen Samuel */
class MutableImage(raster: Raster) extends Image(raster) {

  override def clear(color: Color = X11Colorlist.White): Image = {
    val g2 = awt.getGraphics
    g2.setColor(color)
    g2.fillRect(0, 0, width, height)
    g2.dispose()
    this
  }

  protected[scrimage] def flip(tx: AffineTransform): MutableImage = {
    val op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR)
    val output = op.createCompatibleDestImage(awt, null)
    op.filter(awt, output)
    awt.setData(output.getData)
    this
  }

  override def filter(filter: Filter): MutableImage = {
    filter.apply(this)
    this
  }

  override def filled(color: Color): MutableImage = {
    val g2 = awt.getGraphics.asInstanceOf[Graphics2D]
    g2.setColor(color)
    g2.fillRect(0, 0, awt.getWidth, awt.getHeight)
    g2.dispose()
    this
  }

  def setPixel(x: Int, y: Int, pixel: Int) {
    awt.setRGB(x, y, pixel)
  }
}

object MutableImage {
  def apply(in: InputStream): MutableImage = Image(in).toMutable
  def apply(file: File): MutableImage = Image(file).toMutable
  def apply(image: Image): MutableImage = image.toMutable
  def apply(buff: BufferedImage): MutableImage = Image(buff).toMutable
}
