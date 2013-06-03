package com.sksamuel.scrimage

import com.sksamuel.scrimage.ScaleMethod.FastScale
import com.sksamuel.scrimage.Centering.Center
import java.awt.image.{BufferedImageOp, BufferedImage}
import java.awt.Graphics2D

/** @author Stephen Samuel
  *
  *         RichImage is class that represents an in memory image.
  *
  **/
class RichImage(image: BufferedImage) {

    val width: Int = image.getWidth(null)
    val height: Int = image.getHeight(null)
    val dimensions: (Int, Int) = (width, height)

    // applies a filter to this image and returns the result as a new image
    def filter(filter: Filter): RichImage = copy._filter(filter)

    // mutates the current image with the given filter
    def _filter(filter: Filter): RichImage = {
        image.getGraphics.asInstanceOf[Graphics2D].drawImage(c.image, filter.op, 0, 0)
        this
    }

    def copy = {
        val c = new BufferedImage(width, height, image.getType)
        c.getGraphics.drawImage(image, 0, 0)
        new RichImage(c)
    }

    // Returns a copy of the image which has been scaled to fit inside the current dimensions whilst keeping the aspect ratio.
    // That is the canvas will have the given dimensions but the image will not necessarily cover it all.
    def fit(dimension: (Int, Int), scaleMethod: ScaleMethod = FastScale, centering: Centering = Center): RichImage = copy

    def cover(dimension: (Int, Int), scaleMethod: ScaleMethod = FastScale): RichImage = copy

    // Scale will resize the canvas and the image. This is like a "image resize" in Photoshop
    def scale(dimension: (Int, Int), scaleMethod: ScaleMethod = FastScale): RichImage = copy

    // Resize will resize the canvas, it will not scale the image. This is like a "canvas resize" in Photoshop.
    // If the dimensions are smaller than the current canvas size then the image will be cropped.
    def resize(dimension: (Int, Int), centering: Centering = Center): RichImage = copy
}

sealed trait ScaleMethod
object ScaleMethod {
    object FastScale extends ScaleMethod
    object TripleChocolate extends ScaleMethod
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
    implicit def awt2rich(awtImage: BufferedImage) = new RichImage(awtImage)
}
