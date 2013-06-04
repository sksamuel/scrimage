package com.sksamuel.scrimage

import java.awt.image.{DataBufferByte, AffineTransformOp, BufferedImageOp, BufferedImage}
import java.awt.Graphics2D
import com.sksamuel.scrimage.ScaleMethod._
import com.sksamuel.scrimage.Centering.Center
import com.mortennobel.imagescaling.{ResampleFilters, ResampleOp}
import java.awt.geom.AffineTransform

/** @author Stephen Samuel
  *
  *         RichImage is class that represents an in memory image.
  *
  * */
class Image(val awt: BufferedImage) {

    val SCALE_THREADS = 2

    lazy val width: Int = awt.getWidth(null)
    lazy val height: Int = awt.getHeight(null)
    lazy val dimensions: (Int, Int) = (width, height)
    lazy val ratio: Double = if (height == 0) 0 else width / height.toDouble

    /**
     * Returns the pixel data at the given point as an int consisting of the ARGB values.
     *
     * Inspired by user ryyst's code from
     * http://stackoverflow.com/questions/6524196/java-get-pixel-array-from-image
     * http://stackoverflow.com/users/282635/ryyst
     *
     * @param point the coordinates of the pixel to grab
     *
     * @return
     */
    def pixel(point: (Int, Int)): Int = {
        val pixels = awt.getRaster.getDataBuffer.asInstanceOf[DataBufferByte].getData
        val pixelIndex = point._1 * point._2 * 4
        // stored in the raster array as ABGR but we are dealing with ARGB consistently.
        (pixels(pixelIndex) & 0xff) << 24 + // alpha
          (pixels(pixelIndex + 1) & 0xff) + // blue
          (pixels(pixelIndex + 2) & 0xff) << 8 + // green
          (pixels(pixelIndex + 3) & 0xff) << 16 // red
    }

    /**
     *
     * Applies the given filter to this image and returns the modified image. Note, unlike most operations this operation
     * is mutable to the current image. If you wish to operate on a copy, then call copy() before filter, eg
     *
     * image.copy.filter(myFilter)
     *
     * @param filter the filter to apply. See Filter.
     *
     * @return This image after the filter has been applied.
     */
    def filter(filter: Filter): Image = {
        filter.apply(this)
        this
    }

    /**
     * Copies this image and returns a new image with a new backing array. Any operations to the copied image will
     * not affect the original. Images can be copied multiple times.
     *
     * This operation is useful when you want to apply a mutable operation without destroying the original image.
     *
     * @return A clone of this image.
     */
    def copy = Image.copy(awt)

    /**
     *
     * @return A new image that is the result of flipping this image horizontally.
     */
    def flipX: Image = {
        val tx = AffineTransform.getScaleInstance(-1, 1)
        tx.translate(-width, 0)
        val op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR)
        val flipped = op.filter(awt, null)
        new Image(flipped)
    }

    /**
     *
     * @return A new image that is the result of flipping this image vertically.
     */
    def flipY: Image = {
        val tx = AffineTransform.getScaleInstance(1, -1)
        tx.translate(0, -height)
        val op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR)
        val flipped = op.filter(awt, null)
        new Image(flipped)
    }

    def rotateLeft = _rotate(Math.PI)
    def rotateRight = _rotate(-Math.PI)

    def _rotate(angle: Double): Image = {
        val target = new BufferedImage(height, width, awt.getType)
        target.getGraphics.asInstanceOf[Graphics2D].rotate(angle)
        target.getGraphics.drawImage(awt, 0, 0, null)
        new Image(target)
    }

    /**
     * Returns a copy of the image which has been scaled to fit
     * inside the current dimensions whilst keeping the aspect ratio.
     *
     *
     *
     * @return
     */
    def fit(dimension: (Int, Int), scaleMethod: ScaleMethod = Bicubic, centering: Centering = Center): Image = copy

    /**
     *
     * That is the canvas will have the given dimensions but the image will not necessarily cover it all.
     *
     * @param dimension the target size
     * @param scaleMethod
     *
     * @return
     */
    def cover(dimension: (Int, Int), scaleMethod: ScaleMethod = Bicubic): Image = copy

    /**
     *
     * Scale will resize the canvas and the image. This is like a "image resize" in Photoshop
     *
     * @param dimensions the target size
     * @param scaleMethod the type of scaling method to use. Defaults to SmoothScale
     *
     * @return a new Image that is the result of scaling this image
     */
    def scale(dimensions: (Int, Int), scaleMethod: ScaleMethod = Bicubic): Image = {
        val op = new ResampleOp(dimensions._1, dimensions._2)
        op.setNumberOfThreads(SCALE_THREADS)
        scaleMethod match {
            case FastScale =>
            case Bicubic => op.setFilter(ResampleFilters.getBiCubicFilter)
            case Bilinear => op.setFilter(ResampleFilters.getTriangleFilter)
            case BSpline => op.setFilter(ResampleFilters.getBSplineFilter)
            case Lanczos3 => op.setFilter(ResampleFilters.getLanczos3Filter)
        }
        val scaled = op.filter(awt, null)
        new Image(scaled)
    }

    /**
     *
     * Resize will resize the canvas, it will not scale the image. This is like a "canvas resize" in Photoshop.
     * If the dimensions are smaller than the current canvas size then the image will be cropped.
     *
     * @param dimension the target size
     * @param centering
     *
     * @return a new Image that is the result of resizing the canvas.
     */
    def resize(dimension: (Int, Int), centering: Centering = Center): Image = {
        val target = new BufferedImage(dimension._1, dimension._2, awt.getType)
        target.getGraphics.asInstanceOf[Graphics2D].drawImage(awt, 0, 0, null)
        new Image(target)
    }
}

object Image {
    def apply(awt: BufferedImage) = new Image(awt)
    def apply(awt: java.awt.Image): Image = {
        awt match {
            case buff: BufferedImage => apply(buff)
            case _ => copy(awt)
        }
    }
    def copy(awt: java.awt.Image) = {
        val buff = new BufferedImage(awt.getWidth(null), awt.getHeight(null), BufferedImage.TYPE_INT_ARGB)
        buff.getGraphics.drawImage(awt, 0, 0, null)
        apply(buff)
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

sealed trait Centering
object Centering {
    object Center extends Centering
    object TopLeft extends Centering
    object TopRight extends Centering
    object Top extends Centering
    object Left extends Centering
    object Right extends Centering
    object Bottom extends Centering
    object BottomLeft extends Centering
    object BottomRight extends Centering
}

trait Filter {
    def apply(image: Image)
}

/**
 * Extension of Filter that applies its filters using a standard java BufferedImageOp
 */
trait BufferedOpFilter extends Filter {
    val op: BufferedImageOp
    def apply(image: Image) {
        image.awt.getGraphics.asInstanceOf[Graphics2D].drawImage(image.awt, op, 0, 0)
    }
}

object RichImage {
    implicit def awt2rich(awtImage: BufferedImage) = new Image(awtImage)
}


