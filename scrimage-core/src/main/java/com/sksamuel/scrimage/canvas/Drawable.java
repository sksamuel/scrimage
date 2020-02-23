package com.sksamuel.scrimage.canvas;

public interface Drawable {

    void draw(RichGraphics2D g);

    GraphicsContext context();
}