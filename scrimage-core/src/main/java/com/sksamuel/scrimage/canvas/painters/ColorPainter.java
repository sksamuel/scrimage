package com.sksamuel.scrimage.canvas.painters;

import java.awt.*;

public class ColorPainter implements Painter {

    private final Color color;

    public ColorPainter(Color color) {
        this.color = color;
    }

    @Override
    public Paint paint() {
        return color;
    }
}
