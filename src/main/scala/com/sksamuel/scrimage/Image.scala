package com.sksamuel.scrimage

import java.awt.image.{BufferedImageOp, BufferedImage}
import java.awt.Graphics2D
import com.sksamuel.scrimage.ScaleMethod.SmoothScale
import com.sksamuel.scrimage.Centering.Center

/** @author Stephen Samuel
  *
  *         RichImage is class that represents an in memory image.
  *
  * */
class Image(image: BufferedImage) {

    val width: Int = image.getWidth(null)
    val height: Int = image.getHeight(null)
    val dimensions: (Int, Int) = (width, height)

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
        image.getGraphics.asInstanceOf[Graphics2D].drawImage(c.image, filter.op, 0, 0)
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
    def copy = {
        val c = new BufferedImage(width, height, image.getType)
        c.getGraphics.drawImage(image, 0, 0)
        new Image(c)
    }

    def flipX = this
    def flipY = this
    def rotateLeft = this
    def rotateRight = this
    def rotate(angle: Float) = this

    /**
     * Returns a copy of the image which has been scaled to fit
     * inside the current dimensions whilst keeping the aspect ratio.
     *
     *
     *
     * @return
     */
    def fit(dimension: (Int, Int), scaleMethod: ScaleMethod = SmoothScale, centering: Centering = Center): Image = copy

    /**
     *
     * That is the canvas will have the given dimensions but the image will not necessarily cover it all.
     *
     * @param dimension
     * @param scaleMethod
     *
     * @return
     */
    def cover(dimension: (Int, Int), scaleMethod: ScaleMethod = SmoothScale): Image = copy

    /**
     *
     * Scale will resize the canvas and the image. This is like a "image resize" in Photoshop
     *
     * @param dimension
     * @param scaleMethod the type of scaling method to use. Defaults to SmoothScale
     *
     * @return a new Image that is the result of scaling this image
     */
    def scale(dimension: (Int, Int), scaleMethod: ScaleMethod = SmoothScale): Image = {
        val target = ImageTools.resize(image, dimension)
        new Image(target)
    }

    /**
     *
     * Resize will resize the canvas, it will not scale the image. This is like a "canvas resize" in Photoshop.
     * If the dimensions are smaller than the current canvas size then the image will be cropped.
     * @param dimension
     * @param centering
     *
     * @return a new Image that is the result of resizing the canvas.
     */
    def resize(dimension: (Int, Int), centering: Centering = Center): Image = {
        val target = new BufferedImage(dimension._1, dimension._2, image.getType)
        target.getGraphics.asInstanceOf[Graphics2D].drawImage(image, 0, 0, null)
        new Image(target)
    }
}

sealed trait ScaleMethod
object ScaleMethod {
    object FastScale extends ScaleMethod
    object SmoothScale extends ScaleMethod
    object ReplicateScale extends ScaleMethod
    object AreaAveragingScale extends ScaleMethod
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
    val op: BufferedImageOp
}

object RichImage {
    implicit def awt2rich(awtImage: BufferedImage) = new Image(awtImage)
}
