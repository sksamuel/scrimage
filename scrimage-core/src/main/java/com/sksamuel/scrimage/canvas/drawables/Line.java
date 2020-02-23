package com.sksamuel.scrimage.canvas.drawables;

import com.sksamuel.scrimage.canvas.Drawable;
import com.sksamuel.scrimage.canvas.GraphicsContext;
import com.sksamuel.scrimage.canvas.RichGraphics2D;

public class Line implements Drawable {

   private final int x0;
   private final int y0;
   private final int x1;
   private final int y1;
   private final GraphicsContext context;

   public Line(int x0, int y0, int x1, int y1) {
      this(x0, y0, x1, y1, GraphicsContext.identity());
   }

   public Line(int x0, int y0, int x1, int y1, GraphicsContext context) {
      this.x0 = x0;
      this.y0 = y0;
      this.x1 = x1;
      this.y1 = y1;
      this.context = context;
   }

   @Override
   public void draw(RichGraphics2D g) {
      g.drawLine(x0, y0, x1, y1);
   }

   @Override
   public GraphicsContext context() {
      return context;
   }
}
