package com.sksamuel.scrimage.graphics;

import com.sksamuel.scrimage.canvas.painters.Painter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

public class RichGraphics2D extends Graphics2D {

   private final Graphics2D g2;

   public RichGraphics2D(Graphics2D g2) {
      this.g2 = g2;
   }

   /*
    * Sets a basic stroke with the given width.
    */
   public void setBasicStroke(float width) {
      setStroke(new BasicStroke(width));
   }

   /**
    * Sets a simple dashed stroke where the width of the dash and the dash-gap are set by the dashWidth arg.
    * For a more complicated dashing stroke, create an instance of BasicStroke and pass to setStroke.
    */
   public void setDashedStroke(float width, float dashWidth) {
      float[] dashes = new float[]{dashWidth, dashWidth};
      Stroke stroke = new BasicStroke(width, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, dashes, 0f);
      setStroke(stroke);
   }

   public void setWhite() {
      setColor(Color.WHITE);
   }

   public void setBlack() {
      setColor(Color.BLACK);
   }

   public void setColor(com.sksamuel.scrimage.color.Color color) {
      g2.setColor(color.awt());
   }

   public void setPainter(Painter painter) {
      g2.setPaint(painter.paint());
   }

   public void setAntiAlias(boolean on) {
      Graphics2DUtils.setAntiAlias(this, on);
   }

   @Override
   public void draw3DRect(int x, int y, int width, int height, boolean raised) {
      g2.draw3DRect(x, y, width, height, raised);
   }

   @Override
   public void fill3DRect(int x, int y, int width, int height, boolean raised) {
      g2.fill3DRect(x, y, width, height, raised);
   }

   @Override
   public void draw(Shape s) {
      g2.draw(s);
   }

