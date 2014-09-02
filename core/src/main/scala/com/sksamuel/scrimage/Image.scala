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
import java.awt.image.{BufferedImage, DataBufferInt}
import java.io.{ByteArrayInputStream, File, InputStream}
import javax.imageio.ImageIO

import com.sksamuel.scrimage.Position.Center
import com.sksamuel.scrimage.ScaleMethod._
import com.sksamuel.scrimage.io.ImageWriter
import com.sksamuel.scrimage.scaling.ResampleOpScala
import org.apache.commons.io.{FileUtils, IOUtils}
import thirdparty.mortennobel.{ResampleFilters, ResampleOp}

import scala.List
import scala.concurrent.ExecutionContext

/** An Image represents an abstraction over a set of pixels that allow operations such
  * as resize, scale, rotate, flip, trim, pad, cover, fit. An image does not
  * contain its underlying data directly, but instead delegates to a Raster. A Raster is a simple data
  * structure for a grid of pixels. An Image is immutable and all operations return new Images backed by new Rasters.
  *
  * @author Stephen Samuel
  */
class Image(val raster: Raster) extends ImageLike[Image] with WritableImageLike {
  require(raster != null, "Raster cannot be null")

  val width: Int = raster.width
  val height: Int = raster.height

  /** Returns a new AWT buffered image from the data in this images raster.
    *
    * @return a BufferedImage with the same data as this Image.
    */
  def toBufferedImage: BufferedImage = {
    val imageType = raster match {
      case _: RGBRaster => BufferedImage.TYPE_INT_RGB
      case _: ARGBRaster => BufferedImage.TYPE_INT_ARGB
      case _: GrayRaster => BufferedImage.TYPE_BYTE_GRAY
      case _: GrayAlphaRaster => BufferedImage.TYPE_INT_ARGB
    }
    val buffered = new BufferedImage(width, height, imageType)
    if ((imageType == BufferedImage.TYPE_INT_ARGB) || (imageType == BufferedImage.TYPE_INT_RGB))
      buffered.setRGB(0, 0, width, height, raster.extract.map(_.toInt), 0, width)
    if (imageType == BufferedImage.TYPE_BYTE_GRAY)
      buffered.getRaster.setDataElements(0, 0, width, height, raster.model)
    buffered
  }

  @deprecated("java.awt is to be removed", "22 Jul 2014")
  lazy val awt: BufferedImage = {
    import java.awt.image.{ColorModel, DataBufferInt}
    val cm = ColorModel.getRGBdefault
    val sm = cm.createCompatibleSampleModel(width, height)
    val db = new DataBufferInt(raster.extract.map(_.toInt), width * height)
    val wr = java.awt.image.Raster.createWritableRaster(sm, db, null)
    new BufferedImage(cm, wr, false, null)
  }

  /** This a workaround to allow code written for the old version image to work.
    */
  @deprecated("java.awt is to be removed", "22 Jul 2014")
  def updateFromAWT(): Unit = {
    raster.write(Image(awt).raster.read)
  }

  override def empty: Image = Image.empty(width, height)
  override def copy: Image = new Image(raster.copy)

  override def map(f: (Int, Int, Int) => Int): Image = {
    val target = copy
    target.mapInPlace(f)
    target
  }

  private[scrimage] def mapInPlace(f: (Int, Int, Int) => Int): Unit = {
    points.foreach(p => raster.write(p._1, p._2, f(p._1, p._2, raster.read(p._1, p._2).toInt)))
  }

  /** Returns an image that is no larger than the given width and height.
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

  /** Removes the given amount of pixels from each edge; like a crop operation.
    *
    * @param amount the number of pixels to trim from each edge
    *
    * @return a new Image with the dimensions width-trim*2, height-trim*2
    */
  def trim(amount: Int): Image = trim(amount, amount, amount, amount)

  /** Removes the given amount of pixels from each edge; like a crop operation.
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

  /** Returns an image that is the result of translating the image while keeping the same
    * view window. Eg, if translating by 10,5 then all pixels will move 10 to the right, and 5 down.
    * This would mean 10 columns and 5 rows of background added to the left and top.
    *
    * @return a new Image with this image translated.
    */
  def translate(x: Int, y: Int, background: Color = Color.White): Image = {
    filled(background).overlay(this, x, y)
  }

