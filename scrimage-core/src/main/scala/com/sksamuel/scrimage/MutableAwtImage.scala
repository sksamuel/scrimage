package com.sksamuel.scrimage

import java.awt.image.{DataBufferByte, BufferedImage, RescaleOp}
import java.awt.{Graphics2D, RenderingHints}

/**
 * Contains methods that operate on an AWT BufferedImage by mutating the buffer in place.
 * All methods in this class should return Unit
 */
class MutableAwtImage(awt: BufferedImage) extends AwtImage(awt) {

  /**
   * Maps the pixels of this image into another image by applying the given function to each pixel.
   *
   * The function accepts three parameters: x,y,p where x and y are the coordinates of the pixel
   * being transformed and p is the pixel at that location.
   *
   * @param f the function to transform pixel x,y with existing value p into new pixel value p' (p prime)
   */
  protected[scrimage] def mapInPlace(f: (Int, Int, Pixel) => Pixel): Unit = {
    points.foreach {
      case (x, y) =>
        val newpixel = f(x, y, pixel(x, y))
        awt.setRGB(x, y, newpixel.toInt)
    }
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
   * Fills all pixels the given color on the existing image.
   */
  protected[scrimage] def fillInPlace(color: Color): Unit = {
    for (
      x <- 0 until width;
      y <- 0 until height
    ) awt.setRGB(x, y, color.toInt)
  }

  /**
   * Applies the given image over the current buffer.
   */
  protected[scrimage] def overlayInPlace(overlay: BufferedImage, x: Int = 0, y: Int = 0): Unit = {
    val g2 = awt.getGraphics.asInstanceOf[Graphics2D]
    g2.drawImage(overlay, x, y, null)
    g2.dispose()
  }

  def setPixel(x: Int, y: Int, pixel: Pixel): Unit = awt.setRGB(x, y, pixel.argb)

  /**
   * Mutates this image by scaling all pixel values by the given factor (brightness in other words).
   */
  protected def rescale(factor: Double): Unit = {
    val rescale = new RescaleOp(factor.toFloat, 0f,
      new RenderingHints(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY))
    rescale.filter(awt, awt)
  }

  protected def contrastInPlace(factor: Double): Unit = {
    for ( x <- 0 until width; y <- 0 until height ) {
      val p = pixel(x, y)
      val r = PixelTools.truncate((factor * (p.red - 128)) + 128)
      val g = PixelTools.truncate((factor * (p.green - 128)) + 128)
      val b = PixelTools.truncate((factor * (p.blue - 128)) + 128)
      setPixel(x, y, Pixel(r, g, b, p.alpha))
    }
  }
}
