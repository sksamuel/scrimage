package com.sksamuel.scrimage.canvas;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.graphics.RichGraphics2D;

import java.awt.*;
import java.util.Arrays;
import java.util.Collection;

/**
 * A Canvas extends an ImmutableImage with functionality for drawing.
 */
public class Canvas {

    private final ImmutableImage image;

    public Canvas(ImmutableImage image) {
        this.image = image;
    }

    public ImmutableImage getImage() {
       return image;
    }

    private Graphics2D g2(ImmutableImage image) {
        return (Graphics2D) image.awt().getGraphics();
    }

    public void drawInPlace(Drawable... drawables) {
        drawInPlace(Arrays.asList(drawables));
    }

    public void drawInPlace(Collection<Drawable> drawables) {
        Graphics2D g = g2(image);
        drawables.forEach(d -> {
            RichGraphics2D rich2d = new RichGraphics2D(g);
            d.context().configure(rich2d);
            d.draw(rich2d);
        });
        g.dispose();
    }

    public Canvas draw(Drawable... drawables) {
        return draw(Arrays.asList(drawables));
    }

    public Canvas draw(Collection<Drawable> drawables) {
        ImmutableImage target = image.copy();
        Graphics2D g = g2(target);
        drawables.forEach(d -> {
            RichGraphics2D rich2d = new RichGraphics2D(g);
            d.context().configure(rich2d);
            d.draw(rich2d);
        });
        g.dispose();
        return new Canvas(target);
    }
}
