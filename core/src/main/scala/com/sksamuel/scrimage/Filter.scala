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

import java.awt.image.BufferedImageOp
import java.awt.Graphics2D

/** @author Stephen Samuel */
trait Filter {
  def apply(image: Image)
}

/**
 * Extension of Filter that applies its filters using a standard java BufferedImageOp.
 *
 * Filters that wish to provide an awt BufferedImageOp need to simply extend this class.
 */
abstract class BufferedOpFilter extends Filter {
  val op: BufferedImageOp
  def apply(image: Image) {
    val g2 = image.awt.getGraphics.asInstanceOf[Graphics2D]
    g2.drawImage(image.awt, op, 0, 0)
    g2.dispose()
  }
}

class PipelineFilter(filters: Filter*) extends Filter {
  def apply(image: Image): Unit = filters.foreach(_.apply(image))
}