package com.sksamuel.scrimage

import java.awt.image.BufferedImage

import com.sksamuel.scrimage.ScaleMethod.{BSpline, Bicubic, Bilinear, FastScale, Lanczos3}
import thirdparty.mortennobel.{ResampleFilters, ResampleOp}

class ParImage(awt: BufferedImage, metadata: ImageMetadata) extends AbstractImage(awt, metadata) {

  override protected[scrimage] def wrapAwt(awt: BufferedImage, metadata: ImageMetadata): ParImage.this.type = {
    new ParImage(awt, metadata).asInstanceOf[ParImage.this.type]
  }

  override protected[scrimage] def wrapPixels(w: Int,
                                              h: Int,
                                              pixels: Array[Pixel],
                                              metadata: ImageMetadata): ParImage.this.type = {
    Image(w, h, pixels).withMetadata(metadata).asInstanceOf[ParImage.this.type]
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
  override def scaleTo(targetWidth: Int,
                       targetHeight: Int,
                       scaleMethod: ScaleMethod = Bicubic): ParImage.this.type = {
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
        super.op(new ResampleOp(method, targetWidth, targetHeight))
    }
  }
}
