package com.sksamuel.scrimage.canvas.drawables;

import com.sksamuel.scrimage.canvas.Drawable;
import com.sksamuel.scrimage.canvas.GraphicsContext;
import com.sksamuel.scrimage.canvas.RichGraphics2D;

import java.util.List;


//object Polygon {
//
//  implicit def awt2polygon(awt: java.awt.Polygon, context: GraphicsContext): Polygon = {
//    val points = awt.xpoints.zip(awt.ypoints).map { case (x, y) => new java.awt.Point(x, y) }.toIndexedSeq
//    Polygon(points, context)
//  }
//
//  def rectangle(x: Int, y: Int, width: Int, height: Int, context: GraphicsContext): Polygon = {
//    Polygon(
//      Seq(
//        new java.awt.Point(x, y),
//        new java.awt.Point(x + width, y),
//        new java.awt.Point(x + width, y + height),
//        new java.awt.Point(x, y + height)
//      ),
//      context
//    )
//  }
//}


public class Polygon implements Drawable {

    private final List<java.awt.Point> points;
    private final GraphicsContext context;

    public Polygon(List<java.awt.Point> points, GraphicsContext context) {
        this.points = points;
        this.context = context;
    }

    @Override
    public void draw(RichGraphics2D g) {
        int[] xs = points.stream().mapToInt(p -> p.x).toArray();
        int[] ys = points.stream().mapToInt(p -> p.y).toArray();
        g.drawPolygon(xs, ys, points.size());
    }

    public FilledPolygon toFilled() {
        return new FilledPolygon(points, context);
    }

    public Polyline toLine() {
        return new Polyline(points, context);
    }

    @Override
    public GraphicsContext context() {
        return context;
    }
}