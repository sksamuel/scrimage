package com.sksamuel.scrimage

import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.io.{InputStream, OutputStream, File}
import com.sksamuel.scrimage.Format.PNG
import com.sksamuel.scrimage.ScaleMethod._
import javax.imageio.ImageIO
import org.apache.commons.io.FileUtils
import java.awt.image.{DataBufferInt, AffineTransformOp, BufferedImage}
import com.sksamuel.scrimage.Color.White
import thirdparty.mortennobel.{ResampleFilters, ResampleOp}
import com.sksamuel.scrimage.Position.Center
import com.sksamuel.scrimage.io.ImageWriter

/** @author Stephen Samuel
  *
  *         RichImage is class that represents an in memory image.
  *
  * */
class Image(val awt: BufferedImage) {
    require(awt != null, "Wrapping image cannot be null")
    require(awt.getType == Image.CANONICAL_DATA_TYPE,
        "Unsupported underlying image type. Consider using Image.apply(java.awt.Image) in order to wrap the image in the right data type")

    lazy val width: Int = awt.getWidth(null)
    lazy val height: Int = awt.getHeight(null)
    lazy val dimensions: (Int, Int) = (width, height)
    lazy val ratio: Double = if (height == 0) 0 else width / height.toDouble

    /**
     * Creates an empty Image with the same dimensions of this image.
     *
     * @return a new Image that is a clone of this image but with uninitialized data
     */
    def empty: Image = Image.empty(width, height)

    /**
     * Creates a new image with the same data as this image.
     * Any operations to the copied image will not write back to the original.
     * Images can be copied multiple times as well as copies copied etc.
     *
     * @return A copy of this image.
     */
    def copy = Image._copy(awt)

    /**
     * @param point the coordinates of the pixel to grab
     *
     * @return
     */
    def pixel(point: (Int, Int)): Int = {
        val pixels = awt.getRaster.getDataBuffer.asInstanceOf[DataBufferInt].getData
        val pixelIndex = (width * point._2 + point._1)
        pixels(pixelIndex)
    }

