package com.sksamuel.scrimage.canvas.painters;

import java.awt.*;

public class LinearGradient implements Painter {

   public static LinearGradient horizontal(Color color1, Color color2) {
      return new LinearGradient(0, Integer.MIN_VALUE, color1, 0, Integer.MAX_VALUE, color2);
   }

   public static LinearGradient vertical(Color color1, Color color2) {
      return new LinearGradient(Integer.MIN_VALUE, 0, color1, Integer.MAX_VALUE, 0, color2);
   }

   private final int x1;
   private final int y1;
   private final Color color1;
   private final int x2;
   private final int y2;
   private final Color color2;

   public int getX1() {
      return x1;
   }

   public int getY1() {
      return y1;
   }

   public Color getColor1() {
      return color1;
   }

   public int getX2() {
      return x2;
   }

   public int getY2() {
      return y2;
   }

   public Color getColor2() {
      return color2;
   }

   public LinearGradient(int x1, int y1, Color color1, int x2, int y2, Color color2) {
      this.x1 = x1;
      this.y1 = y1;
      this.color1 = color1;
      this.x2 = x2;
      this.y2 = y2;
      this.color2 = color2;
   }

   @Override
   public Paint paint() {
      return new GradientPaint(x1, y1, color1, x2, y2, color2);
   }
}