  /** Returns a new Image that is a subimage or region of the original image.
    *
    * @param x the start x coordinate
    * @param y the start y coordinate
    * @param w the width of the subimage
    * @param h the height of the subimage
    * @return a new Image that is the subimage
    */
  def subimage(x: Int, y: Int, w: Int, h: Int): Image = Image(w, h, pixels(x, y, w, h))

  /** Returns the pixel at the given coordinates as a integer in ARGB format.
    *
    * @param x the x coordinate of the pixel to grab
    * @param y the y coordinate of the pixel to grab
    *
    * @return the ARGB value of the pixel
    */
  def pixel(x: Int, y: Int): Int = raster.pixel(x, y)

  /** Uses linear interpolation to get a sub-pixel.
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
        val pixelInt = pixel(xInt, yInt)

        List(
          weight * PixelTools.alpha(pixelInt),
          weight * PixelTools.red(pixelInt),
          weight * PixelTools.green(pixelInt),
          weight * PixelTools.blue(pixelInt))
      }
    }

    // We perform the weighted averaging (a summation).
    // First though, we need to transpose so that we sum within channels,
    // not within pixels.
    val List(a, r, g, b) = summands.transpose.map(_.sum)

    PixelTools.argb(a.round.toInt, r.round.toInt, g.round.toInt, b.round.toInt)
  }

  /** Extracts a subimage, but using subpixel interpolation.
    */
  def subpixelSubimage(x: Double,
                       y: Double,
                       subWidth: Int,
                       subHeight: Int): Image = {
    require(x >= 0)
    require(x + subWidth < width)
    require(y >= 0)
    require(y + subHeight < height)
    val raster = ARGBRaster(subWidth, subHeight)
    // Simply copy the pixels over, one by one.
    for (
      yIndex <- 0 until subHeight;
      xIndex <- 0 until subWidth
    ) {
      raster.write(xIndex, yIndex, subpixel(xIndex + x, yIndex + y))
    }
    new Image(raster)
  }

  /** Extract a patch, centered at a subpixel point.
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

  /** Returns all the patches of a given size in the image, assuming pixel
    * alignment (no subpixel extraction).
    *
    * The patches are returned as a sequence of closures.
    */
  def patches(patchWidth: Int, patchHeight: Int): IndexedSeq[() => Image] =
    for (
      row <- 0 to height - patchHeight;
      col <- 0 to width - patchWidth
    ) yield {
      () => new Image(raster.patch(col, row, patchWidth, patchHeight))
    }

  /** Returns the pixels of this image represented as an array of Integers.
    *
    * @return
    */
  def pixels: Array[Int] = {
    raster match {
      case i: ARGBRaster => i.extract.map(_.argb)
    }
  }

  /** Creates a copy of this image with the given filter applied.
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

  /** Apply a sequence of filters in sequence.
    * This is sugar for image.filter(filter1).filter(filter2)....
    *
    * @param filters the sequence filters to apply
    * @return the result of applying each filter in turn
    */
  def filter(filters: Filter*): Image = filters.foldLeft(this)((image, filter) => image.filter(filter))

  def removeTransparency(color: java.awt.Color): Image = {
    def rmTransparency(c: RGBColor): RGBColor = {
      val r = (c.red * c.alpha + color.getRed * color.getAlpha * (255 - c.alpha) / 255) / 255
      val g = (c.green * c.alpha + color.getGreen * color.getAlpha * (255 - c.alpha) / 255) / 255
      val b = (c.blue * c.alpha + color.getBlue * color.getAlpha * (255 - c.alpha) / 255) / 255
      RGBColor(r, g, b)
    }
    val rgbColors = raster.read.map(_.toRGB).map(rmTransparency)
    new Image(Raster(width, height, rgbColors, Raster.RGB))
  }

  /** Flips this image horizontally.
    *
    * @return The result of flipping this image horizontally.
    */
  def flipX: Image = {
    val copy = raster.copy
    for (x <- 0 until width; y <- 0 until height) {
      copy.write(x, y, raster.read(width - 1 - x, y))
    }
    new Image(copy)
  }

