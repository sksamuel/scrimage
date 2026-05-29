package com.sksamuel.scrimage.canvas;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.graphics.RichGraphics2D;

import java.awt.*;
import java.util.Arrays;
import java.util.Collection;

/**
 * A Canvas extends an ImmutableImage with functionality for drawing.
 * It is essentially some sugar around using the Graphics2D functionality in AWT.
 */
public class Canvas {

   private final ImmutableImage image;

   public Canvas(ImmutableImage image) {
      this.image = image;
   }

   /**
    * Returns the image that backs this canvas.
    *
    * @return the ImmutableImage that backs this canvas
    */
   public ImmutableImage getImage() {
      return image;
   }

   private Graphics2D g2(ImmutableImage image) {
      return (Graphics2D) image.awt().getGraphics();
   }

   public void drawInPlace(Drawable... drawables) {
      drawInPlace(Arrays.asList(drawables));
   }

   public void drawInPlace(Collection<Drawable> drawables) {
      Graphics2D g = g2(image);
      try {
         drawables.forEach(d -> drawScoped(g, d));
      } finally {
         g.dispose();
      }
   }

   public Canvas draw(Drawable... drawables) {
      return draw(Arrays.asList(drawables));
   }

   public Canvas draw(Collection<Drawable> drawables) {
      ImmutableImage target = image.copy();
      Graphics2D g = g2(target);
      try {
         drawables.forEach(d -> drawScoped(g, d));
      } finally {
         g.dispose();
      }
      return new Canvas(target);
   }

   // Each drawable gets a scratch Graphics2D snapshot so its GraphicsContext's
   // configure() mutations (setColor, setStroke, setFont, translate, rotate,
   // setComposite, …) don't bleed across to the next drawable. Without this
   // every subsequent drawable inherited whatever state the previous one
   // happened to leave on the shared Graphics2D — a red rectangle followed
   // by an un-configured one would render the second in red too.
   private static void drawScoped(Graphics2D g, Drawable d) {
      Graphics2D scoped = (Graphics2D) g.create();
      try {
         RichGraphics2D rich2d = new RichGraphics2D(scoped);
         d.context().configure(rich2d);
         d.draw(rich2d);
      } finally {
         scoped.dispose();
      }
   }
}
