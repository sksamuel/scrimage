package com.sksamuel.scrimage.canvas.painters;

import java.awt.*;

public class LinearGradient implements Painter {

   /**
    * Returns a left-to-right gradient — the colour varies along the horizontal
    * axis, color1 on the left and color2 on the right.
    *
    * @return a horizontal LinearGradient from color1 on the left to color2 on the right
    */
   public static LinearGradient horizontal(Color color1, Color color2) {
      return new LinearGradient(Integer.MIN_VALUE, 0, color1, Integer.MAX_VALUE, 0, color2);
   }

   /**
    * Returns a top-to-bottom gradient — the colour varies along the vertical
    * axis, color1 at the top and color2 at the bottom.
    *
    * @return a vertical LinearGradient from color1 at the top to color2 at the bottom
    */
   public static LinearGradient vertical(Color color1, Color color2) {
      return new LinearGradient(0, Integer.MIN_VALUE, color1, 0, Integer.MAX_VALUE, color2);
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

   /**
    * Resolves the "span the whole image" sentinel coordinates used by
    * {@link #horizontal} and {@link #vertical} against a concrete image size.
    * <p>
    * A horizontal gradient becomes (0,0) to (width,0) and a vertical gradient
    * becomes (0,0) to (0,height). A gradient constructed with explicit
    * coordinates is returned unchanged. Callers that know the size of the
    * surface being painted (ImmutableImage.fill, RichGraphics2D) must call this
    * before {@link #paint()}, otherwise AWT receives a GradientPaint spanning
    * Integer.MIN_VALUE..Integer.MAX_VALUE and renders a solid mid colour.
    *
    * @param width  the width of the surface being painted
    * @param height the height of the surface being painted
    * @return a LinearGradient with concrete coordinates
    */
   public LinearGradient resolve(int width, int height) {
      if (x1 == Integer.MIN_VALUE && x2 == Integer.MAX_VALUE) {
         return new LinearGradient(0, 0, color1, width, 0, color2);
      } else if (y1 == Integer.MIN_VALUE && y2 == Integer.MAX_VALUE) {
         return new LinearGradient(0, 0, color1, 0, height, color2);
      }
      return this;
   }

   @Override
   public Paint paint() {
      return new GradientPaint(x1, y1, color1, x2, y2, color2);
   }
}
