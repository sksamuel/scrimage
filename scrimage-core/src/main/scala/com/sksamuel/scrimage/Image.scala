/*
   Copyright 2013-2014 Stephen K Samuel

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.sksamuel.scrimage

import java.awt.image.BufferedImage
import java.io.{ByteArrayInputStream, File, InputStream}
import javax.imageio.ImageIO

import com.sksamuel.scrimage.ScaleMethod._
import com.sksamuel.scrimage.nio.ImageReader
import org.apache.commons.io.IOUtils
import thirdparty.mortennobel.{ResampleFilters, ResampleOp}

import scala.language.implicitConversions

class ImageParseException extends RuntimeException("Unparsable image")

/**
 * An immutable Image backed by an AWT BufferedImage.
 *
 * An Image represents an abstraction that allow operations such
 * as resize, scale, rotate, flip, trim, pad, cover, fit.
 *
 * All operations on an image are read only or return a cloned instance of this image.
 * For operations that can be performed without a copying step, see MutableImage.
 *
 * @author Stephen Samuel
 */
class Image(awt: BufferedImage, metadata: ImageMetadata)
  extends AbstractImage(awt, metadata) {

  require(awt != null, "Wrapping image cannot be null")

  override protected[scrimage] def wrapAwt(awt: BufferedImage, metadata: ImageMetadata): Image.this.type = {
    new Image(awt, metadata).asInstanceOf[Image.this.type]
  }

  override protected[scrimage] def wrapPixels(w: Int,
                                              h: Int,
                                              pixels: Array[Pixel],
                                              metadata: ImageMetadata): Image.this.type = {
    val image = Image(w, h, pixels)
    new Image(image.awt, metadata).asInstanceOf[Image.this.type]
  }

  /**
   * Scale will resize both the canvas and the image.
   * This is like a "image resize" in Photoshop.
   *
   * The size of the scaled instance are taken from the given
   * width and height parameters.
   *
   * @param targetWidth the target width
   * @param targetHeight the target height
   * @param scaleMethod the type of scaling method to use. Defaults to SmoothScale
   *
   * @return a new Image that is the result of scaling this image
   */
  def scaleTo(targetWidth: Int,
              targetHeight: Int,
              scaleMethod: ScaleMethod = Bicubic): Image.this.type = {
    val i = scaleMethod match {
      case FastScale => wrapAwt(fastscale(targetWidth, targetHeight), metadata)
      // todo put this back
      // case Bicubic =>
      // ResampleOpScala.scaleTo(ResampleOpScala.bicubicFilter)(this)(targetWidth, targetHeight, Image.SCALE_THREADS)
      case _ =>
        val method = scaleMethod match {
          case Bicubic => ResampleFilters.biCubicFilter
          case Bilinear => ResampleFilters.triangleFilter
          case BSpline => ResampleFilters.bSplineFilter
          case Lanczos3 => ResampleFilters.lanczos3Filter
          case _ => ResampleFilters.biCubicFilter
        }
        super.op(new ResampleOp(method, targetWidth, targetHeight))
    }
    i.asInstanceOf[Image.this.type]
  }

  def toPar: ParImage = new ParImage(awt, metadata)

  def withMetadata(metadata: ImageMetadata): Image = new Image(awt, metadata)
}

object Image {

  ImageIO.scanForPlugins()

  val CANONICAL_DATA_TYPE = BufferedImage.TYPE_INT_ARGB

  /**
   * Create a new Image from an array of pixels. The specified
   * width and height must match the number of pixels.
   *
   * @return a new Image
   */
  def apply(w: Int, h: Int, pixels: Array[Pixel]): Image = apply(w, h, pixels, CANONICAL_DATA_TYPE)
  def apply(w: Int, h: Int, pixels: Array[Pixel], `type`: Int): Image = {
    require(w * h == pixels.length)
    val image = Image(w, h, `type`)
    image.mapInPlace((x, y, p) => pixels(PixelTools.coordinateToOffset(x, y, w)))
    image
  }

  /**
   * Create a new Image from an array of bytes. This is intended to create
   * an image from an image format eg PNG, not from a stream of pixels.
   *
   * @param bytes the bytes from the format stream
   * @return a new Image
   */
  def apply(bytes: Array[Byte]): Image = ImageReader.fromBytes(bytes, CANONICAL_DATA_TYPE)
  def apply(bytes: Array[Byte], `type`: Int): Image = ImageReader.fromBytes(bytes, `type`)

