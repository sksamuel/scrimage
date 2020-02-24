package com.sksamuel.scrimage.canvas;

import com.sksamuel.scrimage.graphics.RichGraphics2D;

public interface GraphicsContext {

    /**
     * An implementation of [GraphicsContext] that does not change the Graphics object.
     */
    static GraphicsContext identity() {
        return g2 -> {
        };
    }

    void configure(RichGraphics2D g2);
}