  /** Flips this image vertically.
    *
    * @return The result of flipping this image vertically.
    */
  def flipY: Image = {
    val copy = raster.copy
    for (x <- 0 until width; y <- 0 until height) {
      copy.write(x, y, raster.read(x, height - 1 - y))
    }
    new Image(copy)
  }

  /** Returns a copy of this image rotated 90 degrees anti-clockwise (counter clockwise to US English speakers).
    *
    * @return
    */
  def rotateLeft = {
    val rotated = raster.empty(height, width)
    for(x <- 0 until width; y <- 0 until height; c <- 0 until raster.n_channel){
      rotated.writeChannel(y, width - 1 - x, c)(raster.readChannel(x, y, c))
    }
    new Image(rotated)
  }

  /** Returns a copy of this image rotated 90 degrees clockwise. */
  def rotateRight = {
    val rotated = raster.empty(height, width)
    for(x <- 0 until width; y <- 0 until height; c <- 0 until raster.n_channel){
      rotated.writeChannel(height - 1 - y, x, c)(raster.readChannel(x, y, c))
    }
    new Image(rotated)
  }

  /** Returns a copy of this image with the given dimensions
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

  /** Returns a copy of the canvas with the given dimensions where the
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

  /** Scale will resize both the canvas and the image.
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
      case Bicubic => ResampleOpScala.scaleTo(ResampleOpScala.bicubicFilter)(this)(targetWidth, targetHeight, Image.SCALE_THREADS)
      case _ =>
        val method = scaleMethod match {
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

  /** Resize will resize the canvas, it will not scale the image.
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

  /** Returns a new Image that is the result of overlaying the supplied image over this image
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
    val copy = raster.copy
    for (
      x1 <- 0 until overlayImage.width;
      y1 <- 0 until overlayImage.height
    ) {
      val x2 = x1 + x
      val y2 = y1 + y
      if (0 <= x2 && x2 < width && 0 <= y2 && y2 < height)
        copy.write(x2, y2, overlayImage.raster.read(x1, y1))
    }
    new Image(copy)
  }

  /** Crops an image by removing cols and rows that are composed only of a single
    * given color.
    *
    * Eg, if an image had a 20 pixel border of white at the top, and this method was
    * invoked with Color.White then the image returned would have that 20 pixel border
    * removed.
    *
    * This method is useful when images have an abudance of
    *
    * @param color the color to match
    * @return
    */
  def autocrop(color: Color): Image = {
    def uniform(color: Color, pixels: Array[Int]) = pixels.forall(p => p == color.argb)
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

  /** Creates a new image which is the result of this image padded to the canvas size specified.
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

  /** Creates a new image by adding the given number of columns/rows on left, top, right and bottom.
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

  /** Returns a new image that is scaled to fit the specified bounds while retaining the same aspect ratio
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

  /** Returns a new image that is the result of scaling this image but without changing the canvas size.
    *
    * This can be thought of as zooming in on a camera - the viewpane does not change but the image increases
    * in size with the outer columns/rows being dropped as required.
    *
    * @param factor how much to zoom by
    * @param method how to apply the scaling method
    * @return the zoomed image
    */
  def zoom(factor: Double, method: ScaleMethod = ScaleMethod.Bicubic) = scale(factor, method).resizeTo(width, height)

  /** Creates a new Image with the same dimensions of this image and with
    * all the pixels initialized by the given color.
    *
    * @return a new Image with the same dimensions as this
    */
  def filled(color: Color = Color.White): Image = Image.filled(width, height, color)

  def writer[T <: ImageWriter](format: Format[T]): T = format.writer(this)

  // This tuple contains all the state that identifies this particular image.
  private[scrimage] def imageState = (width, height, raster.model.toList)

  // See this Stack Overflow question to see why this is implemented this way.
  // http://stackoverflow.com/questions/7370925/what-is-the-standard-idiom-for-implementing-equals-and-hashcode-in-scala
  override def hashCode = imageState.hashCode

  override def equals(other: Any): Boolean =
    other match {
      case that: Image => imageState == that.imageState
      case _ => false
    }

  /** Creates a MutableImage instance backed by this images raster.
    *
    * Note, any changes to the mutable image write back to this Image.
    * If you want a mutable copy then you must first copy this image
    * before invoking this operation.
    *
    * @return
    */
  @deprecated
  def toMutable: MutableImage = new MutableImage(raster)

  /** Creates an AsyncImage instance backed by this image.
    *
    * The returned AsyncImage will contain the same backing array
    * as this image.
    *
    * To return back to an image instance use asyncImage.toImage
    *
    * @return an AsyncImage wrapping this image.
    */
  def toAsync(implicit executionContext: ExecutionContext): AsyncImage = AsyncImage(this)

  /** Clears all image data to the given color
    */
  @deprecated("use filled", "1.4")
  def clear(color: Color): Image = filled(color)
}

object Image {

