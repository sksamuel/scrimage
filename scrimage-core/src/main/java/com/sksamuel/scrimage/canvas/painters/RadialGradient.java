package com.sksamuel.scrimage.canvas.painters;

import java.awt.*;

public class RadialGradient implements Painter {

    private final float cx;
    private final float cy;
    private final float radius;
    private final float[] fractions;
    private final Color[] colors;

    public RadialGradient(float cx, float cy, float radius, float[] fractions, Color[] colors) {
        this.cx = cx;
        this.cy = cy;
        this.radius = radius;
        this.fractions = fractions;
        this.colors = colors;
    }

    @Override
    public Paint paint() {
        return new RadialGradientPaint(cx, cy, radius, fractions, colors);
    }
}
