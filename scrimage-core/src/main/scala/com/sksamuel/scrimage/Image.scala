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

import java.awt._
import java.awt.geom.AffineTransform
import java.awt.image.{ AffineTransformOp, BufferedImage, ColorModel, DataBufferInt, Raster }
import java.io.{ ByteArrayInputStream, File, InputStream }
import java.nio.file.Path
import javax.imageio.ImageIO

import com.sksamuel.scrimage.Position.Center
import com.sksamuel.scrimage.ScaleMethod._
import com.sksamuel.scrimage.nio.{ PngWriter, ImageWriter }
import org.apache.commons.io.{ FileUtils, IOUtils }
import thirdparty.mortennobel.{ ResampleFilters, ResampleOp }

import scala.List
import scala.language.implicitConversions

class ImageParseException extends RuntimeException("Unparsable image")

/**
 * An Image represents an abstraction over a set of pixels that allow operations such
 * as resize, scale, rotate, flip, trim, pad, cover, fit. An image does not
 * contain its underlying data directly, but instead delegates to a Raster. A Raster is a simple data
 * structure for a grid of pixels. An Image is immutable and all operations return new Images backed by new Rasters.
 *
 * @author Stephen Samuel
 */
class Image(private[scrimage] val awt: BufferedImage) extends ImageLike[Image] {
  require(awt != null, "Wrapping image cannot be null")

  lazy val width: Int = awt.getWidth
  lazy val height: Int = awt.getHeight

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

  override def empty: Image = Image.empty(width, height)

  override def copy: Image = new Image(toNewBufferedImage)

  def map(f: (Int, Int, Pixel) => Pixel): Image = {
    val target = copy
    target.mapInPlace(f)
    target
  }

  private[scrimage] def mapInPlace(f: (Int, Int, Pixel) => Pixel): this.type = {
    points.foreach {
      case (x, y) =>
        val newpixel = f(x, y, pixel(x, y))
        awt.setRGB(x, y, newpixel.toInt)
    }
    this
  }

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
  def constrain(width: Int, height: Int): Image = {
    if (this.width <= width && this.height <= height) this
    else bound(width, height)
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
    Image.empty(width - left - right, height - bottom - top).overlay(this, -left, -top)
  }

  /**
   * Returns an image that is the result of translating the image while keeping the same
   * view window. Eg, if translating by 10,5 then all pixels will move 10 to the right, and 5 down.
   * This would mean 10 columns and 5 rows of background added to the left and top.
   *
   * @return a new Image with this image translated.
   */
  def translate(x: Int, y: Int, background: Color = Color.White): Image = {
    filled(background).overlay(this, x, y)
  }

  /**
   * Returns a new Image that is a subimage or region of the original image.
   *
   * @param x the start x coordinate
   * @param y the start y coordinate
   * @param w the width of the subimage
   * @param h the height of the subimage
   * @return a new Image that is the subimage
   */
  def subimage(x: Int, y: Int, w: Int, h: Int): Image = Image(w, h, pixels(x, y, w, h))

  /**
   * Returns the pixel at the given coordinates as a integer in ARGB format.
   *
   * @param x the x coordinate of the pixel to grab
   * @param y the y coordinate of the pixel to grab
   *
   * @return the ARGB value of the pixel
   */
  def pixel(x: Int, y: Int): Pixel = new ARGBPixel(awt.getRGB(x, y))

  /**
   * Uses linear interpolation to get a sub-pixel.
   *
   * Legal values for `x` and `y` are in [0, width) and [0, height),
   * respectively.
   */
  def subpixel(x: Double, y: Double): Int = {
    require(x >= 0 && x < width && y >= 0 && y < height)

    // As a part of linear interpolation, determines the integer coordinates
    // of the pixel's neighbors, as well as the amount of weight each should
    // get in the weighted average.
    // Operates on one dimension at a time.
    def integerPixelCoordinatesAndWeights(double: Double, numPixels: Int): List[(Int, Double)] = {
      if (double <= 0.5) List((0, 1.0))
      else if (double >= numPixels - 0.5) List((numPixels - 1, 1.0))
      else {
        val shifted = double - 0.5
        val floor = shifted.floor
        val floorWeight = 1 - (shifted - floor)
        val ceil = shifted.ceil
        val ceilWeight = 1 - floorWeight
        assert(floorWeight + ceilWeight == 1)
        List((floor.toInt, floorWeight), (ceil.toInt, ceilWeight))
      }
    }

    val xIntsAndWeights = integerPixelCoordinatesAndWeights(x, width)
    val yIntsAndWeights = integerPixelCoordinatesAndWeights(y, height)

    // These are the summands in the weighted averages.
    // Note there are 4 weighted averages: one for each channel (a, r, g, b).
    val summands = for (
      (xInt, xWeight) <- xIntsAndWeights;
      (yInt, yWeight) <- yIntsAndWeights
    ) yield {
      val weight = xWeight * yWeight
      if (weight == 0) List(0.0, 0.0, 0.0, 0.0)
      else {
        val px = pixel(xInt, yInt)
        List(
          weight * px.alpha,
          weight * px.red,
          weight * px.green,
          weight * px.blue)
      }
    }

    // We perform the weighted averaging (a summation).
    // First though, we need to transpose so that we sum within channels,
    // not within pixels.
    val List(a, r, g, b) = summands.transpose.map(_.sum)

    PixelTools.argb(a.round.toInt, r.round.toInt, g.round.toInt, b.round.toInt)
  }

