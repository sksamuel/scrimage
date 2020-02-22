package com.sksamuel.scrimage.canvas.drawables;

import com.sksamuel.scrimage.canvas.Drawable;
import com.sksamuel.scrimage.canvas.GraphicsContext;

import java.awt.*;

public class Oval implements Drawable {

    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final GraphicsContext context;

    public Oval(int x, int y, int width, int height, GraphicsContext context) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.context = context;
    }

    @Override
    public void draw(Graphics2D g) {
        g.drawOval(x, y, width, height);
    }

    public FilledOval toFilled() {
        return new FilledOval(x, y, width, height, context);
    }

    @Override
    public GraphicsContext context() {
        return context;
    }
}
