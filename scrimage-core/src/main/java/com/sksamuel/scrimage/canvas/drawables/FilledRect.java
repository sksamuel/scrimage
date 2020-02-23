package com.sksamuel.scrimage.canvas.drawables;

import com.sksamuel.scrimage.canvas.Drawable;
import com.sksamuel.scrimage.canvas.GraphicsContext;
import com.sksamuel.scrimage.canvas.RichGraphics2D;

public class FilledRect implements Drawable {

   private final int x;
   private final int y;
   private final int width;
   private final int height;
   private final GraphicsContext context;

   public FilledRect(int x, int y, int width, int height) {
      this(x, y, width, height, GraphicsContext.identity());
   }

   public FilledRect(int x, int y, int width, int height, GraphicsContext context) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.context = context;
   }


   @Override
   public void draw(RichGraphics2D g) {
      g.fillRect(x, y, width, height);
   }

   public Rect toOutline() {
      return new Rect(x, y, width, height, context);
   }

   public FilledRoundedRect toRounded(int arcWidth, int arcHeight) {
      return new FilledRoundedRect(x, y, width, height, arcWidth, arcHeight, context);
   }

   @Override
   public GraphicsContext context() {
      return context;
   }
}