  /**
   * Extracts a subimage, but using subpixel interpolation.
   */
  def subpixelSubimage(x: Double,
                       y: Double,
                       subWidth: Int,
                       subHeight: Int): Image = {
    //    require(x >= 0)
    //    require(x + subWidth < width)
    //    require(y >= 0)
    //    require(y + subHeight < height)
    //    val raster = ARGBRaster(subWidth, subHeight)
    //    // Simply copy the pixels over, one by one.
    //    for (
    //      yIndex <- 0 until subHeight;
    //      xIndex <- 0 until subWidth
    //    ) {
    //      raster.write(xIndex, yIndex, subpixel(xIndex + x, yIndex + y))
    //    }
    //    new Image(raster)
    // todo reimplement
    ???
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
   * Returns all the patches of a given size in the image, assuming pixel
   * alignment (no subpixel extraction).
   *
   * The patches are returned as a sequence of closures.
   */
  def patches(patchWidth: Int, patchHeight: Int): IndexedSeq[() => Image] = {
    // to do reimplement
    //    for (
    //      row <- 0 to height - patchHeight;
    //      col <- 0 to width - patchWidth
    //    ) yield {
    //      () => new Image(raster.patch(col, row, patchWidth, patchHeight))
    //    }
    ???
  }

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
   * Apply a sequence of filters in sequence.
   * This is sugar for image.filter(filter1).filter(filter2)....
   *
   * @param filters the sequence filters to apply
   * @return the result of applying each filter in turn
   */
  def filter(filters: Filter*): Image = filters.foldLeft(this)((image, filter) => image.filter(filter))

  /**
   * Returns a new image with the transarency replaced with the given color.
   */
  def removeTransparency(color: java.awt.Color): Image = {

    def rmTransparency(p: Pixel): Int = {
      val r = (p.red * p.alpha + color.getRed * color.getAlpha * (255 - p.alpha) / 255) / 255
      val g = (p.green * p.alpha + color.getGreen * color.getAlpha * (255 - p.alpha) / 255) / 255
      val b = (p.blue * p.alpha + color.getBlue * color.getAlpha * (255 - p.alpha) / 255) / 255
      RGBPixel(r, g, b).toInt
    }

    val pxs = pixels
    val rgb = pxs.map(rmTransparency)
    val buffer = new DataBufferInt(rgb, rgb.length)

    val bandMasks = Array(0xFF0000, 0xFF00, 0xFF, 0xFF000000)
    val raster = Raster.createPackedRaster(buffer, width, height, width, bandMasks, null)

    val cm = ColorModel.getRGBdefault()
    val target = new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), null)
    new Image(target)
  }

  override def toString: String = s"Image [width=$width, height=$height, type=${awt.getType}]"

  /**
   * Flips this image horizontally.
   *
   * @return The result of flipping this image horizontally.
   */
  def flipX: Image = {
    val tx = AffineTransform.getScaleInstance(-1, 1)
    tx.translate(-width, 0)
    flip(tx)
  }

  /**
   * Flips this image vertically.
   *
   * @return The result of flipping this image vertically.
   */
  def flipY: Image = {
    val tx = AffineTransform.getScaleInstance(1, -1)
    tx.translate(0, -height)
    flip(tx)
  }

  protected[scrimage] def flip(tx: AffineTransform): Image = {
    val op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR)
    val flipped = op.filter(awt, null)
    new Image(flipped)
  }

  /**
   * Returns a copy of this image rotated 90 degrees anti-clockwise (counter clockwise to US English speakers).
   *
   * @return
   */
  def rotateLeft: Image = rotate(Math.PI / 2)

  /**
   * Returns a copy of this image rotated 90 degrees clockwise.
   *
   * @return
   */
  def rotateRight: Image = rotate(-Math.PI / 2)

  private def rotate(angle: Double): Image = {
    val target = new BufferedImage(height, width, awt.getType)
    val g2 = target.getGraphics.asInstanceOf[Graphics2D]
    val offset = angle match {
      case a if a < 0 => (0, width)
      case a if a > 0 => (height, 0)
      case _ => (0, 0)
    }
    g2.translate(offset._1, offset._2)
    g2.rotate(angle)
    g2.drawImage(awt, 0, 0, null)
    g2.dispose()
    new Image(target)
  }

  /**
   * Returns a copy of this image with the given dimensions
   * where the original image has been scaled to fit completely
   * inside the new dimensions whilst retaining the original aspect ratio.
   *
   * @param targetWidth the target width
   * @param targetHeight the target height
   * @param scaleMethod the algorithm to use for the scaling operation. See ScaleMethod.
   * @param color the color to use as the "padding" colour should the scaled original not fit exactly inside the new dimensions
   * @param position where to position the image inside the new canvas
   *
   * @return a new Image with the original image scaled to fit inside
   */
  def fit(targetWidth: Int,
          targetHeight: Int,
          color: Color = X11Colorlist.White,
          scaleMethod: ScaleMethod = Bicubic,
          position: Position = Center): Image = {
    val fittedDimensions = ImageTools.dimensionsToFit((targetWidth, targetHeight), (width, height))
    val x = ((targetWidth - fittedDimensions._1) / 2.0).toInt
    val y = ((targetHeight - fittedDimensions._2) / 2.0).toInt
    val scaled = scaleTo(fittedDimensions._1, fittedDimensions._2, scaleMethod)
    Image.filled(targetWidth, targetHeight, color).overlay(scaled, x, y)
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
    Image.empty(targetWidth, targetHeight).overlay(scaled, x, y)
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
  def scaleTo(targetWidth: Int, targetHeight: Int, scaleMethod: ScaleMethod = Bicubic): Image = {

    scaleMethod match {
      case FastScale =>
        val target = Image.empty(targetWidth, targetHeight)
        val g2 = target.awt.getGraphics.asInstanceOf[Graphics2D]
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR)
        g2.drawImage(awt, 0, 0, targetWidth, targetHeight, null)
        g2.dispose()
        target
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
        val op = new ResampleOp(Image.SCALE_THREADS, method, targetWidth, targetHeight)
        val scaled = op.filter(awt, null)
        Image(scaled)
    }

  }

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
      Image.filled(targetWidth, targetHeight, background).overlay(this, x, y)
    }
  }

  /**
   * Returns a new Image that is the result of overlaying the supplied image over this image
   * The x / y parameters determine where the (0,0) coordinate of the overlay should be placed.
   *
   * If the image to render exceeds the boundaries of the source image, then the excess
   * pixels will be ignored.
   *
   * @param overlayImage the image to overlay.
   *
   * @return a new Image with the given image overlaid.
   */
  def overlay(overlayImage: Image, x: Int = 0, y: Int = 0): Image = {
    val copy = toNewBufferedImage
    copy.getGraphics.drawImage(overlayImage.awt, x, y, null)
    new Image(copy)
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
    def uniform(color: Color, pixels: Array[Pixel]) = pixels.forall(p => p.toInt == color.argb)
    def scanright(col: Int, image: Image): Int = {
      if (uniform(color, pixels(col, 0, 1, height))) scanright(col + 1, image)
      else col
    }
    def scanleft(col: Int, image: Image): Int = {
      if (uniform(color, pixels(col, 0, 1, height))) scanleft(col - 1, image)
      else col
    }
    def scandown(row: Int, image: Image): Int = {
      if (uniform(color, pixels(0, row, width, 1))) scandown(row + 1, image)
      else row
    }
    def scanup(row: Int, image: Image): Int = {
      if (uniform(color, pixels(0, row, width, 1))) scanup(row - 1, image)
      else row
    }
    val x1 = scanright(0, this)
    val x2 = scanleft(width - 1, this)
    val y1 = scandown(0, this)
    val y2 = scanup(height - 1, this)
    subimage(x1, y1, x2 - x1, y2 - y1)
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
  def padWith(left: Int, top: Int, right: Int, bottom: Int, color: Color = Color.White): Image = {
    val w = width + left + right
    val h = height + top + bottom
    Image.filled(w, h, color).overlay(this, left, top)
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
   * @param boundedWidth the maximum width
   * @param boundedHeight the maximum height
   *
   * @return A new image that is the result of the binding.
   */
  def bound(boundedWidth: Int, boundedHeight: Int): Image = {
    val dimensions = ImageTools.dimensionsToFit((boundedWidth, boundedHeight), (width, height))
    scaleTo(dimensions._1, dimensions._2)
  }

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
  def zoom(factor: Double, method: ScaleMethod = ScaleMethod.Bicubic): Image = scale(factor, method)
    .resizeTo(width, height)

  /**
   * Creates a new Image with the same dimensions of this image and with
   * all the pixels initialized by the given color.
   *
   * @return a new Image with the same dimensions as this
   */
  def filled(color: Color = Color.White): Image = Image.filled(width, height, color)

  def bytes(implicit writer: ImageWriter): Array[Byte] = forWriter(writer).bytes

  def output(file: File)(implicit writer: ImageWriter): File = forWriter(writer).write(file)

  def output(path: Path)(implicit writer: ImageWriter): Path = forWriter(writer).write(path)

  def forWriter(writer: ImageWriter): WriteContext = new WriteContext(writer, this)
}

object Image {

  ImageIO.scanForPlugins()

  val CANONICAL_DATA_TYPE = BufferedImage.TYPE_INT_ARGB
  val SCALE_THREADS = Runtime.getRuntime.availableProcessors()

  /**
   * Create a new Image from an array of pixels. The specified
   * width and height must match the number of pixels.
   *
   * @return a new Image
   */
  def apply(w: Int, h: Int, pixels: Array[Pixel]): Image = {
    require(w * h == pixels.length)
    Image.empty(w, h).mapInPlace((x, y, p) => pixels(PixelTools.coordinateToOffset(x, y, w)))
  }

  /**
   * Create a new Image from a byte stream. This is intended to create
   * an image from an image format eg PNG, not from a stream of pixels.
   *
   * @param bytes the bytes from the format stream
   * @return a new Image
   */
  def apply(bytes: Array[Byte]): Image = {
    try {
      apply(ImageIO.read(new ByteArrayInputStream(bytes)))
    } catch {
      case e: Exception =>
        import scala.collection.JavaConverters._
        ImageIO.getImageReaders(new ByteArrayInputStream(bytes)).asScala.foldLeft(None: Option[Image]) {
          (valueOpt, reader) =>
            // only bother to read if it hasn't already successfully been read
            valueOpt orElse {

              try {
                reader.setInput(new ByteArrayInputStream(bytes), true, true)
                val params = reader.getDefaultReadParam
                val imageTypes = reader.getImageTypes(0)
                while (imageTypes.hasNext) {
                  val imageTypeSpecifier = imageTypes.next()
                  val bufferedImageType = imageTypeSpecifier.getBufferedImageType
                  if (bufferedImageType == BufferedImage.TYPE_BYTE_GRAY) {
                    params.setDestinationType(imageTypeSpecifier)
                  }
                }
                val bufferedImage = reader.read(0, params)
                Some(apply(bufferedImage))
              } catch {
                case e: Exception => None
              }
            }
        }.getOrElse(throw new ImageParseException)
    }
  }

  def apply(in: InputStream): Image = {
    require(in != null)
    require(in.available > 0)

    val bytes = IOUtils.toByteArray(in) // lets buffer in case we have to repeat
    IOUtils.closeQuietly(in)

    apply(bytes)
  }

  def apply(file: File): Image = {
    require(file != null)
    val in = FileUtils.openInputStream(file)
    apply(in)
  }

  /**
   * Create a new Scrimage Image from an AWT Image.
   *
   * @param awt the source AWT Image
   *
   * @return a new Scrimage Image
   */
  def apply(awt: java.awt.Image): Image = {
    require(awt != null, "AWT image cannot be null")
    // todo optimisation to check for TYPE_INT_ARGB before copying
    val target = new BufferedImage(awt.getWidth(null), awt.getHeight(null), BufferedImage.TYPE_INT_ARGB)
    val g2 = target.getGraphics.asInstanceOf[Graphics2D]
    g2.drawImage(awt, 0, 0, null)
    g2.dispose()
    new Image(target)
  }

  /**
   * Creates a new Image which is a copy of the given image.
   * Any operations to the new object do not affect the original image.
   *
   * @param image the image to copy
   *
   * @return a new Image object.
   */
  def apply(image: Image): Image = image.copy

  /**
   * Return a new Image with the given width and height, with all pixels set to the supplied colour.
   *
   * @param width the width of the new Image
   * @param height the height of the new Image
   * @param color the color to set all pixels to
   *
   * @return the new Image
   */
  def filled(width: Int, height: Int, color: Color = Color.White): Image = {
    val target = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    for (
      w <- 0 until width;
      h <- 0 until height
    ) target.setRGB(w, h, color.toRGB.toInt)
    new Image(target)
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
  def empty(width: Int, height: Int): Image = {
    val target = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    new Image(target)
  }

}

sealed trait ScaleMethod
object ScaleMethod {
  object FastScale extends ScaleMethod
  object Lanczos3 extends ScaleMethod
  object BSpline extends ScaleMethod
  object Bilinear extends ScaleMethod
  object Bicubic extends ScaleMethod
}

object Implicits {
  implicit def awtToScrimage(awt: java.awt.Image): Image = Image(awt)
}
