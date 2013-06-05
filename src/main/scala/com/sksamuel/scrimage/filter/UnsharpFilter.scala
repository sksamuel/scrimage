package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.BufferedOpFilter
import com.sksamuel.scrimage.filter.EdgeAction.{WrapEdges, ClampEdges, ZeroEdges}

/** @author Stephen Samuel */
class UnsharpFilter(amount: Float, threshold: Int, edgeAction: EdgeAction) extends BufferedOpFilter {
    val op = new com.jhlabs.image.UnsharpFilter()
    op.setAmount(amount)
    op.setThreshold(threshold)
    edgeAction match {
        case ZeroEdges => op.setEdgeAction(com.jhlabs.image.ConvolveFilter.ZERO_EDGES)
        case ClampEdges => op.setEdgeAction(com.jhlabs.image.ConvolveFilter.CLAMP_EDGES)
        case WrapEdges => op.setEdgeAction(com.jhlabs.image.ConvolveFilter.WRAP_EDGES)
    }
}
object UnsharpFilter {
    def apply(): UnsharpFilter = new UnsharpFilter(0.5f, 1, ZeroEdges)
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


