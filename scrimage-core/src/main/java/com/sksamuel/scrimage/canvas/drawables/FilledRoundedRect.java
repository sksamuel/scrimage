package com.sksamuel.scrimage.canvas.drawables;

import com.sksamuel.scrimage.canvas.Drawable;
import com.sksamuel.scrimage.canvas.GraphicsContext;

import java.awt.Graphics2D;


public class FilledRoundedRect implements Drawable {

    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final int arcWidth;
    private final int arcHeight;
    private final GraphicsContext context;

    public FilledRoundedRect(int x, int y, int width, int height, int arcWidth, int arcHeight, GraphicsContext context) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.arcWidth = arcWidth;
        this.arcHeight = arcHeight;
        this.context = context;
    }

    @Override
    public void draw(Graphics2D g) {
        g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    public RoundedRect toOutline() {
        return new RoundedRect(x, y, width, height, arcWidth, arcHeight, context);
    }

    public FilledRect toUnrounded() {
        return new FilledRect(x, y, width, height, context);
    }

    @Override
    public GraphicsContext context() {
        return context;
    }
}