package com.sksamuel.scrimage

import java.awt.geom.AffineTransform
import java.awt.image.{AffineTransformOp, BufferedImage, BufferedImageOp, RescaleOp}
import java.awt.{Graphics2D, RenderingHints}
import java.io.File
import java.nio.file.{Paths, Path}

import com.sksamuel.scrimage.nio.ImageWriter

/**
 * A skeleton implementation of read only operations based on a backing AWT image.
 */
abstract class AbstractImage(protected[scrimage] val awt: BufferedImage) {

  import X11Colorlist._
  import ScaleMethod._
  import Position._

  /**
   * Create a new image from the given BufferedImage.
   * The type returned is the concrete type of the implementing class.
   * Implementations should simply wrap the given image without copying it unless necessary.
   * It is the responsibility of the caller to ensure that the awt image passed in
   * is a non-shared buffer.
   */
  protected[scrimage] def wrapAwt(awt: BufferedImage): this.type

  protected[scrimage] def wrapPixels(w: Int, h: Int, pixels: Array[Pixel]): this.type

  /**
   * Creates an empty image of the same concrete type as this type with the given dimensions.
   * If the optional color is specified then the background pixels will all be set to that color.
   */
  final protected[scrimage] def blank(w: Int, h: Int, color: Option[Color] = None): this.type = {
    val target = wrapAwt(new BufferedImage(w, h, awt.getType))
    color.foreach(target.fillInPlace)
    target
  }

  /**
   * @return the width of the image
   */
  lazy val width: Int = awt.getWidth

  /**
   * @return the height of the image
   */
  lazy val height: Int = awt.getHeight

  /**
   * Returns the centre coordinates for the image.
   */
  lazy val center: (Int, Int) = (width / 2, height / 2)
  lazy val radius: Int = Math.sqrt(Math.pow(width / 2.0, 2) + Math.pow(height / 2.0, 2)).toInt
  lazy val dimensions: (Int, Int) = (width, height)

  /**
   * @return Returns the aspect ratio for this image.
   */
  lazy val ratio: Double = if (height == 0) 0 else width / height.toDouble

  /**
   * Returns all the pixels for the image
   *
   * @return an array of pixels for this image
   */
  def pixels: Array[Pixel] = iterator.toArray

  /**
   * Returns the pixels of the image as an iterator.
   * The iterator is the most efficient way to lazily iterator over the pixels as the pixels will only
   * be fetched from the raster as needed.
   * @return the iterator
   */
  def iterator: Iterator[Pixel] = new Iterator[Pixel] {
    private var k = 0
    def hasNext: Boolean = k < AbstractImage.this.count
    def next(): Pixel = {
      val (x, y) = PixelTools.offsetToCoordinate(k, width)
      val rgb = awt.getRGB(x, y)
      k = k + 1
      Pixel(rgb)
    }
  }

  /**
   * Returns the pixel at the given coordinates.
   *
   * @param x the x coordinate of the pixel to grab
   * @param y the y coordinate of the pixel to grab
   *
   * @return the Pixel at the location
   */
  def pixel(x: Int, y: Int): Pixel = Pixel(awt.getRGB(x, y))

  /**
   * Returns the pixel at the given coordinate.
   *
   * @param p the pixel as an integer tuple
   * @return the pixel
   */
  def pixel(p: (Int, Int)): Pixel = pixel(p._1, p._2)

  // This tuple contains all the state that identifies this particular image.
  private[scrimage] def imageState = (width, height, pixels.toList)

  // See this Stack Overflow question to see why this is implemented this way.
  // http://stackoverflow.com/questions/7370925/what-is-the-standard-idiom-for-implementing-equals-and-hashcode-in-scala
  override def hashCode: Int = imageState.hashCode

  override def equals(other: Any): Boolean = {
    other match {
      case that: AbstractImage =>
        this.width == that.width &&
          this.height == that.height &&
          (iterator sameElements that.iterator)
      case _ => false
    }
  }

  lazy val points: Seq[(Int, Int)] = for ( x <- 0 until width; y <- 0 until height ) yield (x, y)

  /**
   * Returns the number of pixels in the image.
   * @return the number of pixels
   */
  lazy val count: Int = width * height

