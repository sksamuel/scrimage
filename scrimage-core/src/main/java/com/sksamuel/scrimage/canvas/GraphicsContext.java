package com.sksamuel.scrimage.canvas;

import java.awt.*;

public interface GraphicsContext {

    static GraphicsContext identity() {
        return g2 -> {};
    }

    void configure(Graphics2D g2);
}
