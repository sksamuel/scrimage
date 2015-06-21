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

import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.awt.image.{AffineTransformOp, BufferedImage, BufferedImageOp}
import java.io.{ByteArrayInputStream, File, InputStream}
import java.nio.file.{Path, Paths}
import javax.imageio.ImageIO

import com.sksamuel.scrimage.nio.{ImageReader, ImageWriter}
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
class Image(awt: BufferedImage, val metadata: ImageMetadata) extends MutableAwtImage(awt) {

  require(awt != null, "Wrapping image cannot be null")

  import Position._
  import ScaleMethod._
  import X11Colorlist._

  protected[scrimage] def wrapAwt(awt: BufferedImage, metadata: ImageMetadata): Image = {
    new Image(awt, metadata)
  }

  protected[scrimage] def wrapPixels(w: Int, h: Int, pixels: Array[Pixel], metadata: ImageMetadata): Image = {
    Image(w, h, pixels).withMetadata(metadata)
  }

  /**
   * Crops an image by removing cols and rows that are composed only of a single
   * given color.
   *
   * Eg, if an image had a 20 pixel row of white at the top, and this method was
   * invoked with Color.White then the image returned would have that 20 pixel row
   * removed.
   *
   * This method is useful when images have an abudance of a single colour around them.
   *
   * @param color the color to match
   * @return
   */
  def autocrop(color: Color): Image = {
    val x1 = AutocropOps.scanright(color, height, 0, pixels)
    val x2 = AutocropOps.scanleft(color, height, width - 1, pixels)
    val y1 = AutocropOps.scandown(color, width, 0, pixels)
    val y2 = AutocropOps.scanup(color, width, height - 1, pixels)
    subimage(x1, y1, x2 - x1, y2 - y1)
  }

  /**
   * Creates an empty image of the same concrete type as this type with the given dimensions.
   * If the optional color is specified then the background pixels will all be set to that color.
   */
  final protected[scrimage] def blank(w: Int, h: Int, color: Option[Color] = None): Image = {
    val target = wrapAwt(new BufferedImage(w, h, awt.getType), metadata)
    color.foreach(target.fillInPlace)
    target
  }

  /**
   * Creates an empty Image with the same dimensions of this image.
   *
   * @return a new Image that is a clone of this image but with uninitialized data
   */
  def blank: Image = blank(width, height)

  /**
   * Returns an image that is no larger than the given width and height.
   *
   * If the current image is already within the given dimensions then the same image will be returned.
   * If not, then a scaled image, with the same aspect ratio as the original, and with dimensions
   * inside the constraints will be returned.
   *
   * @param width the maximum width
   * @param height the maximum height
   * @return the constrained image.
   */
  def bound(width: Int, height: Int): Image = {
    if (this.width <= width && this.height <= height) this
    else max(width, height)
  }

  /**
   * Creates a new image with the same data as this image.
   * Any operations to the copied image will not write back to the original.
   *
   * @return A copy of this image.
   */
  def copy: Image = {
    val target = blank(width, height)
    target.overlayInPlace(awt, 0, 0)
    target
  }

  /**
   * Returns a copy of the canvas with the given dimensions where the
   * original image has been scaled to completely cover the new dimensions
   * whilst retaining the original aspect ratio.
   *
   * If the new dimensions have a different aspect ratio than the old image
   * then the image will be cropped so that it still covers the new area
   * without leaving any background.
   *
   * @param targetWidth the target width
   * @param targetHeight the target height
   * @param scaleMethod the type of scaling method to use. Defaults to Bicubic
   * @param position where to position the image inside the new canvas
   *
   * @return a new Image with the original image scaled to cover the new dimensions
   */
  def cover(targetWidth: Int,
            targetHeight: Int,
            scaleMethod: ScaleMethod = Bicubic,
            position: Position = Center): Image = {
    val coveredDimensions = ImageTools.dimensionsToCover((targetWidth, targetHeight), (width, height))
    val scaled = scaleTo(coveredDimensions._1, coveredDimensions._2, scaleMethod)
    val x = ((targetWidth - coveredDimensions._1) / 2.0).toInt
    val y = ((targetHeight - coveredDimensions._2) / 2.0).toInt
    blank(targetWidth, targetHeight).overlay(scaled, x, y)
  }