  def forall(f: (Int, Int, Pixel) => Boolean): Boolean = points.forall(p => f(p._1, p._2, pixel(p)))
  def foreach(f: (Int, Int, Pixel) => Unit): Unit = points.foreach(p => f(p._1, p._2, pixel(p)))

  def row(y: Int): Array[Pixel] = pixels(0, y, width, 1)
  def col(x: Int): Array[Pixel] = pixels(x, 0, 1, height)

  /**
   * Returns true if a pixel with the given color exists.
   *
   * @param color the pixel colour to look for.
   * @return true if there exists at least one pixel that has the given pixels color
   */
  def contains(color: Color): Boolean = exists(p => p.toInt == color.toPixel.toInt)

  /**
   * Creates a new image with the same data as this image.
   * Any operations to the copied image will not write back to the original.
   *
   * @return A copy of this image.
   */
  def copy: this.type = {
    val target = blank(width, height)
    target.overlayInPlace(this, 0, 0)
    target
  }

  /**
   * Returns true if the predicate holds on the image
   * @param p a predicate
   * @return true if p holds for at least one pixel
   */
  def exists(p: Pixel => Boolean): Boolean = iterator.exists(p)

  /**
   * Returns the color at the given coordinates.
   *
   * @return the RGBColor value at the coords
   */
  def color(x: Int, y: Int): RGBColor = pixel(x, y).toInt

  /**
   * Returns the ARGB components for the pixel at the given coordinates
   *
   * @param x the x coordinate of the pixel component to grab
   * @param y the y coordinate of the pixel component to grab
   *
   * @return an array containing ARGB components in that order.
   */
  def argb(x: Int, y: Int): Array[Int] = {
    val p = pixel(x, y)
    Array(p.alpha, p.red, p.green, p.blue)
  }

  /**
   * Returns the ARGB components for all pixels in this image
   *
   * @return an array containing ARGB components in that order.
   */
  def argb: Array[Array[Int]] = {
    pixels.map(p => Array(p.alpha, p.red, p.green, p.blue))
  }

  def rgb(x: Int, y: Int): Array[Int] = {
    val p = pixel(x, y)
    Array(p.red, p.green, p.blue)
  }

  def rgb: Array[Array[Int]] = {
    pixels.map(p => Array(p.red, p.green, p.blue))
  }

  /**
   * Returns a rectangular region within the given boundaries as a single
   * dimensional array of integers.
   *
   * Eg, pixels(10, 10, 30, 20) would result in an array of size 600 with
   * the first row of the region in indexes 0,..,29, second row 30,..,59 etc.
   *
   * @param x the start x coordinate
   * @param y the start y coordinate
   * @param w the width of the region
   * @param h the height of the region
   * @return an Array of pixels for the region
   */
  def pixels(x: Int, y: Int, w: Int, h: Int): Array[Pixel] = {
    for (
      y1 <- Array.range(y, y + h);
      x1 <- Array.range(x, x + w)
    ) yield pixel(x1, y1)
  }

  /**
   * Returns a set of the distinct colours used in this image.
   * @return the set of distinct Colors
   */
  def colours: Set[RGBColor] = iterator.map(pixel => pixel.toColor).toSet

  /**
   * Counts the number of pixels with the given colour.
   *
   * @param color the colour to detect.
   * @return the number of pixels that matched the colour of the given pixel
   */
  def count(color: Color): Int = iterator.count(_.toColor == color)

  /**
   * Counts the number of pixels that are true for the given predicate
   * @param p a predicate
   * @return the number of pixels that evaluated true
   */
  def count(p: Pixel => Boolean): Int = iterator.count(p)

  /**
   * Creates an empty Image with the same dimensions of this image.
   *
   * @return a new Image that is a clone of this image but with uninitialized data
   */
  def blank: this.type = blank(width, height)

  /**
   * Mutates this image by scaling all pixel values by the given factor (brightness in other words).
   * @mutable
   */
  protected def rescale(factor: Double): Unit = {
    val rescale = new RescaleOp(factor.toFloat, 0f,
      new RenderingHints(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY))
    rescale.filter(awt, awt)
  }

  def patch(x: Int, y: Int, patchWidth: Int, patchHeight: Int): Array[Pixel] = {
    val px = pixels
    val patch = Array.ofDim[Pixel](patchWidth * patchHeight)
    for ( i <- y until y + patchHeight ) {
      System.arraycopy(px, offset(x, y), patch, offset(0, y), patchWidth)
    }
    patch
  }