  @deprecated("use fromStream", "2.0")
  def apply(in: InputStream): Image = fromStream(in)
  /**
   * Create a new Image from an input stream. This is intended to create
   * an image from an image format eg PNG, not from a stream of pixels.
   * This method will also attach metadata if available.
   *
   * @param in the stream to read the bytes from
   * @return a new Image
   */
  def fromStream(in: InputStream, `type`: Int = CANONICAL_DATA_TYPE): Image = {
    require(in != null)
    require(in.available > 0)
    val bytes = IOUtils.toByteArray(in)
    val image = ImageReader.fromStream(new ByteArrayInputStream(bytes), `type`)
    val metadata = ImageMetadata.fromStream(new ByteArrayInputStream(bytes))
    new Image(image.awt, metadata)
  }

  /**
   * Creates a new Image from the resource on the classpath.
   */
  def fromResource(path: String, `type`: Int = CANONICAL_DATA_TYPE): Image = {
    fromStream(getClass.getResourceAsStream(path), `type`)
  }

  @deprecated("use fromFile", "2.0")
  def apply(file: File): Image = fromFile(file)
  /**
   * Create a new Image from a file.
   * This method will also attach metadata.
   */
  def fromFile(file: File): Image = {
    require(file != null)
    val image = ImageReader.fromFile(file)
    val metadata = ImageMetadata.fromFile(file)
    new Image(image.awt, metadata)
  }

  /**
   * Create a new Scrimage Image from an AWT Image.
   * This method will copy the given AWT image so that modifications to the original do not
   * write forward to the scrimage Image
   *
   * @param awt the source AWT Image
   * @return a new Scrimage Image
   */
  @deprecated("use fromAwt", "2.0")
  def apply(awt: java.awt.Image): Image = fromAwt(awt)
  def fromAwt(awt: java.awt.Image, `type`: Int = CANONICAL_DATA_TYPE): Image = {
    val target = new BufferedImage(awt.getWidth(null), awt.getHeight(null), `type`)
    val g2 = target.getGraphics
    g2.drawImage(awt, 0, 0, null)
    g2.dispose()
    new Image(target, ImageMetadata.empty)
  }

  /**
   * Create a new Scrimage Image from an AWT Image.
   * This method will not copy the underlying image, so care should be taken that the image
   * passed in is not mutated elsewhere.
   *
   * @param awt the source AWT Image
   * @param type the AWT image type to use. If the image is not in this format already it will be coped.
   *             specify -1 if you want to use the original
   * @return a new Scrimage Image
   */
  def wrapAwt(awt: BufferedImage, `type`: Int = -1): Image = {
    if (`type` == -1 || awt.getType == `type`) new Image(awt, ImageMetadata.empty)
    else fromAwt(awt)
  }

  /**
   * Return a new Image with the given width and height, with all pixels set to the supplied colour.
   *
   * @param width the width of the new Image
   * @param height the height of the new Image
   * @param color the color to set all pixels to
   *
   * @return the new Image
   */
  def filled(width: Int, height: Int, color: Color = Color.White, `type`: Int = CANONICAL_DATA_TYPE): Image = {
    val target = apply(width, height, `type`)
    for ( w <- 0 until width; h <- 0 until height )
      target.awt.setRGB(w, h, color.toRGB.toInt)
    target
  }

  /**
   * Create a new Image that is the given width and height with no initialization. This will usually result in a
   * default black background (all pixel data defaulting to zeroes) but that is not guaranteed.
   *
   * @param width the width of the new image
   * @param height the height of the new image
   *
   * @return the new Image with the given width and height
   */
  def apply(width: Int, height: Int): Image = apply(width, height, CANONICAL_DATA_TYPE)
  def apply(width: Int, height: Int, `type`: Int): Image = {
    val target = new BufferedImage(width, height, `type`)
    new Image(target, ImageMetadata.empty)
  }
}

object Implicits {
  implicit def awtToScrimage(awt: java.awt.Image): Image = Image.fromAwt(awt)
}
