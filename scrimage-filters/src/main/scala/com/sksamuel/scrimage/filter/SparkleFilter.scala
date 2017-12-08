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

class SparkleFilter(x: Int, y: Int, rays: Int, radius: Int, amount: Int) extends BufferedOpFilter {
  val op = new thirdparty.jhlabs.image.SparkleFilter()
  op.setRays(rays)
  op.setRadius(radius)
  op.setAmount(amount)
  op.setDimensions(x, y)
}

object SparkleFilter {
  def apply(x: Int = 0, y: Int = 0, rays: Int = 50, radius: Int = 25, amount: Int = 50) =
    new SparkleFilter(x, y, rays, radius, amount)
}