  /**
   * Returns all the patches of a given size in the image, assuming pixel
   * alignment (no subpixel extraction).
   *
   * The patches are returned as a sequence of pixel matrices closures
   */
  def patches(patchWidth: Int, patchHeight: Int): IndexedSeq[() => Array[Pixel]] = {
    for (
      row <- 0 to height - patchHeight;
      col <- 0 to width - patchWidth
    ) yield {
      () => patch(col, row, patchWidth, patchHeight)
    }
  }

  /**
   * Returns a new image with the transarency replaced with the given color.
   */
  def removeTransparency(color: Color): this.type = removeTransparency(color.toAWT)
  def removeTransparency(color: java.awt.Color): this.type = {
    val target = copy
    target.removetrans(color)
    target
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

  protected[scrimage] def fastscale(targetWidth: Int, targetHeight: Int): BufferedImage = {
    val target = new BufferedImage(targetWidth, targetHeight, awt.getType)
    val g2 = target.getGraphics.asInstanceOf[Graphics2D]
    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR)
    g2.drawImage(awt, 0, 0, targetWidth, targetHeight, null)
    g2.dispose()
    target
  }

  protected[scrimage] def op(op: BufferedImageOp): this.type = {
    val after = op.filter(awt, null)
    wrapAwt(after)
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

  def metadata = ImageMetadata.fromImage(this)

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

  /**
   * Returns a new Image that is a subimage or region of the original image.
   *
   * @param x the start x coordinate
   * @param y the start y coordinate
   * @param w the width of the subimage
   * @param h the height of the subimage
   * @return a new Image that is the subimage
   */
  def subimage(x: Int, y: Int, w: Int, h: Int): this.type = wrapPixels(w, h, pixels(x, y, w, h))

  protected[scrimage] def rotate(angle: Double): BufferedImage = {
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
    target
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
  def autocrop(color: Color): this.type = {
    def uniform(color: Color, pixels: Array[Pixel]) = pixels.forall(p => p.toInt == color.toInt)
    def scanright(col: Int, image: this.type): Int = {
      if (uniform(color, pixels(col, 0, 1, height))) scanright(col + 1, image)
      else col
    }
    def scanleft(col: Int, image: this.type): Int = {
      if (uniform(color, pixels(col, 0, 1, height))) scanleft(col - 1, image)
      else col
    }
    def scandown(row: Int, image: this.type): Int = {
      if (uniform(color, pixels(0, row, width, 1))) scandown(row + 1, image)
      else row
    }
    def scanup(row: Int, image: this.type): Int = {
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
   * Apply the given image with this image using the given composite.
   * The original image is unchanged.
   *
   * @param composite the composite to use. See com.sksamuel.scrimage.Composite.
   * @param applicative the image to apply with the composite.
   *
   * @return A new image with the given image applied using the given composite.
   */
  def composite(composite: Composite, applicative: AbstractImage): this.type = {
    val target = copy
    composite.apply(target, applicative)
    target
  }

  /**
   * Fills all pixels the given color on the existing image.
   * @mutable
   */
  protected def fillInPlace(color: Color): Unit = {
    for (
      x <- 0 until width;
      y <- 0 until height
    ) awt.setRGB(x, y, color.toInt)
  }

  /**
   * Flips this image horizontally.
   *
   * @return The result of flipping this image horizontally.
   */
  def flipX: this.type = {
    val tx = AffineTransform.getScaleInstance(-1, 1)
    tx.translate(-width, 0)
    wrapAwt(affineTransform(tx))
  }

  /**
   * Flips this image vertically.
   *
   * @return The result of flipping this image vertically.
   */
  def flipY: this.type = {
    val tx = AffineTransform.getScaleInstance(1, -1)
    tx.translate(0, -height)
    wrapAwt(affineTransform(tx))
  }

  /**
   * Applies an affine transform returning a new BufferedImage.
   * @mutable
   */
  protected[scrimage] def affineTransform(tx: AffineTransform): BufferedImage = {
    val op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR)
    op.filter(awt, null)
  }

  /**
   * Returns a new AWT BufferedImage from this image using the same AWT type.

   * @return a new, non-shared, BufferedImage with the same data as this Image.
   */
  def toNewBufferedImage: BufferedImage = {
    val target = new BufferedImage(width, height, awt.getType)
    val g2 = target.getGraphics
    g2.drawImage(awt, 0, 0, null)
    g2.dispose()
    target
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
  def max(maxW: Int, maxH: Int): this.type = {
    val dimensions = ImageTools.dimensionsToFit((maxW, maxH), (width, height))
    scaleTo(dimensions._1, dimensions._2)
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
               background: Color = X11Colorlist.White): this.type = {
    if (targetWidth == width && targetHeight == height) this
    else {
      val (x, y) = position.calculateXY(targetWidth, targetHeight, width, height)
      blank(targetWidth, targetHeight, Some(background)).overlay(this, x, y)
    }
  }

  /**
   * Creates a new Image with the same dimensions of this image and with
   * all the pixels initialized to the given color.
   *
   * @return a new Image with the same dimensions as this
   */
  def fill(color: Color = Color.White): this.type = {
    val target = blank
    for ( w <- 0 until width; h <- 0 until height )
      target.awt.setRGB(w, h, color.toRGB.toInt)
    target
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
  def underlay(underlayImage: AbstractImage, x: Int = 0, y: Int = 0): this.type = {
    val target = this.blank
    target.overlayInPlace(underlayImage, x, y)
    target.overlayInPlace(this, x, y)
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
  def overlay(overlayImage: AbstractImage, x: Int = 0, y: Int = 0): this.type = {
    val target = copy
    target.overlayInPlace(overlayImage, x, y)
    target
  }

  /**
   * Applies the given image over the current buffer.
   * @mutable
   */
  protected[scrimage] def overlayInPlace(overlayImage: AbstractImage, x: Int = 0, y: Int = 0): Unit = {
    val g2 = awt.getGraphics.asInstanceOf[Graphics2D]
    g2.drawImage(overlayImage.awt, x, y, null)
    g2.dispose()
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
  def resize(scaleFactor: Double, position: Position = Center, background: Color = White): this.type = {
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
  def resizeToHeight(targetHeight: Int, position: Position = Center, background: Color = White): this.type = {
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
  def resizeToWidth(targetWidth: Int, position: Position = Center, background: Color = White): this.type = {
    resizeTo(targetWidth, (targetWidth / width.toDouble * height).toInt, position, background)
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
  def bound(width: Int, height: Int): this.type = {
    if (this.width <= width && this.height <= height) this
    else max(width, height)
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
            position: Position = Center): this.type = {
    val coveredDimensions = ImageTools.dimensionsToCover((targetWidth, targetHeight), (width, height))
    val scaled = scaleTo(coveredDimensions._1, coveredDimensions._2, scaleMethod)
    val x = ((targetWidth - coveredDimensions._1) / 2.0).toInt
    val y = ((targetHeight - coveredDimensions._2) / 2.0).toInt
    blank(targetWidth, targetHeight).overlay(scaled, x, y)
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
          position: Position = Center): this.type = {
    val (w, h) = ImageTools.dimensionsToFit((canvasWidth, canvasHeight), (width, height))
    val (x, y) = position.calculateXY(canvasWidth, canvasHeight, w, h)
    val scaled = scaleTo(w, h, scaleMethod)
    blank(canvasWidth, canvasHeight).fill(color).overlay(scaled, x, y)
  }

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
  def pad(size: Int, color: Color = X11Colorlist.White): this.type = {
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
  def padTo(targetWidth: Int, targetHeight: Int, color: Color = X11Colorlist.White): this.type = {
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
  def padWith(left: Int, top: Int, right: Int, bottom: Int, color: Color = White): this.type = {
    val w = width + left + right
    val h = height + top + bottom
    blank(w, h, Some(color)).overlay(this, left, top)
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
  def scaleToWidth(targetWidth: Int, scaleMethod: ScaleMethod = Bicubic): this.type = {
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
  def scaleToHeight(targetHeight: Int, scaleMethod: ScaleMethod = Bicubic): this.type = {
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
  def scale(scaleFactor: Double, scaleMethod: ScaleMethod = Bicubic): this.type = {
    scaleTo((width * scaleFactor).toInt, (height * scaleFactor).toInt, scaleMethod)
  }

  /**
   * Scale will resize the canvas and scale the image to match.
   * This is like a "image resize" in Photoshop.
   *
   * @param targetWidth the target width
   * @param targetHeight the target width
   * @param scaleMethod the type of scaling method to use.
   *
   * @return a new Image that is the result of scaling this image
   */
  def scaleTo(targetWidth: Int, targetHeight: Int, scaleMethod: ScaleMethod = Bicubic): this.type

  /**
   * Returns an image that is the result of translating the image while keeping the same
   * view window. Eg, if translating by 10,5 then all pixels will move 10 to the right, and 5 down.
   * This would mean 10 columns and 5 rows of background added to the left and top.
   *
   * @return a new Image with this image translated.
   */
  def translate(x: Int, y: Int, background: Color = Color.White): this.type = {
    fill(background).overlay(this, x, y)
  }

  /**
   * Removes the given amount of pixels from each edge; like a crop operation.
   *
   * @param amount the number of pixels to trim from each edge
   *
   * @return a new Image with the dimensions width-trim*2, height-trim*2
   */
  def trim(amount: Int): this.type = trim(amount, amount, amount, amount)

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
  def trim(left: Int, top: Int, right: Int, bottom: Int): this.type = {
    blank(width - left - right, height - bottom - top).overlay(this, -left, -top)
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
  def zoom(factor: Double, method: ScaleMethod = ScaleMethod.Bicubic): this.type = {
    scale(factor, method).resizeTo(width, height)
  }

  def bytes(implicit writer: ImageWriter): Array[Byte] = forWriter(writer).bytes

  def output(path: String)(implicit writer: ImageWriter): Path = forWriter(writer).write(Paths.get(path))
  def output(file: File)(implicit writer: ImageWriter): File = forWriter(writer).write(file)
  def output(path: Path)(implicit writer: ImageWriter): Path = forWriter(writer).write(path)

  def forWriter(writer: ImageWriter): WriteContext = new WriteContext(writer, this)

  /**
   * Apply a sequence of filters in sequence.
   * This is sugar for image.filter(filter1).filter(filter2)....
   *
   * @param filters the sequence filters to apply
   * @return the result of applying each filter in turn
   */
  def filter(filters: Filter*): this.type = {
    filters.foldLeft(this)((image, filter) => image.filter(filter)).asInstanceOf[this.type]
  }

  override def toString: String = s"Image [width=$width, height=$height, type=${awt.getType}]"

  /**
   * Returns a copy of this image rotated 90 degrees anti-clockwise (counter clockwise to US English speakers).
   *
   * @return
   */
  def rotateLeft: this.type = wrapAwt(rotate(Math.PI / 2))

  /**
   * Returns a copy of this image rotated 90 degrees clockwise.
   *
   * @return
   */
  def rotateRight: this.type = wrapAwt(rotate(-Math.PI / 2))

  /**
   * Creates a copy of this image with the given filter applied.
   * The original (this) image is unchanged.
   *
   * @param filter the filter to apply. See com.sksamuel.scrimage.Filter.
   *
   * @return A new image with the given filter applied.
   */
  def filter(filter: Filter): this.type = {
    val target = copy
    filter.apply(target)
    target
  }

  def offset(x: Int, y: Int): Int = PixelTools.coordinateToOffset(x, y, width)

  /**
   * Returns a new Image with the brightness adjusted.
   */
  def brightness(factor: Double): this.type = {
    val target = copy
    target.rescale(factor)
    target
  }

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
    require(x >= 0)
    require(x + subWidth < width)
    require(y >= 0)
    require(y + subHeight < height)

    val matrix = Array.ofDim[Pixel](subWidth * subHeight)
    // Simply copy the pixels over, one by one.
    for ( yIndex <- 0 until subHeight; xIndex <- 0 until subWidth ) {
      matrix(PixelTools.coordinateToOffset(xIndex, yIndex, subWidth)) = Pixel(subpixel(xIndex + x, yIndex + y))
    }
    Image(subWidth, subHeight, matrix)
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
  def map(f: (Int, Int, Pixel) => Pixel): this.type = {
    val target = copy
    target.mapInPlace(f)
    target
  }
}