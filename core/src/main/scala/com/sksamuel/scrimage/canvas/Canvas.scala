package com.sksamuel.scrimage.canvas

import java.awt.image.BufferedImage
import java.awt.{Font, Graphics2D, RenderingHints}

import com.sksamuel.scrimage.{Color, Composite, Image, X11Colorlist}

import scala.language.implicitConversions

/** @author Stephen Samuel */
case class Canvas(image: Image,
                  painter: Painter = X11Colorlist.Black,
                  font: Font = new Font(Font.SERIF, 0, 24),
                  aliasing: Boolean = false) {

  def withPainter(painter: Painter): Canvas = copy(painter = painter)
  def withPainter(color: Color): Canvas = copy(painter = color)
  def withFont(font: Font): Canvas = copy(font = font)
  def withAliasing(aliasing: Boolean): Canvas = copy(aliasing = aliasing)

  private def g2(image: Image): Graphics2D = {
    val g2 = image.awt.getGraphics.asInstanceOf[Graphics2D]
    g2.setPaint(painter.paint)
    if (aliasing) {
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
    }
    g2.setFont(font)
    g2
  }

  def draw(drawables: Drawable*): Canvas = {
    val copy = image.copy
    val g = g2(copy)
    drawables.foreach(_.draw(g))
    g.dispose()
    imageFromAWT(copy.awt)
  }

  def draw(drawables: Iterable[Drawable]): Canvas = {
    val copy = image.copy
    val g = g2(copy)
    drawables.foreach(_.draw(g))
    g.dispose()
    imageFromAWT(copy.awt)
  }

  // TODO: check the type of the given image
  def imageFromAWT(image: BufferedImage): Image = {
    // todo reimplement
    //    val model = Array.ofDim[Int](image.getWidth * image.getHeight)
    //    image.getRGB(0, 0, image.getWidth, image.getHeight, model, 0, image.getWidth)
    //    val raster = ARGBRaster(image.getWidth, image.getHeight, model)
    //    new Image(raster)
    ???
  }

  def watermark(text: String): Canvas = watermark(text, 0.5)
  def watermark(text: String, alpha: Double): Canvas = image.filter(new Watermark(text, font, alpha))

  /** Apply the given image with this image using the given composite.
    * The original image is unchanged.
    *
    * @param composite the composite to use. See com.sksamuel.scrimage.Composite.
    * @param applicative the image to apply with the composite.
    *
    * @return A new image with the given image applied using the given composite.
    */
  def composite(composite: Composite, applicative: Image): Image = {
    val copy = image.copy
    composite.apply(copy, applicative)
    imageFromAWT(copy.awt)
  }
}

object Canvas {
  implicit def image2canvas(image: Image): Canvas = new Canvas(image)
  implicit def canvas2image(canvas: Canvas): Image = canvas.image
}

