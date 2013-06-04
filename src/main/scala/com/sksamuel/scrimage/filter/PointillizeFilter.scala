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
    val op = new com.jhlabs.image.PointillizeFilter
    op.setAngle(angle)
    op.setScale(scale)
    op.setEdgeThickness(edgeThickness)
    op.setEdgeColor(edgeColor)
    op.setFadeEdges(fadeEdges)
    op.setFuzziness(fuzziness)
    gridType match {
        case Random => op.setGridType(com.jhlabs.image.CellularFilter.RANDOM)
        case Square => op.setGridType(com.jhlabs.image.CellularFilter.SQUARE)
        case Hexagonal => op.setGridType(com.jhlabs.image.CellularFilter.HEXAGONAL)
        case Octangal => op.setGridType(com.jhlabs.image.CellularFilter.OCTAGONAL)
        case Triangular => op.setGridType(com.jhlabs.image.CellularFilter.TRIANGULAR)
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
              gridType: PointillizeGridType = Hexagonal): PointillizeFilter = new
        PointillizeFilter(angle, scale, edgeThickness, edgeColor, fadeEdges, edgeColor, gridType)
}

sealed trait PointillizeGridType
object PointillizeGridType {
    case object Random extends PointillizeGridType
    case object Square extends PointillizeGridType
    case object Hexagonal extends PointillizeGridType
    case object Octangal extends PointillizeGridType
    case object Triangular extends PointillizeGridType
}
