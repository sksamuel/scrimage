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
import com.sksamuel.scrimage.filter.PointillizeGridType._

/** @author Stephen Samuel */
class PointillizeFilter(angle: Float = 0.0f,
  scale: Int,
  edgeThickness: Float,
  edgeColor: Int = 0xff000000,
  fadeEdges: Boolean,
  fuzziness: Float,
  gridType: PointillizeGridType)
  extends BufferedOpFilter {
  val op = new thirdparty.jhlabs.image.PointillizeFilter
  op.setAngle(angle)
  op.setScale(scale)
  op.setEdgeThickness(edgeThickness)
  op.setEdgeColor(edgeColor)
  op.setFadeEdges(fadeEdges)
  op.setFuzziness(fuzziness)
  gridType match {
    case Random => op.setGridType(thirdparty.jhlabs.image.CellularFilter.RANDOM)
    case Square => op.setGridType(thirdparty.jhlabs.image.CellularFilter.SQUARE)
    case Hexagonal => op.setGridType(thirdparty.jhlabs.image.CellularFilter.HEXAGONAL)
    case Octangal => op.setGridType(thirdparty.jhlabs.image.CellularFilter.OCTAGONAL)
    case Triangular => op.setGridType(thirdparty.jhlabs.image.CellularFilter.TRIANGULAR)
  }
}

object PointillizeFilter {
  def apply(gridType: PointillizeGridType): PointillizeFilter = apply(angle = 0.0f, gridType = gridType)
  def apply(angle: Float = 0.0f,
    scale: Int = 6,
    edgeThickness: Float = 0.4f,
    edgeColor: Int = 0xff000000,
    fadeEdges: Boolean = false,
    fuzziness: Float = 0.1f,
    gridType: PointillizeGridType = Hexagonal): PointillizeFilter = new PointillizeFilter(angle, scale, edgeThickness, edgeColor, fadeEdges, edgeColor, gridType)
}

sealed trait PointillizeGridType
object PointillizeGridType {
  case object Random extends PointillizeGridType
  case object Square extends PointillizeGridType
  case object Hexagonal extends PointillizeGridType
  case object Octangal extends PointillizeGridType
  case object Triangular extends PointillizeGridType
}
