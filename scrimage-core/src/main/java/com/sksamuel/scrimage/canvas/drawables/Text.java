package com.sksamuel.scrimage.canvas.drawables;

import com.sksamuel.scrimage.canvas.Drawable;
import com.sksamuel.scrimage.canvas.GraphicsContext;
import com.sksamuel.scrimage.graphics.RichGraphics2D;

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
    public void draw(RichGraphics2D g2) {
        // Canvas.draw / drawInPlace already invoke `d.context().configure(g2)`
        // before calling d.draw(g2) — the same way it does for every other
        // Drawable. This class used to call configure() a second time here,
        // which was a no-op for overwriting setters (setColor/setFont/...) but
        // applied additive operations (translate/rotate/transform) twice.
        g2.drawString(text, x, y);
    }

    @Override
    public GraphicsContext context() {
        return context;
    }
}
