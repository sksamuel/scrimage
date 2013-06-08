package com.sksamuel.scrimage

import com.sksamuel.scrimage.io._

/** @author Stephen Samuel */
sealed trait Format[T <: ImageWriter] {
    def reader: ImageReader = GenericImageIOReader

    /**
     * Returns a new defaultly configured writer compatible with the format type.
     *
     */
    def writer: T
}
object Format {
    case object PNG extends Format[PngWriter] {
        def writer = new PngWriter
    }
    case object JPEG extends Format[JpegWriter] {
        def writer = JpegWriter()
    }
    case object GIF extends Format[GifWriter] {
        def writer = GifWriter()
    }
    case object TIFF extends Format[TiffWriter] {
        def writer = TiffWriter()
    }
}