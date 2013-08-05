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

/** @author Stephen Double */
class HSBFilter(hue: Double, saturation: Double, brightness: Double) extends BufferedOpFilter {
  require(hue <= 1)
  require(brightness <= 1)
  require(saturation <= 1)
  val op = new thirdparty.jhlabs.image.HSBAdjustFilter()
  op.setHFactor(hue.toFloat)
  op.setSFactor(saturation.toFloat)
  op.setBFactor(brightness.toFloat)
}
object HSBFilter {
  def apply(hue: Double = 0, saturation: Double = 0, brightness: Double = 0): HSBFilter = new
      HSBFilter(hue, saturation, brightness)
}