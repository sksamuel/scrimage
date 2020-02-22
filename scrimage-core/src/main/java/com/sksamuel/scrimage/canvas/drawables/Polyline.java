package com.sksamuel.scrimage.canvas.drawables;

import com.sksamuel.scrimage.canvas.Drawable;
import com.sksamuel.scrimage.canvas.GraphicsContext;

import java.awt.Graphics2D;
import java.util.List;

public class Polyline implements Drawable {

    private final List<java.awt.Point> points;
    private final GraphicsContext context;

    public Polyline(List<java.awt.Point> points, GraphicsContext context) {
        this.points = points;
        this.context = context;
    }

    @Override
    public void draw(Graphics2D g) {
        int[] xs = points.stream().mapToInt(p -> p.x).toArray();
        int[] ys = points.stream().mapToInt(p -> p.y).toArray();
        g.drawPolyline(xs, ys, points.size());
    }

    public Polygon close() {
        return new Polygon(points, context);
    }

    @Override
    public GraphicsContext context() {
        return context;
    }
}