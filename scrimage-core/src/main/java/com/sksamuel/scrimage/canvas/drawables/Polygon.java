package com.sksamuel.scrimage.canvas.drawables;

import com.sksamuel.scrimage.canvas.Drawable;
import com.sksamuel.scrimage.canvas.GraphicsContext;
import com.sksamuel.scrimage.canvas.RichGraphics2D;

import java.util.Arrays;
import java.util.List;

public class Polygon implements Drawable {

   public static Polygon rectangle(int x, int y, int width, int height, GraphicsContext context) {
      return new Polygon(
         Arrays.asList(
            new java.awt.Point(x, y),
            new java.awt.Point(x + width, y),
            new java.awt.Point(x + width, y + height),
            new java.awt.Point(x, y + height)
         ),
         context
      );
   }

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