    /**
     * Returns the pixels of this image represented as integers
     * in a single dimension array.
     *
     * @return
     */
    def pixels: Array[Int] = {
        awt.getRaster.getDataBuffer match {
            case buffer: DataBufferInt => buffer.getData
            case _ => throw new UnsupportedOperationException
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
     * @return A new image that is the result of flipping this image horizontally.
     */
    def flipX: Image = {
        val tx = AffineTransform.getScaleInstance(-1, 1)
        tx.translate(-width, 0)
        _flip(tx)
    }

    /**
     *
     * @return A new image that is the result of flipping this image vertically.
     */
    def flipY: Image = {
        val tx = AffineTransform.getScaleInstance(1, -1)
        tx.translate(0, -height)
        _flip(tx)
    }

    def _flip(tx: AffineTransform): Image = {
        val op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR)
        val flipped = op.filter(awt, null)
        new Image(flipped)
    }

    /**
     * Returns a copy of this image rotated 90 degrees anti-clockwise (counter clockwise to US English speakers).
     *
     * @return
     */
    def rotateLeft = _rotate(Math.PI)

    /**
     * Returns a copy of this image rotated 90 degrees clockwise.
     *
     * @return
     */
    def rotateRight = _rotate(-Math.PI)

    def _rotate(angle: Double): Image = {
        val target = new BufferedImage(height, width, awt.getType)
        val g2 = target.getGraphics.asInstanceOf[Graphics2D]
        g2.rotate(angle)
        g2.drawImage(awt, 0, 0, null)
        g2.dispose()
        new Image(target)
    }

    /**
     *
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
            color: com.sksamuel.scrimage.Color = White,
            scaleMethod: ScaleMethod = Bicubic,
            position: Position = Center): Image = {
        val fittedDimensions = ImageTools.dimensionsToFit((targetWidth, targetHeight), (width, height))
        val scaled = scale(fittedDimensions._1, fittedDimensions._2, scaleMethod)
        val target = Image.filled(targetWidth, targetHeight, color)
        val g2 = target.awt.getGraphics.asInstanceOf[Graphics2D]
        val x = ((targetWidth - fittedDimensions._1) / 2.0).toInt
        val y = ((targetHeight - fittedDimensions._2) / 2.0).toInt
        g2.drawImage(scaled.awt, x, y, null)
        g2.dispose()
        target
    }

    /**
     *
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
    def cover(targetWidth: Int, targetHeight: Int, scaleMethod: ScaleMethod = Bicubic, position: Position = Center): Image = {
        val coveredDimensions = ImageTools.dimensionsToCover((targetWidth, targetHeight), (width, height))
        val scaled = scale(coveredDimensions._1, coveredDimensions._2, scaleMethod)
        val target = Image.empty(targetWidth, targetHeight)
        val g2 = target.awt.getGraphics.asInstanceOf[Graphics2D]
        val x = ((targetWidth - coveredDimensions._1) / 2.0).toInt
        val y = ((targetHeight - coveredDimensions._2) / 2.0).toInt
        g2.drawImage(scaled.awt, x, y, null)
        g2.dispose()
        target
    }

    /**
     *
     * Scale will resize the canvas and scale the image to match.
     * This is like a "image resize" in Photoshop.
     *
     * This overloaded version of scale will scale the image so that the new image
     * has a width that matches the given targetWidth
     * and the same aspect ratio as the original.
     *
     * Eg, an image of 200,300 with a scaleToWidth of 400 will result
     * in a scaled image of 400,600
     *
     * @param targetWidth the target width
     * @param scaleMethod the type of scaling method to use.
     *
     * @return a new Image that is the result of scaling this image
     */
    def scaleToWidth(targetWidth: Int, scaleMethod: ScaleMethod = Bicubic): Image =
        scale(targetWidth, ((targetWidth / width.toDouble) * height).toInt, Bicubic)

    /**
     *
     * Scale will resize the canvas and scale the image to match.
     * This is like a "image resize" in Photoshop.
     *
     * This overloaded version of scale will scale the image so that the new image
     * has a height that matches the given targetHeight
     * and the same aspect ratio as the original.
     *
     * Eg, an image of 200,300 with a scaleToHeight of 450 will result
     * in a scaled image of 300,450
     *
     * @param targetHeight the target height
     * @param scaleMethod the type of scaling method to use.
     *
     * @return a new Image that is the result of scaling this image
     */
    def scaleToHeight(targetHeight: Int, scaleMethod: ScaleMethod = Bicubic): Image =
        scale(((targetHeight / height.toDouble) * width).toInt, targetHeight, Bicubic)

    def scale(scaleFactor: Double): Image = scale(scaleFactor, Bicubic)

    /**
     *
     * Scale will resize the canvas and the image.
     * This is like a "image resize" in Photoshop.
     *
     * @param scaleFactor the target increase or decrease. 1 is the same as original.
     * @param scaleMethod the type of scaling method to use.
     *
     * @return a new Image that is the result of scaling this image
     */
    def scale(scaleFactor: Double, scaleMethod: ScaleMethod): Image =
        scale((width * scaleFactor).toInt, (height * scaleFactor).toInt, scaleMethod)

    val SCALE_THREADS = 2

    /**
     *
     * Scale will resize the canvas and the image.
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
    def scale(targetWidth: Int, targetHeight: Int, scaleMethod: ScaleMethod = Bicubic): Image = {
        val op = new ResampleOp(targetWidth, targetHeight)
        op.setNumberOfThreads(SCALE_THREADS)
        scaleMethod match {
            case FastScale =>
            case Bicubic => op.setFilter(ResampleFilters.getBiCubicFilter)
            case Bilinear => op.setFilter(ResampleFilters.getTriangleFilter)
            case BSpline => op.setFilter(ResampleFilters.getBSplineFilter)
            case Lanczos3 => op.setFilter(ResampleFilters.getLanczos3Filter)
        }
        val scaled = op.filter(awt, null)
        Image(scaled)
    }

    /**
     *
     * Resize will resize the canvas, it will not scale the image.
     * This is like a "canvas resize" in Photoshop.
     *
     * This overloaded version of resize will resize by a scale factor.
     *
     * @param scaleFactor the scaleFactor. 1 retains original size. 0.5 is half. 2 double. etc
     *
     * @return a new Image that is the result of resizing the canvas.
     */
    def resize(scaleFactor: Double): Image = resize(scaleFactor, Center)

    /**
     *
     * Resize will resize the canvas, it will not scale the image.
     * This is like a "canvas resize" in Photoshop.
     *
     * This overloaded version of resize will resize by a scale factor.
     *
     * @param scaleFactor the scaleFactor. 1 retains original size. 0.5 is half. 2 double. etc
     * @param position where to position the original image after the canvas size change
     *
     * @return a new Image that is the result of resizing the canvas.
     */
    def resize(scaleFactor: Double, position: Position): Image =
        resize((width * scaleFactor).toInt, (height * scaleFactor).toInt, position)

    /**
     *
     * Resize will resize the canvas, it will not scale the image.
     * This is like a "canvas resize" in Photoshop.
     *
     * If the dimensions are smaller than the current canvas size
     * then the image will be cropped.
     *
     * @param targetWidth the target width
     * @param targetHeight the target height
     * @param position where to position the original image after the canvas size change
     *
     * @return a new Image that is the result of resizing the canvas.
     */
    def resize(targetWidth: Int, targetHeight: Int, position: Position = Center): Image = {
        val target = new BufferedImage(targetWidth, targetHeight, Image.CANONICAL_DATA_TYPE)
        target.getGraphics.asInstanceOf[Graphics2D].drawImage(awt, 0, 0, null)
        new Image(target)
    }

    /**
     *
     * Creates a new image which is the result of this image
     * padded with the given number of pixels on each edge.
     *
     * Eg, requesting a pad of 30 on an image of 250,300 will result
     * in a new image with a canvas size of 310,360

     * @param size the number of pixels to add on each edge
     * @param color the background of the padded area.
     *
     * @return A new image that is the result of the padding
     */
    def pad(size: Int, color: com.sksamuel.scrimage.Color): Image = pad(width + size * 2, height + size * 2, color)

    /**
     *
     * Creates a new image which is the result of this image padded to the canvas size specified.
     * If this image is already larger than the specified pad then the sizes of the existing
     * image will be used instead.
     *
     * Eg, requesting a pad of 200,200 on an image of 250,300 will result
     * in keeping the 250,300.
     *
     * Eg2, requesting a pad of 300,300 on an image of 400,250 will result
     * in the width staying at 400 and the height padded to 300.

     * @param targetWidth the size of the output canvas width
     * @param targetHeight the size of the output canvas height
     * @param color the background of the padded area.
     *
     * @return A new image that is the result of the padding
     */
    def pad(targetWidth: Int, targetHeight: Int, color: com.sksamuel.scrimage.Color = Color.White): Image = {
        val w = if (width < targetWidth) targetWidth else width
        val h = if (height < targetHeight) targetHeight else height
        val filled = Image.filled(w, h, color)
        val g = filled.awt.getGraphics
        val x = ((w - width) / 2.0).toInt
        val y = ((h - height) / 2.0).toInt
        g.drawImage(awt, x, y, null)
        g.dispose()
        filled
    }

    /**
     * Creates a new Image with the same dimensions of this image and with
     * all the pixels initialized to the given color
     *
     * @return a new Image with the same dimensions as this
     */
    def filled(color: com.sksamuel.scrimage.Color): Image = filled(color.value)

    /**
     * Creates a new Image with the same dimensions of this image and with
     * all the pixels initialized to the given color
     *
     * @return a new Image with the same dimensions as this
     */
    def filled(color: Int): Image = filled(new java.awt.Color(color))

    /**
     * Creates a new Image with the same dimensions of this image and with
     * all the pixels initialized to the given color
     *
     * @return a new Image with the same dimensions as this
     */
    def filled(color: java.awt.Color): Image = Image.filled(width, height, color)

    def writer[T <: ImageWriter](format: Format[T]): T = format.writer

    def write(file: File) {
        write(file, Format.PNG)
    }
    def write(file: File, format: Format[_ <: ImageWriter]) {
        write(file, format)
    }
    def write(out: OutputStream) {
        write(out, PNG)
    }
    def write(out: OutputStream, format: Format[_ <: ImageWriter]) {
        writer(format).write(this, out)
    }

    override def hashCode(): Int = awt.hashCode
    override def equals(obj: Any): Boolean = obj match {
        case other: Image => other.pixels.sameElements(pixels)
        case _ => false
    }

    // -- mutable operations -- //

    def _fill(color: java.awt.Color): Image = {
        val g2 = awt.getGraphics
        g2.setColor(color)
        g2.fillRect(0, 0, awt.getWidth, awt.getHeight)
        g2.dispose()
        this
    }

    /**
     * Creates a MutableImage instance backed by this image.
     *
     * Note, any changes to the mutable image write back to this Image.
     * If you want a mutable copy then you must first copy this image
     * before invoking this operation.
     *
     * @return
     */
    def toMutable: MutableImage = new MutableImage(awt)
}

object Image {

