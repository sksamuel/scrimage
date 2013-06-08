package com.sksamuel.scrimage.io

import com.sksamuel.scrimage.Image
import java.io.OutputStream
import javax.imageio.{IIOImage, ImageWriteParam, ImageIO}
import org.apache.commons.io.IOUtils

/** @author Stephen Samuel */
class TiffWriter(compression: Boolean, compressionType: String = TiffWriter.COMPRESSION_LZW) extends ImageWriter {

    def withCompression(compression: Boolean): TiffWriter = new TiffWriter(compression, compressionType)
    def withCompressionType(compressionType: String): TiffWriter = new TiffWriter(compression, compressionType)

    def write(image: Image, out: OutputStream) {

        val writer = ImageIO.getImageWritersByFormatName("tiff").next()

        val params = writer.getDefaultWriteParam

        if (compression) {
            params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT)
            params.setCompressionType(compressionType)
            params.setTilingMode(ImageWriteParam.MODE_EXPLICIT)
            params.setTiling(image.width, image.height, 0, 0)
        }

        writer.setOutput(out)
        writer.write(null, new IIOImage(image.awt, null, null), params)
        writer.dispose()

        IOUtils.closeQuietly(out)
    }
}

object TiffWriter {
    val COMPRESSION_LZW = "LZW"
    val COMPRESSION_PACKBITS = "PackBits"
    def apply() = new TiffWriter(false)
}