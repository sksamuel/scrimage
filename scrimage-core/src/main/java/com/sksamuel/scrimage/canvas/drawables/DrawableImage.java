package com.sksamuel.scrimage.canvas.drawables;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.canvas.Drawable;
import com.sksamuel.scrimage.canvas.GraphicsContext;
import com.sksamuel.scrimage.canvas.RichGraphics2D;

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

    public DrawableImage(ImmutableImage image, int x, int y) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.context = GraphicsContext.identity();
    }


    @Override
    public void draw(RichGraphics2D g) {
        g.drawImage(image.awt(), x, y, null);
    }

    @Override
    public GraphicsContext context() {
        return context;
    }
}
