package com.sksamuel.scrimage.io

import java.io.OutputStream
import javax.imageio.{IIOImage, ImageWriteParam, ImageIO}
import org.apache.commons.io.IOUtils
import com.sksamuel.scrimage.Image
import javax.imageio.stream.MemoryCacheImageOutputStream

/** @author Stephen Samuel */
class TiffWriter(image: Image, compression: Boolean, compressionType: String = TiffWriter.COMPRESSION_LZW) extends ImageWriter {

    def withCompression(compression: Boolean): TiffWriter = new TiffWriter(image, compression, compressionType)
    def withCompressionType(compressionType: String): TiffWriter = new TiffWriter(image, compression, compressionType)

    def write(out: OutputStream) {

        val writer = ImageIO.getImageWritersByFormatName("tiff").next()

        val params = writer.getDefaultWriteParam

        if (compression) {
            params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT)
            params.setCompressionType(compressionType)
            params.setTilingMode(ImageWriteParam.MODE_EXPLICIT)
            params.setTiling(image.width, image.height, 0, 0)
        }

        val output = new MemoryCacheImageOutputStream(out)
        writer.setOutput(output)
        writer.write(null, new IIOImage(image.awt, null, null), params)
        writer.dispose()
        output.close()

        IOUtils.closeQuietly(out)
    }
}

object TiffWriter {
    val COMPRESSION_LZW = "LZW"
    val COMPRESSION_PACKBITS = "PackBits"
    def apply(image: Image) = new TiffWriter(image, false)
}