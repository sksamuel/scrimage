package com.sksamuel.scrimage.canvas.drawables;

import com.sksamuel.scrimage.canvas.Drawable;
import com.sksamuel.scrimage.canvas.GraphicsContext;
import com.sksamuel.scrimage.graphics.RichGraphics2D;

import java.util.List;

public class FilledPolygon implements Drawable {

    private final List<java.awt.Point> points;
    private final GraphicsContext context;

    public FilledPolygon(List<java.awt.Point> points, GraphicsContext context) {
        this.points = points;
        this.context = context;
    }

    @Override
    public void draw(RichGraphics2D g) {
        int[] xs = points.stream().mapToInt(p -> p.x).toArray();
        int[] ys = points.stream().mapToInt(p -> p.y).toArray();
        g.fillPolygon(xs, ys, points.size());
    }

    public Polygon toOutline() {
        return new Polygon(points, context);
    }

    @Override
    public GraphicsContext context() {
        return context;
    }
}