   @Override
   public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
      return g2.drawImage(img, xform, obs);
   }

   @Override
   public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
      g2.drawImage(img, op, x, y);
   }

   @Override
   public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
      g2.drawRenderedImage(img, xform);
   }

   @Override
   public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
      g2.drawRenderableImage(img, xform);
   }

   @Override
   public void drawString(String str, int x, int y) {
      g2.drawString(str, x, y);
   }

   @Override
   public void drawString(String str, float x, float y) {
      g2.drawString(str, x, y);
   }

   @Override
   public void drawString(AttributedCharacterIterator iterator, int x, int y) {
      g2.drawString(iterator, x, y);
   }

   @Override
   public void drawString(AttributedCharacterIterator iterator, float x, float y) {
      g2.drawString(iterator, x, y);
   }

   @Override
   public void drawGlyphVector(GlyphVector g, float x, float y) {
      g2.drawGlyphVector(g, x, y);
   }

   @Override
   public void fill(Shape s) {
      g2.fill(s);
   }

   @Override
   public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
      return g2.hit(rect, s, onStroke);
   }

   @Override
   public GraphicsConfiguration getDeviceConfiguration() {
      return g2.getDeviceConfiguration();
   }

   @Override
   public void setComposite(Composite comp) {
      g2.setComposite(comp);
   }

   @Override
   public void setPaint(Paint paint) {
      g2.setPaint(paint);
   }

   @Override
   public void setStroke(Stroke s) {
      g2.setStroke(s);
   }

   @Override
   public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
      g2.setRenderingHint(hintKey, hintValue);
   }

   @Override
   public Object getRenderingHint(RenderingHints.Key hintKey) {
      return g2.getRenderingHint(hintKey);
   }

   @Override
   public void setRenderingHints(Map<?, ?> hints) {
      g2.setRenderingHints(hints);
   }

   @Override
   public void addRenderingHints(Map<?, ?> hints) {
      g2.addRenderingHints(hints);
   }

   @Override
   public RenderingHints getRenderingHints() {
      return g2.getRenderingHints();
   }

   @Override
   public void translate(int x, int y) {
      g2.translate(x, y);
   }

   @Override
   public void translate(double tx, double ty) {
      g2.translate(tx, ty);
   }

   @Override
   public void rotate(double theta) {
      g2.rotate(theta);
   }

   @Override
   public void rotate(double theta, double x, double y) {
      g2.rotate(theta, x, y);
   }

   @Override
   public void scale(double sx, double sy) {
      g2.scale(sx, sy);
   }

   @Override
   public void shear(double shx, double shy) {
      g2.shear(shx, shy);
   }

   @Override
   public void transform(AffineTransform Tx) {
      g2.transform(Tx);
   }

   @Override
   public void setTransform(AffineTransform Tx) {
      g2.setTransform(Tx);
   }

   @Override
   public AffineTransform getTransform() {
      return g2.getTransform();
   }

   @Override
   public Paint getPaint() {
      return g2.getPaint();
   }

   @Override
   public Composite getComposite() {
      return g2.getComposite();
   }

   @Override
   public void setBackground(Color color) {
      g2.setBackground(color);
   }

   @Override
   public Color getBackground() {
      return g2.getBackground();
   }

   @Override
   public Stroke getStroke() {
      return g2.getStroke();
   }

   @Override
   public void clip(Shape s) {
      g2.clip(s);
   }

   @Override
   public FontRenderContext getFontRenderContext() {
      return g2.getFontRenderContext();
   }

   @Override
   public Graphics create() {
      return g2.create();
   }

   @Override
   public Graphics create(int x, int y, int width, int height) {
      return g2.create(x, y, width, height);
   }

   @Override
   public Color getColor() {
      return g2.getColor();
   }

   @Override
   public void setColor(Color c) {
      g2.setColor(c);
   }

   @Override
   public void setPaintMode() {
      g2.setPaintMode();
   }

   @Override
   public void setXORMode(Color c1) {
      g2.setXORMode(c1);
   }

   @Override
   public Font getFont() {
      return g2.getFont();
   }

   @Override
   public void setFont(Font font) {
      g2.setFont(font);
   }

   @Override
   public FontMetrics getFontMetrics() {
      return g2.getFontMetrics();
   }

   @Override
   public FontMetrics getFontMetrics(Font f) {
      return g2.getFontMetrics(f);
   }

   @Override
   public Rectangle getClipBounds() {
      return g2.getClipBounds();
   }

   @Override
   public void clipRect(int x, int y, int width, int height) {
      g2.clipRect(x, y, width, height);
   }

   @Override
   public void setClip(int x, int y, int width, int height) {
      g2.setClip(x, y, width, height);
   }

   @Override
   public Shape getClip() {
      return g2.getClip();
   }

   @Override
   public void setClip(Shape clip) {
      g2.setClip(clip);
   }

   @Override
   public void copyArea(int x, int y, int width, int height, int dx, int dy) {
      g2.copyArea(x, y, width, height, dx, dy);
   }

   @Override
   public void drawLine(int x1, int y1, int x2, int y2) {
      g2.drawLine(x1, y1, x2, y2);
   }

   @Override
   public void fillRect(int x, int y, int width, int height) {
      g2.fillRect(x, y, width, height);
   }

   @Override
   public void drawRect(int x, int y, int width, int height) {
      g2.drawRect(x, y, width, height);
   }

   @Override
   public void clearRect(int x, int y, int width, int height) {
      g2.clearRect(x, y, width, height);
   }

   @Override
   public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
      g2.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
   }

   @Override
   public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
      g2.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
   }

   @Override
   public void drawOval(int x, int y, int width, int height) {
      g2.drawOval(x, y, width, height);
   }

   @Override
   public void fillOval(int x, int y, int width, int height) {
      g2.fillOval(x, y, width, height);
   }

   @Override
   public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
      g2.drawArc(x, y, width, height, startAngle, arcAngle);
   }

   @Override
   public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
      g2.fillArc(x, y, width, height, startAngle, arcAngle);
   }

   @Override
   public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
      g2.drawPolyline(xPoints, yPoints, nPoints);
   }

   @Override
   public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
      g2.drawPolygon(xPoints, yPoints, nPoints);
   }

   @Override
   public void drawPolygon(Polygon p) {
      g2.drawPolygon(p);
   }

   @Override
   public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
      g2.fillPolygon(xPoints, yPoints, nPoints);
   }

   @Override
   public void fillPolygon(Polygon p) {
      g2.fillPolygon(p);
   }

   @Override
   public void drawChars(char[] data, int offset, int length, int x, int y) {
      g2.drawChars(data, offset, length, x, y);
   }

   @Override
   public void drawBytes(byte[] data, int offset, int length, int x, int y) {
      g2.drawBytes(data, offset, length, x, y);
   }

   @Override
   public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
      return g2.drawImage(img, x, y, observer);
   }

   @Override
   public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
      return g2.drawImage(img, x, y, width, height, observer);
   }

   @Override
   public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
      return g2.drawImage(img, x, y, bgcolor, observer);
   }

   @Override
   public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
      return g2.drawImage(img, x, y, width, height, bgcolor, observer);
   }

   @Override
   public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
      return g2.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
   }

   @Override
   public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
      return g2.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
   }

   @Override
   public void dispose() {
      g2.dispose();
   }

   @Override
   public void finalize() {
      super.finalize();
      g2.finalize();
   }

   @Override
   public String toString() {
      return g2.toString();
   }

   @Override
   @Deprecated
   public Rectangle getClipRect() {
      return g2.getClipRect();
   }

   @Override
   public boolean hitClip(int x, int y, int width, int height) {
      return g2.hitClip(x, y, width, height);
   }

   @Override
   public Rectangle getClipBounds(Rectangle r) {
      return g2.getClipBounds(r);
   }
}
