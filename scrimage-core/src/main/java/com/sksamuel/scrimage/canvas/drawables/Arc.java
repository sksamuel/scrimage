package com.sksamuel.scrimage.canvas.drawables;

import com.sksamuel.scrimage.canvas.Drawable;
import com.sksamuel.scrimage.canvas.GraphicsContext;
import com.sksamuel.scrimage.graphics.RichGraphics2D;

public class Arc implements Drawable {

    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final int startAngle;
    private final int endAngle;
    private final GraphicsContext context;

    public Arc(int x, int y, int width, int height, int startAngle, int endAngle, GraphicsContext context) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.startAngle = startAngle;
        this.endAngle = endAngle;
        this.context = context;
    }

    @Override
    public void draw(RichGraphics2D g) {
        g.drawArc(x, y, width, height, startAngle, endAngle);
    }

    public FilledArc toFilled() {
        return new FilledArc(x, y, width, height, startAngle, endAngle, context);
    }

    @Override
    public GraphicsContext context() {
        return context;
    }
}
