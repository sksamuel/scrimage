package com.sksamuel.scrimage.canvas.drawables;

import com.sksamuel.scrimage.canvas.Drawable;
import com.sksamuel.scrimage.canvas.GraphicsContext;
import com.sksamuel.scrimage.canvas.RichGraphics2D;

public class Rect implements Drawable {

   private final int x;
   private final int y;
   private final int width;
   private final int height;
   private final GraphicsContext context;

   public Rect(int x, int y, int width, int height) {
      this(x, y, width, height, GraphicsContext.identity());
   }

   public Rect(int x, int y, int width, int height, GraphicsContext context) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.context = context;
   }


   @Override
   public void draw(RichGraphics2D g) {
      g.drawRect(x, y, width, height);
   }

   public FilledRect toFilled() {
      return new FilledRect(x, y, width, height, context);
   }

   @Override
   public GraphicsContext context() {
      return context;
   }
}
