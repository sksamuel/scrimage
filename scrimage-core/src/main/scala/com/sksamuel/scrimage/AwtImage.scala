package com.sksamuel.scrimage

import java.awt.geom.AffineTransform
import java.awt.image.{AffineTransformOp, BufferedImage, DataBufferByte, DataBufferInt}

/**
 * A skeleton implementation of read only operations based on a backing AWT image.
 */
abstract class AwtImage[R](awt: BufferedImage) extends ReadOnlyOperations[R] with InPlaceOperations[R] {

  override lazy val width: Int = awt.getWidth
  override lazy val height: Int = awt.getHeight

  override def copy: Image = new Image(toNewBufferedImage)

  /**
   * Returns the pixels of this image represented as an array of Pixels.
   */
  def pixels: Array[Pixel] = {
    awt.getRaster.getDataBuffer match {
      case buffer: DataBufferInt if awt.getType == BufferedImage.TYPE_INT_ARGB => buffer.getData.map(ARGBIntPixel.apply)
      case buffer: DataBufferInt if awt.getType == BufferedImage.TYPE_INT_RGB => buffer.getData.map(RGBIntPixel.apply)
      case buffer: DataBufferByte if awt.getType == BufferedImage.TYPE_4BYTE_ABGR =>
        buffer.getData.grouped(4).map { abgr => ARGBIntPixel(abgr(3), abgr(1), abgr(2), abgr.head) }.toArray
      case _ =>
        val pixels = Array.ofDim[Pixel](width * height)
        for ( x <- 0 until width; y <- 0 until height ) {
          pixels(y * width + x) = ARGBIntPixel(awt.getRGB(x, y))
        }
        pixels
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
        awt.setRGB(x, y, newpixel.toARGBInt)
    }
  }

  protected def fillpx(color: Color): Unit = {
    for ( x <- 0 until width; y <- 0 until height ) awt.setRGB(x, y, color.toInt)
  }

  /**
   * Flips this image horizontally.
   */
  protected def fx(): Unit = {
    val tx = AffineTransform.getScaleInstance(-1, 1)
    tx.translate(-width, 0)
    flip(tx)
  }

  /**
   * Flips this image vertically.
   */
  protected def fy(): Unit = {
    val tx = AffineTransform.getScaleInstance(1, -1)
    tx.translate(0, -height)
    flip(tx)
  }

  protected[scrimage] def flip(tx: AffineTransform): Unit = {
    val op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR)
    op.filter(awt, null)
  }

  /**
   * Returns a new AWT BufferedImage from this image.
   *
   * @return a new, non-shared, BufferedImage with the same data as this Image.
   */
  def toNewBufferedImage: BufferedImage = {
    val buffered = new BufferedImage(width, height, awt.getType)
    buffered.getGraphics.drawImage(awt, 0, 0, null)
    buffered
  }
}
