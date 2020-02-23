package com.sksamuel.scrimage.canvas.drawables;

import com.sksamuel.scrimage.canvas.Drawable;
import com.sksamuel.scrimage.canvas.GraphicsContext;
import com.sksamuel.scrimage.canvas.RichGraphics2D;

public class FilledOval implements Drawable {

    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final GraphicsContext context;

    public FilledOval(int x, int y, int width, int height, GraphicsContext context) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.context = context;
    }

    @Override
    public void draw(RichGraphics2D g) {
        g.fillOval(x, y, width, height);
    }

    public Oval toOutline() {
        return new Oval(x, y, width, height, context);
    }

    @Override
    public GraphicsContext context() {
        return context;
    }
}
