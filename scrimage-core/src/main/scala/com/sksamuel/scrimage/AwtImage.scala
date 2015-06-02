package com.sksamuel.scrimage

import java.awt.geom.AffineTransform
import java.awt.image.{AffineTransformOp, BufferedImage, BufferedImageOp, DataBufferByte, DataBufferInt}
import java.awt.{Graphics2D, RenderingHints}

/**
 * A skeleton implementation of read only operations based on a backing AWT image.
 */
abstract class AwtImage[R](awt: BufferedImage) extends ReadOnlyOperations[R] with InPlaceOperations[R] {

  override lazy val width: Int = awt.getWidth
  override lazy val height: Int = awt.getHeight

  /**
   * Create a new Image which is a copy of this image.
   * Any operations on the new image do not write back to the original.
   *
   * @return A copy of this image.
   */
  override def copy: Image = new Image(toNewBufferedImage)

  /**
   * Returns the pixels of this image represented as an array of Pixels.
   */
  def pixels: Array[Pixel] = {
    awt.getRaster.getDataBuffer match {
      case buffer: DataBufferInt if awt.getType == BufferedImage.TYPE_INT_ARGB => buffer.getData.map(Pixel.apply)
      case buffer: DataBufferInt if awt.getType == BufferedImage.TYPE_INT_RGB =>
        buffer.getData.map(Pixel.apply)
      case buffer: DataBufferByte if awt.getType == BufferedImage.TYPE_4BYTE_ABGR =>
        buffer.getData.grouped(4).map { abgr => Pixel(abgr(3), abgr(1), abgr(2), abgr.head) }.toArray
      case _ =>
        val pixels = Array.ofDim[Pixel](width * height)
        for ( x <- 0 until width; y <- 0 until height ) {
          pixels(y * width + x) = Pixel(awt.getRGB(x, y))
        }
        pixels
    }
  }

  protected[scrimage] def overlayInPlace(overlayImage: Image, x: Int = 0, y: Int = 0): Unit = {
    val g2 = graphics
    g2.drawImage(overlayImage.awt, x, y, null)
    g2.dispose()
  }

  protected[scrimage] def fastscale(targetWidth: Int, targetHeight: Int): Image = {
    val target = Image.apply(targetWidth, targetHeight)
    val g2 = target.graphics
    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR)
    g2.drawImage(awt, 0, 0, targetWidth, targetHeight, null)
    g2.dispose()
    target
  }

  protected[scrimage] def op(op: BufferedImageOp): Image = {
    val scaled = op.filter(awt, null)
    Image(scaled)
  }

  protected[scrimage] def removetrans(color: java.awt.Color): Unit = {
    def rmTransparency(p: Pixel): Pixel = {
      val r = (p.red * p.alpha + color.getRed * color.getAlpha * (255 - p.alpha) / 255) / 255
      val g = (p.green * p.alpha + color.getGreen * color.getAlpha * (255 - p.alpha) / 255) / 255
      val b = (p.blue * p.alpha + color.getBlue * color.getAlpha * (255 - p.alpha) / 255) / 255
      Pixel(r, g, b, 255)
    }
    for ( w <- 0 until width; h <- 0 until height ) {
      awt.setRGB(w, h, rmTransparency(Pixel(awt.getRGB(w, h))).toInt)
    }
  }

  /**
   * Maps the pixels of this image into another image by applying the given function to each pixel.
   *
   * The function accepts three parameters: x,y,p where x and y are the coordinates of the pixel
   * being transformed and p is the pixel at that location.
   *
   * @param f the function to transform pixel x,y with existing value p into new pixel value p' (p prime)
   * @return
   */
  protected[scrimage] def mapInPlace(f: (Int, Int, Pixel) => Pixel): Unit = {
    points.foreach {
      case (x, y) =>
        val newpixel = f(x, y, pixel(x, y))
        awt.setRGB(x, y, newpixel.toInt)
    }
  }

  protected[scrimage] def graphics: Graphics2D = awt.getGraphics.asInstanceOf[Graphics2D]

  protected[scrimage] def rotate(angle: Double): Image = {
    val target = Image(height, width)
    val g2 = target.graphics
    val offset = angle match {
      case a if a < 0 => (0, width)
      case a if a > 0 => (height, 0)
      case _ => (0, 0)
    }
    g2.translate(offset._1, offset._2)
    g2.rotate(angle)
    g2.drawImage(awt, 0, 0, null)
    g2.dispose()
    target
  }

  protected def fillpx(color: Color): Unit = {
    for (
      x <- 0 until width;
      y <- 0 until height
    ) awt.setRGB(x, y, color.toInt)
  }

  /**
   * Flips this image horizontally.
   */
  def flipX: R = {
    val tx = AffineTransform.getScaleInstance(-1, 1)
    tx.translate(-width, 0)
    Image(flip(tx)).asInstanceOf[R]
  }

  /**
   * Flips this image vertically.
   */
  def flipY: R = {
    val tx = AffineTransform.getScaleInstance(1, -1)
    tx.translate(0, -height)
    Image(flip(tx)).asInstanceOf[R]
  }

  protected[scrimage] def flip(tx: AffineTransform): BufferedImage = {
    val op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR)
    op.filter(awt, null)
  }

  /**
   * Returns a new AWT BufferedImage from this image using the same AWT type.
   *
   * @return a new, non-shared, BufferedImage with the same data as this Image.
   */
  def toNewBufferedImage: BufferedImage = {
    val target = new BufferedImage(width, height, awt.getType)
    val g2 = target.getGraphics
    g2.drawImage(awt, 0, 0, null)
    g2.dispose()
    target
  }
}