  /**
   * Creates a new Image with the same dimensions of this image and with
   * all the pixels initialized to the given color.
   *
   * @return a new Image with the same dimensions as this
   */
  def fill(color: Color = Color.White): Image = {
    val target = blank
    target.fillInPlace(color)
    target
  }

  /**
   * Apply a sequence of filters in sequence.
   * This is sugar for image.filter(filter1).filter(filter2)....
   *
   * @param filters the sequence filters to apply
   * @return the result of applying each filter in turn
   */
  def filter(filters: Filter*): Image = {
    filters.foldLeft(this)((image, filter) => image.filter(filter))
  }

  /**
   * Creates a copy of this image with the given filter applied.
   * The original (this) image is unchanged.
   *
   * @param filter the filter to apply. See com.sksamuel.scrimage.Filter.
   *
   * @return A new image with the given filter applied.
   */
  def filter(filter: Filter): Image = {
    val target = copy
    filter.apply(target)
    target
  }

  /**
   * Returns a copy of this image with the given dimensions
   * where the original image has been scaled to fit completely
   * inside the new dimensions whilst retaining the original aspect ratio.
   *
   * @param canvasWidth the target width
   * @param canvasHeight the target height
   * @param scaleMethod the algorithm to use for the scaling operation. See ScaleMethod.
   * @param color the color to use as the "padding" colour should the scaled original not fit exactly inside the new dimensions
   * @param position where to position the image inside the new canvas
   *
   * @return a new Image with the original image scaled to fit inside
   */
  def fit(canvasWidth: Int,
          canvasHeight: Int,
          color: Color = White,
          scaleMethod: ScaleMethod = Bicubic,
          position: Position = Center): Image = {
    val (w, h) = ImageTools.dimensionsToFit((canvasWidth, canvasHeight), (width, height))
    val (x, y) = position.calculateXY(canvasWidth, canvasHeight, w, h)
    val scaled = scaleTo(w, h, scaleMethod)
    blank(canvasWidth, canvasHeight).fill(color).overlay(scaled, x, y)
  }

  //  /**
  //   * Returns the pixels of this image represented as an array of Pixels.
  //   */
  //  override def pixels: Array[Pixel] = {
  //    awt.getRaster.getDataBuffer match {
  //      case buffer: DataBufferInt if awt.getType == BufferedImage.TYPE_INT_ARGB => buffer.getData.map(Pixel.apply)
  //      case buffer: DataBufferInt if awt.getType == BufferedImage.TYPE_INT_RGB =>
  //        buffer.getData.map(Pixel.apply)
  //      case buffer: DataBufferByte if awt.getType == BufferedImage.TYPE_4BYTE_ABGR =>
  //        buffer.getData.grouped(4).map { abgr => Pixel(abgr(3), abgr(1), abgr(2), abgr.head) }.toArray
  //      case _ =>
  //        val pixels = Array.ofDim[Pixel](width * height)
  //        for ( x <- 0 until width; y <- 0 until height ) {
  //          pixels(y * width + x) = Pixel(awt.getRGB(x, y))
  //        }
  //        pixels
  //    }
  //  }

  /**
   * Apply the given image with this image using the given composite.
   * The original image is unchanged.
   *
   * @param composite the composite to use. See com.sksamuel.scrimage.Composite.
   * @param applicative the image to apply with the composite.
   *
   * @return A new image with the given image applied using the given composite.
   */
  def composite(composite: Composite, applicative: Image): Image = {
    val target = copy
    composite.apply(target, applicative)
    target
  }

