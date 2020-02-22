package com.sksamuel.scrimage.canvas.painters;

import java.awt.*;

public interface Painter {
    Paint paint();

    static Painter fromColor(Color color) {
        return new ColorPainter(color);
    }
}
