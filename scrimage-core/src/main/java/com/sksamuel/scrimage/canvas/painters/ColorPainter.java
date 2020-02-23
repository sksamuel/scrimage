package com.sksamuel.scrimage.canvas.painters;

import java.awt.*;

public class ColorPainter implements Painter {

   private final Color color;

   public ColorPainter(Color color) {
      this.color = color;
   }

   public ColorPainter(com.sksamuel.scrimage.color.Color color) {
      this(color.awt());
   }


   @Override
   public Paint paint() {
      return color;
   }
}
