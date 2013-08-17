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

import com.sksamuel.scrimage.BufferedOpFilter

/** @author Stephen Samuel */
class TwirlFilter(angle: Double, radius: Int, centerX: Float, centerY: Float) extends BufferedOpFilter {
  val op = new thirdparty.jhlabs.image.TwirlFilter()
  op.setCentreX(centerX)
  op.setCentreY(centerY)
  op.setRadius(radius.toFloat)
  op.setAngle(angle.toFloat)
}
object TwirlFilter {
  def apply(radius: Int): TwirlFilter = apply(Math.PI / 1.5, radius)
  def apply(angle: Double, radius: Int, centerX: Float = 0.5f, centerY: Float = 0.5f): TwirlFilter =
    new TwirlFilter(angle, radius, centerX, centerY)
}
