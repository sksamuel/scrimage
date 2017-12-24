package com.sksamuel.scrimage.filter;

public enum EdgeAction {
    // Treat pixels off the edge as zero
    ZeroEdges,
    // Clamp pixels off the edge to the nearest edge
    ClampEdges,
    // Wrap pixels off the edge to the opposite edge
    WrapEdges
}
