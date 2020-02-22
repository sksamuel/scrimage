package com.sksamuel.scrimage.canvas.drawables;

import com.sksamuel.scrimage.canvas.Drawable;
import com.sksamuel.scrimage.canvas.GraphicsContext;

import java.awt.*;

public class Text implements Drawable {

    private final String text;
    private final int x;
    private final int y;
    private final GraphicsContext context;

    public Text(String text, int x, int y, GraphicsContext context) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.context = context;
    }

    @Override
    public void draw(Graphics2D g2) {
        context.configure(g2);
        g2.drawString(text, x, y);
    }

    @Override
    public GraphicsContext context() {
        return context;
    }
}