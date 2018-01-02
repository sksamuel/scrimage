package com.sksamuel.scrimage.canvas;

import java.awt.*;

public interface GraphicsContext {

    static GraphicsContext identity() {
        return new GraphicsContext() {
            @Override
            public void configure(Graphics2D g2) {

            }
        };
    }

    void configure(Graphics2D g2);
}