  /**
   * Applies an affine transform in place.
   */
  protected[scrimage] def affineTransform(tx: AffineTransform): BufferedImage = {
    val op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR)
    op.filter(awt, null)
  }

  /**
   * Flips this image horizontally.
   *
   * @return The result of flipping this image horizontally.
   */
  def flipX: Image = {
    val tx = AffineTransform.getScaleInstance(-1, 1)
    tx.translate(-width, 0)
    wrapAwt(affineTransform(tx), metadata)
  }

  /**
   * Flips this image vertically.
   *
   * @return The result of flipping this image vertically.
   */
  def flipY: Image = {
    val tx = AffineTransform.getScaleInstance(1, -1)
    tx.translate(0, -height)
    wrapAwt(affineTransform(tx), metadata)
  }

  /**
   * Returns a new image that is scaled to fit the specified bounds while retaining the same aspect ratio
   * as the original image. The dimensions of the returned image will be the same as the result of the
   * scaling operation. That is, no extra padding will be added to match the bounded width and height. For an
   * operation that will scale an image as well as add padding to fit the dimensions perfectly, then use fit()
   *
   * Requesting a bound of 200,200 on an image of 300,600 will result in a scale to 100,200.
   * Eg, the original image will be scaled down to fit the bounds.
   *
   * Requesting a bound of 150,200 on an image of 150,150 will result in the same image being returned.
   * Eg, the original image cannot be scaled up any further without exceeding the bounds.
   *
   * Requesting a bound of 300,300 on an image of 100,150 will result in a scale to 200,300.
   *
   * Requesting a bound of 100,1000 on an image of 50,50 will result in a scale to 100,100.
   *
   * @param maxW the maximum width
   * @param maxH the maximum height
   *
   * @return A new image that is the result of the binding.
   */
  def max(maxW: Int, maxH: Int): Image = {
    val dimensions = ImageTools.dimensionsToFit((maxW, maxH), (width, height))
    scaleTo(dimensions._1, dimensions._2)
  }

  protected[scrimage] def op(op: BufferedImageOp): Image = {
    val after = op.filter(awt, null)
    wrapAwt(after, metadata)
  }

  def output(path: String)(implicit writer: ImageWriter): Path = forWriter(writer).write(Paths.get(path))
  def output(file: File)(implicit writer: ImageWriter): File = forWriter(writer).write(file)
  def output(path: Path)(implicit writer: ImageWriter): Path = forWriter(writer).write(path)

  /**
   * Resize will resize the canvas, it will not scale the image.
   * This is like a "canvas resize" in Photoshop.
   *
   * If the dimensions are smaller than the current canvas size
   * then the image will be cropped.
   *
   * The position parameter determines how the original image will be positioned on the new
   * canvas.
   *
   * @param targetWidth the target width
   * @param targetHeight the target height
   * @param position where to position the original image after the canvas size change
   * @param background the background color if the canvas was enlarged
   *
   * @return a new Image that is the result of resizing the canvas.
   */
  def resizeTo(targetWidth: Int,
               targetHeight: Int,
               position: Position = Center,
               background: Color = X11Colorlist.White): Image = {
    if (targetWidth == width && targetHeight == height) this
    else {
      val (x, y) = position.calculateXY(targetWidth, targetHeight, width, height)
      blank(targetWidth, targetHeight, Some(background)).overlay(this, x, y)
    }
  }

  /**
   * Returns a new Image that is the result of overlaying this image over the supplied image.
   * That is, the existing image ends up being "on top" of the image parameter.
   * The x / y parameters determine where the (0,0) coordinate of the overlay should be placed.
   *
   * If the image to render exceeds the boundaries of the source image, then the excess
   * pixels will be ignored.
   *
   * @return a new Image with the given image overlaid.
   */
  def underlay(underlayImage: Image, x: Int = 0, y: Int = 0): Image = {
    val target = this.blank
    target.overlayInPlace(underlayImage.awt, x, y)
    target.overlayInPlace(awt, x, y)
    target
  }

  /**
   * Returns a new image that is the result of overlaying the given image over this image.
   * That is, the existing image ends up being "under" the image parameter.
   * The x / y parameters determine where the (0,0) coordinate of the overlay should be placed.
   *
   * If the image to render exceeds the boundaries of the source image, then the excess
   * pixels will be ignored.
   *
   * @return a new Image with the given image overlaid.
   */
  def overlay(overlayImage: AwtImage, x: Int = 0, y: Int = 0): Image = {
    val target = copy
    target.overlayInPlace(overlayImage.awt, x, y)
    target
  }

  /**
   * Resize will resize the canvas, it will not scale the image.
   * This is like a "canvas resize" in Photoshop.
   *
   * @param scaleFactor the scaleFactor. 1 retains original size. 0.5 is half. 2 double. etc
   * @param position where to position the original image after the canvas size change. Defaults to centre.
   * @param background the color to use for expande background areas. Defaults to White.
   *
   * @return a new Image that is the result of resizing the canvas.
   */
  def resize(scaleFactor: Double, position: Position = Center, background: Color = White): Image = {
    resizeTo((width * scaleFactor).toInt, (height * scaleFactor).toInt, position, background)
  }

  /**
   * Resize will resize the canvas, it will not scale the image.
   * This is like a "canvas resize" in Photoshop.
   *
   * @param position where to position the original image after the canvas size change
   *
   * @return a new Image that is the result of resizing the canvas.
   */
  def resizeToHeight(targetHeight: Int, position: Position = Center, background: Color = White): Image = {
    resizeTo((targetHeight / height.toDouble * height).toInt, targetHeight, position, background)
  }

  /**
   * Resize will resize the canvas, it will not scale the image.
   * This is like a "canvas resize" in Photoshop.
   *
   * @param position where to position the original image after the canvas size change
   *
   * @return a new Image that is the result of resizing the canvas.
   */
  def resizeToWidth(targetWidth: Int, position: Position = Center, background: Color = White): Image = {
    resizeTo(targetWidth, (targetWidth / width.toDouble * height).toInt, position, background)
  }

  /**
   * Returns a copy of this image rotated 90 degrees anti-clockwise (counter clockwise to US English speakers).
   *
   * @return
   */
  def rotateLeft: Image = wrapAwt(rotate(Math.PI / 2), metadata)

  /**
   * Returns a copy of this image rotated 90 degrees clockwise.
   *
   * @return
   */
  def rotateRight: Image = wrapAwt(rotate(-Math.PI / 2), metadata)

  /**
   * Creates a new image which is the result of this image
   * padded with the given number of pixels on each edge.
   *
   * Eg, requesting a pad of 30 on an image of 250,300 will result
   * in a new image with a canvas size of 310,360
   *
   * @param size the number of pixels to add on each edge
   * @param color the background of the padded area.
   *
   * @return A new image that is the result of the padding
   */
  def pad(size: Int, color: Color = X11Colorlist.White): Image = {
    padTo(width + size * 2, height + size * 2, color)
  }

  /**
   * Creates a new image which is the result of this image padded to the canvas size specified.
   * If this image is already larger than the specified pad then the sizes of the existing
   * image will be used instead.
   *
   * Eg, requesting a pad of 200,200 on an image of 250,300 will result
   * in keeping the 250,300.
   *
   * Eg2, requesting a pad of 300,300 on an image of 400,250 will result
   * in the width staying at 400 and the height padded to 300.
   *
   * @param targetWidth the size of the output canvas width
   * @param targetHeight the size of the output canvas height
   * @param color the background of the padded area.
   *
   * @return A new image that is the result of the padding
   */
  def padTo(targetWidth: Int, targetHeight: Int, color: Color = X11Colorlist.White): Image = {
    val w = if (width < targetWidth) targetWidth else width
    val h = if (height < targetHeight) targetHeight else height
    val x = ((w - width) / 2.0).toInt
    val y = ((h - height) / 2.0).toInt
    padWith(x, y, w - width - x, h - height - y, color)
  }

  /**
   * Creates a new image by adding the given number of columns/rows on left, top, right and bottom.
   *
   * @param left the number of columns/pixels to add on the left
   * @param top the number of rows/pixels to add to the top
   * @param right the number of columns/pixels to add on the right
   * @param bottom the number of rows/pixels to add to the bottom
   * @param color the background of the padded area.
   *
   * @return A new image that is the result of the padding operation.
   */
  def padWith(left: Int, top: Int, right: Int, bottom: Int, color: Color = White): Image = {
    val w = width + left + right
    val h = height + top + bottom
    blank(w, h, Some(color)).overlay(this, left, top)
  }

  /**
   * Returns a new image with the transarency replaced with the given color.
   */
  def removeTransparency(color: Color): Image = removeTransparency(color.toAWT)
  def removeTransparency(color: java.awt.Color): Image = {
    val target = copy
    target.removetrans(color)
    target
  }

  /**
   * Scale will resize the canvas and scale the image to match.
   * This is like a "image resize" in Photoshop.
   *
   * scaleToWidth will scale the image so that the new image has a width that matches the
   * given targetWidth and the height is determined by the original aspect ratio.
   *
   * Eg, an image of 200,300 with a scaleToWidth of 400 will result
   * in a scaled image of 400,600
   *
   * @param targetWidth the target width
   * @param scaleMethod the type of scaling method to use.
   *
   * @return a new Image that is the result of scaling this image
   */
  def scaleToWidth(targetWidth: Int, scaleMethod: ScaleMethod = Bicubic): Image = {
    scaleTo(targetWidth, (targetWidth / width.toDouble * height).toInt, scaleMethod)
  }

  /**
   * Scale will resize the canvas and scale the image to match.
   * This is like a "image resize" in Photoshop.
   *
   * scaleToHeight will scale the image so that the new image has a height that matches the
   * given targetHeight and the same aspect ratio as the original.
   *
   * Eg, an image of 200,300 with a scaleToHeight of 450 will result
   * in a scaled image of 300,450
   *
   * @param targetHeight the target height
   * @param scaleMethod the type of scaling method to use.
   *
   * @return a new Image that is the result of scaling this image
   */
  def scaleToHeight(targetHeight: Int, scaleMethod: ScaleMethod = Bicubic): Image = {
    scaleTo((targetHeight / height.toDouble * width).toInt, targetHeight, scaleMethod)
  }

  /**
   * Scale will resize the canvas and the image.
   * This is like a "image resize" in Photoshop.
   *
   * @param scaleFactor the target increase or decrease. 1 is the same as original.
   * @param scaleMethod the type of scaling method to use.
   *
   * @return a new Image that is the result of scaling this image
   */
  def scale(scaleFactor: Double, scaleMethod: ScaleMethod = Bicubic): Image = {
    scaleTo((width * scaleFactor).toInt, (height * scaleFactor).toInt, scaleMethod)
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
              scaleMethod: ScaleMethod = Bicubic): Image = {
    scaleMethod match {
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
        op(new ResampleOp(method, targetWidth, targetHeight))
    }
  }

  def toPar: ParImage = new ParImage(awt, metadata)

  /**
   * Returns a new Image that is a subimage or region of the original image.
   *
   * @param x the start x coordinate
   * @param y the start y coordinate
   * @param w the width of the subimage
   * @param h the height of the subimage
   * @return a new Image that is the subimage
   */
  def subimage(x: Int, y: Int, w: Int, h: Int): Image = wrapPixels(w, h, pixels(x, y, w, h), metadata)

  /**
   * Returns an image that is the result of translating the image while keeping the same
   * view window. Eg, if translating by 10,5 then all pixels will move 10 to the right, and 5 down.
   * This would mean 10 columns and 5 rows of background added to the left and top.
   *
   * @return a new Image with this image translated.
   */
  def translate(x: Int, y: Int, background: Color = Color.White): Image = {
    fill(background).overlay(this, x, y)
  }

  /**
   * Removes the given amount of pixels from each edge; like a crop operation.
   *
   * @param amount the number of pixels to trim from each edge
   *
   * @return a new Image with the dimensions width-trim*2, height-trim*2
   */
  def trim(amount: Int): Image = trim(amount, amount, amount, amount)

  /**
   * Removes the given amount of pixels from each edge; like a crop operation.
   *
   * @param left the number of pixels to trim from the left
   * @param top the number of pixels to trim from the top
   * @param right the number of pixels to trim from the right
   * @param bottom the number of pixels to trim from the bottom
   *
   * @return a new Image with the dimensions width-trim*2, height-trim*2
   */
  def trim(left: Int, top: Int, right: Int, bottom: Int): Image = {
    blank(width - left - right, height - bottom - top).overlay(this, -left, -top)
  }

  /**
   * Returns this image, with metadata attached.
   * @interal both the original and the new image will share a buffer
   */
  private[scrimage] def withMetadata(metadata: ImageMetadata): Image = new Image(awt, metadata)

  /**
   * Returns a new image that is the result of scaling this image but without changing the canvas size.
   *
   * This can be thought of as zooming in on a camera - the viewpane does not change but the image increases
   * in size with the outer columns/rows being dropped as required.
   *
   * @param factor how much to zoom by
   * @param method how to apply the scaling method
   * @return the zoomed image
   */
  def zoom(factor: Double, method: ScaleMethod = ScaleMethod.Bicubic): Image = {
    scale(factor, method).resizeTo(width, height)
  }

  def bytes(implicit writer: ImageWriter): Array[Byte] = forWriter(writer).bytes

  def forWriter(writer: ImageWriter): WriteContext = new WriteContext(writer, this)

  /**
   * Returns a new Image with the brightness adjusted.
   */
  def brightness(factor: Double): Image = {
    val target = copy
    target.rescale(factor)
    target
  }

  def contrast(factor: Double) :Image = {
    val target = copy
    target.contrastInPlace(factor)
    target
  }

  /**
   * Extracts a subimage, but using subpixel interpolation.
   */
  def subpixelSubimage(x: Double,
                       y: Double,
                       subWidth: Int,
                       subHeight: Int): Image = {
    require(x >= 0)
    require(x + subWidth < width)
    require(y >= 0)
    require(y + subHeight < height)

    val matrix = Array.ofDim[Pixel](subWidth * subHeight)
    // Simply copy the pixels over, one by one.
    for ( yIndex <- 0 until subHeight;
          xIndex <- 0 until subWidth ) {
      matrix(PixelTools.coordinateToOffset(xIndex, yIndex, subWidth)) = Pixel(subpixel(xIndex + x, yIndex + y))
    }
    wrapPixels(subWidth, subHeight, matrix, metadata)
  }

  /**
   * Extract a patch, centered at a subpixel point.
   */
  def subpixelSubimageCenteredAtPoint(x: Double,
                                      y: Double,
                                      xRadius: Double,
                                      yRadius: Double): Image = {
    val xWidth = 2 * xRadius
    val yWidth = 2 * yRadius

    // The dimensions of the extracted patch must be integral.
    require(xWidth == xWidth.round)
    require(yWidth == yWidth.round)

    subpixelSubimage(
      x - xRadius,
      y - yRadius,
      xWidth.round.toInt,
      yWidth.round.toInt)
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
  def map(f: (Int, Int, Pixel) => Pixel): Image = {
    val target = copy
    target.mapInPlace(f)
    target
  }
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
    // detect iphone mode, and rotate
    IphoneOrientation.reorient(image, metadata).withMetadata(metadata)
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
    val metadata = ImageMetadata.fromFile(file)
    ImageReader.fromFile(file).withMetadata(metadata)
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
