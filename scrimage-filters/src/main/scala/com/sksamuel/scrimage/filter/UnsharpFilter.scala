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
import com.sksamuel.scrimage.filter.EdgeAction.{ WrapEdges, ClampEdges, ZeroEdges }

/** @author Stephen Samuel */
class UnsharpFilter(amount: Float, threshold: Int, edgeAction: EdgeAction) extends BufferedOpFilter {
  val op = new thirdparty.jhlabs.image.UnsharpFilter()
  op.setAmount(amount)
  op.setThreshold(threshold)
  edgeAction match {
    case ZeroEdges => op.setEdgeAction(thirdparty.jhlabs.image.ConvolveFilter.ZERO_EDGES)
    case ClampEdges => op.setEdgeAction(thirdparty.jhlabs.image.ConvolveFilter.CLAMP_EDGES)
    case WrapEdges => op.setEdgeAction(thirdparty.jhlabs.image.ConvolveFilter.WRAP_EDGES)
  }
}
object UnsharpFilter {
  def apply(): UnsharpFilter = apply(0.5f, 1, ZeroEdges)
  def apply(amount: Float, threshold: Int, edgeAction: EdgeAction = ZeroEdges): UnsharpFilter =
    new UnsharpFilter(amount, threshold, edgeAction)
}

sealed trait EdgeAction
object EdgeAction {
  // Treat pixels off the edge as zero
  case object ZeroEdges extends EdgeAction
  // Clamp pixels off the edge to the nearest edge
  case object ClampEdges extends EdgeAction
  // Wrap pixels off the edge to the opposite edge
  case object WrapEdges extends EdgeAction
}

