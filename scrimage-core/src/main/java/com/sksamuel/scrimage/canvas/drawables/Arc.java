package com.sksamuel.scrimage.canvas.drawables;

import com.sksamuel.scrimage.canvas.Drawable;
import com.sksamuel.scrimage.canvas.GraphicsContext;
import com.sksamuel.scrimage.graphics.RichGraphics2D;

public class Arc implements Drawable {

    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final int startAngle;
    private final int arcAngle;
    private final GraphicsContext context;

    /**
     * @param startAngle the beginning angle, in degrees, measured counter-clockwise from 3 o'clock
     * @param arcAngle   the angular extent of the arc, in degrees (NOT the absolute end angle).
     *                   The previous parameter name "endAngle" was misleading — Graphics2D.drawArc
     *                   takes a span, not an absolute end. So {@code Arc(..., 30, 90)} draws an arc
     *                   from 30° spanning 90° (i.e. ending at 120°), not from 30° to 90°.
     */
    public Arc(int x, int y, int width, int height, int startAngle, int arcAngle, GraphicsContext context) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.startAngle = startAngle;
        this.arcAngle = arcAngle;
        this.context = context;
    }

    @Override
    public void draw(RichGraphics2D g) {
        g.drawArc(x, y, width, height, startAngle, arcAngle);
    }

    public FilledArc toFilled() {
        return new FilledArc(x, y, width, height, startAngle, arcAngle, context);
    }

    @Override
    public GraphicsContext context() {
        return context;
    }
}
