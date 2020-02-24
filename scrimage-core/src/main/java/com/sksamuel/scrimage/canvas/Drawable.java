package com.sksamuel.scrimage.canvas;

import com.sksamuel.scrimage.graphics.RichGraphics2D;

public interface Drawable {

    void draw(RichGraphics2D g);

    GraphicsContext context();
}