    val CANONICAL_DATA_TYPE = BufferedImage.TYPE_INT_ARGB

    def apply(in: InputStream): Image = {
        require(in != null)
        require(in.available > 0)
        apply(ImageIO.read(in))
    }

    def apply(file: File): Image = {
        require(file != null)
        val in = FileUtils.openInputStream(file)
        apply(in)
    }

    def apply(awt: java.awt.Image): Image = {
        require(awt != null, "Input image cannot be null")
        awt match {
            case buff: BufferedImage if buff.getType == CANONICAL_DATA_TYPE => new Image(buff)
            case _ => _copy(awt)
        }
    }

    /**
     * Creates a new Image which is a copy of the given image.
     * Any operations to the new object do not affect the original image.
     *
     * @param image the image to copy
     *
     * @return a new Image object.
     */
    def apply(image: Image): Image = _copy(image.awt)

    private[scrimage] def _empty(awt: java.awt.Image): BufferedImage = _empty(awt.getWidth(null), awt.getHeight(null))
    private[scrimage] def _empty(width: Int, height: Int): BufferedImage = new BufferedImage(width, height, CANONICAL_DATA_TYPE)

    private[scrimage] def _copy(awt: java.awt.Image) = {
        require(awt != null, "Input image cannot be null")
        val copy = _empty(awt)
        val g2 = copy.getGraphics
        g2.drawImage(awt, 0, 0, null)
        g2.dispose()
        new Image(copy)
    }

    def filled(width: Int, height: Int, color: com.sksamuel.scrimage.Color): Image = filled(width, height, color.value)
    def filled(width: Int, height: Int, color: Int): Image = filled(width, height, new java.awt.Color(color))
    def filled(width: Int, height: Int, color: java.awt.Color): Image = {
        val awt = new BufferedImage(width, height, Image.CANONICAL_DATA_TYPE)
        val filled = new Image(awt)
        filled._fill(color)
        filled
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
    def empty(width: Int, height: Int): Image = new Image(new BufferedImage(width, height, CANONICAL_DATA_TYPE))
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


