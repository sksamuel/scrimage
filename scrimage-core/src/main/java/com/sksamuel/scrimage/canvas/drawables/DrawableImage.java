package com.sksamuel.scrimage.canvas.drawables;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.canvas.Drawable;
import com.sksamuel.scrimage.canvas.GraphicsContext;

import java.awt.*;

public class DrawableImage implements Drawable {

    private final ImmutableImage image;
    private final int x;
    private final int y;
    private final GraphicsContext context;

    public DrawableImage(ImmutableImage image, int x, int y, GraphicsContext context) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.context = context;
    }

    @Override
    public void draw(Graphics2D g) {
        g.drawImage(image.awt(), x, y, null);
    }

    @Override
    public GraphicsContext context() {
        return context;
    }
}
