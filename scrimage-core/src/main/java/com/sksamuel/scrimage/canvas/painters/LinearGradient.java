package com.sksamuel.scrimage.canvas.painters;

import java.awt.*;

public class LinearGradient implements Painter {

    public static LinearGradient horizontal(Color color1, Color color2) {
        return new LinearGradient(0, 0, color1, 10, 0, color2);
    }

    public static LinearGradient vertical(Color color1, Color color2) {
        return new LinearGradient(0, 10, color1, 0, 0, color2);
    }

    private final int x1;
    private final int y1;
    private final Color color1;
    private final int x2;
    private final int y2;
    private final Color color2;

    public LinearGradient(int x1, int y1, Color color1, int x2, int y2, Color color2) {
        this.x1 = x1;
        this.y1 = y1;
        this.color1 = color1;
        this.x2 = x2;
        this.y2 = y2;
        this.color2 = color2;
    }

    @Override
    public Paint paint() {
        return new GradientPaint(x1, y1, color1, x2, y2, color2);
    }
}
