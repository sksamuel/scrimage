package com.sksamuel.scrimage.canvas.drawables;

import com.sksamuel.scrimage.canvas.Drawable;
import com.sksamuel.scrimage.canvas.GraphicsContext;
import com.sksamuel.scrimage.graphics.RichGraphics2D;

import java.util.List;

public class Polyline implements Drawable {

    private final List<java.awt.Point> points;
    private final GraphicsContext context;

    public Polyline(List<java.awt.Point> points, GraphicsContext context) {
        this.points = points;
        this.context = context;
    }

    @Override
    public void draw(RichGraphics2D g) {
        int n = points.size();
        int[] xs = new int[n];
        int[] ys = new int[n];
        int i = 0;
        for (java.awt.Point p : points) {
            xs[i] = p.x;
            ys[i] = p.y;
            i++;
        }
        g.drawPolyline(xs, ys, n);
    }

    public Polygon close() {
        return new Polygon(points, context);
    }

    @Override
    public GraphicsContext context() {
        return context;
    }
}
