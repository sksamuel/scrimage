package com.sksamuel.scrimage.canvas.drawables;

import com.sksamuel.scrimage.canvas.Drawable;
import com.sksamuel.scrimage.canvas.GraphicsContext;

import java.awt.*;

public class FilledArc implements Drawable {

    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final int startAngle;
    private final int endAngle;
    private final GraphicsContext context;

    public FilledArc(int x, int y, int width, int height, int startAngle, int endAngle, GraphicsContext context) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.startAngle = startAngle;
        this.endAngle = endAngle;
        this.context = context;
    }

    @Override
    public void draw(Graphics2D g) {
        g.fillArc(x, y, width, height, startAngle, endAngle);
    }

    public Arc toOutline() {
        return new Arc(x, y, width, height, startAngle, endAngle, context);
    }

    @Override
    public GraphicsContext context() {
        return context;
    }
}
