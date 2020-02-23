package com.sksamuel.scrimage.canvas;

import java.awt.Graphics2D;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.canvas.drawables.DrawableImage;

public interface Drawable {
    void draw(Graphics2D g);

    GraphicsContext context();

    public static DrawableImage create(ImmutableImage img, int x, int y) {
        return null;
    }
}