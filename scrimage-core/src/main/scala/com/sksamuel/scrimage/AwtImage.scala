package com.sksamuel.scrimage

import java.awt.geom.AffineTransform
import java.awt.image.{ AffineTransformOp, DataBufferInt, BufferedImage }

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
      case buffer: DataBufferInt if awt.getType == BufferedImage.TYPE_INT_ARGB => buffer.getData.map(ARGBPixel.apply)
      // todo implement this using new instances of Pixel
      //        case buffer: DataBufferInt if awt.getType == BufferedImage.TYPE_INT_RGB => buffer.getData
      //            case buffer: DataBufferByte if awt.getType == BufferedImage.TYPE_3BYTE_BGR =>
      //                val array = new Array[Int](buffer.getData.length / 3)
      //                for ( k <- 0 until array.length ) {
      //                    val blue = array(k * 3)
      //                    val green = array(k * 3 + 1)
      //                    val red = array(k * 3 + 2)
      //                    val pixel = red << 16 | green << 8 | blue << 0
      //                    array(k) = pixel
      //                }
      //                array
      //            case buffer: DataBufferByte if awt.getType == BufferedImage.TYPE_4BYTE_ABGR =>
      //                val array = new Array[Int](buffer.getData.length / 4)
      //                for ( k <- 0 until array.length ) {
      //                    val alpha = array(k * 4)
      //                    val blue = array(k * 4 + 1)
      //                    val green = array(k * 4 + 2)
      //                    val red = array(k * 4 + 3)
      //                    val pixel = alpha << 24 | red << 16 | green << 8 | blue << 0
      //                    array(k) = pixel
      //                }
      //                array
      case _ =>
        val pixels = Array.ofDim[Pixel](width * height)
        for (x <- 0 until width; y <- 0 until height) {
          pixels(y * width + x) = ARGBPixel(awt.getRGB(x, y))
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
        awt.setRGB(x, y, newpixel.toInt)
    }
  }

  protected def fillpx(color: Color): Unit = {
    for (x <- 0 until width; y <- 0 until height) awt.setRGB(x, y, color.toInt)
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