  ImageIO.scanForPlugins()
  val CANONICAL_DATA_TYPE = BufferedImage.TYPE_INT_ARGB
  val SCALE_THREADS = Runtime.getRuntime.availableProcessors()

  /** Create a new Image from an array of pixels. The specified
    * width and height must match the number of pixels.
    *
    * @return a new Image
    */
  def apply(w: Int, h: Int, pixels: Array[Int]): Image = {
    require(w * h == pixels.size)
    Image.empty(w, h).map((x, y, p) => pixels(PixelTools.coordinateToOffset(x, y, w)))
  }

  /** Create a new Image from a byte stream. This is intended to create
    * an image from an image format eg PNG, not from a stream of pixels.
    *
    * @param bytes the bytes from the format stream
    * @return a new Image
    */
  def apply(bytes: Array[Byte]): Image = apply(new ByteArrayInputStream(bytes))

  def apply(in: InputStream): Image = {
    require(in != null)
    require(in.available > 0)

    val bytes = IOUtils.toByteArray(in) // lets buffer in case we have to repeat
    IOUtils.closeQuietly(in)

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
        }.getOrElse(throw new RuntimeException("Unparsable image"))
    }
  }

  def apply(file: File): Image = {
    require(file != null)
    val in = FileUtils.openInputStream(file)
    apply(in)
  }

  /** Create a new Scrimage Image from an AWT Image.
    *
    * @param awt the source AWT Image
    *
    * @return a new Scrimage Image
    */
  def apply(awt: java.awt.Image): Image = {
    require(awt != null, "AWT image cannot be null")
    // todo optimisation to check for TYPE_INT_ARGB before copying
    val buff = new BufferedImage(awt.getWidth(null), awt.getHeight(null), BufferedImage.TYPE_INT_ARGB)
    val g2 = buff.getGraphics.asInstanceOf[Graphics2D]
    g2.drawImage(awt, 0, 0, null)
    g2.dispose()
    val raster = ARGBRaster(
      awt.getWidth(null),
      awt.getHeight(null),
      buff.getRaster.getDataBuffer.asInstanceOf[DataBufferInt].getData
    )
    new Image(raster)
  }

  /** Creates a new Image which is a copy of the given image.
    * Any operations to the new object do not affect the original image.
    *
    * @param image the image to copy
    *
    * @return a new Image object.
    */
  def apply(image: Image): Image = image.copy

  /** Return a new Image with the given width and height, with all pixels set to the supplied colour.
    *
    * @param width the width of the new Image
    * @param height the height of the new Image
    * @param color the color to set all pixels to
    *
    * @return the new Image
    */
  def filled(width: Int, height: Int, color: Color = Color.White): Image = {
    val r = ARGBRaster(width, height, color)
    new Image(r)
  }

  /** Create a new Image that is the given width and height with no initialization. This will usually result in a
    * default black background (all pixel data defaulting to zeroes) but that is not guaranteed.
    *
    * @param width the width of the new image
    * @param height the height of the new image
    *
    * @return the new Image with the given width and height
    */
  def empty(width: Int, height: Int): Image = new Image(ARGBRaster(width, height))
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
  implicit def awt2rich(awt: java.awt.Image) = Image(awt)
}
