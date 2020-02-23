package com.sksamuel.scrimage.canvas.drawables;

import com.sksamuel.scrimage.canvas.Drawable;
import com.sksamuel.scrimage.canvas.GraphicsContext;
import com.sksamuel.scrimage.canvas.RichGraphics2D;

public class RoundedRect implements Drawable {

    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final int arcWidth;
    private final int arcHeight;
    private final GraphicsContext context;

    public RoundedRect(int x, int y, int width, int height, int arcWidth, int arcHeight, GraphicsContext context) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.arcWidth = arcWidth;
        this.arcHeight = arcHeight;
        this.context = context;
    }

    @Override
    public void draw(RichGraphics2D g) {
        g.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    public FilledRoundedRect toFilled() {
        return new FilledRoundedRect(x, y, width, height, arcWidth, arcHeight, context);
    }

    public Rect toUnrounded() {
        return new Rect(x, y, width, height, context);
    }

    @Override
    public GraphicsContext context() {
        return context;
    }
}