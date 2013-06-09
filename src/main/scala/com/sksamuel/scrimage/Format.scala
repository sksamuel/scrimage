package com.sksamuel.scrimage

import com.sksamuel.scrimage.io._
import com.sksamuel.scrimage.io.PngWriter

/** @author Stephen Samuel */
sealed trait Format[T <: ImageWriter] {
    /**
     * Returns a new ImageWriter for writing the given image
     * configured with the default configuration settings
     * for this image format type.
     *
     */
    def writer(image: Image): T
}
object Format {
    case object PNG extends Format[PngWriter] {
        def writer(image: Image) = PngWriter(image)
    }
    case object JPEG extends Format[JpegWriter] {
        def writer(image: Image) = JpegWriter(image)
    }
    case object GIF extends Format[GifWriter] {
        def writer(image: Image) = GifWriter(image)
    }
}