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

class RaysFilter(opacity: Float, threshold: Float, strength: Float) extends BufferedOpFilter {
  val op = new thirdparty.jhlabs.image.RaysFilter()
  op.setOpacity(opacity)
  op.setThreshold(threshold)
  op.setStrength(strength)
}

object RaysFilter {
  def apply(opacity: Float = 1.0f, threshold: Float = 0, strength: Float = 0.5f) = new RaysFilter(opacity, threshold, strength)
}
