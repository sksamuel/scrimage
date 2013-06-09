package com.sksamuel.scrimage.io

import com.sksamuel.scrimage.Image
import java.io.OutputStream
import javax.imageio.{IIOImage, ImageWriteParam, ImageIO}
import org.apache.commons.io.IOUtils
import javax.imageio.stream.MemoryCacheImageOutputStream

/** @author Stephen Samuel */
class JpegWriter(image: Image, compression: Float, progressive: Boolean) extends ImageWriter {

    def withCompression(compression: Float): JpegWriter = {
        require(compression >= 0)
        require(compression <= 1)
        new JpegWriter(image, compression, progressive)
    }
    def withProgressive(progressive: Boolean): JpegWriter = new JpegWriter(image, compression, progressive)

    def write(out: OutputStream) {
        val writer = ImageIO.getImageWritersByFormatName("jpeg").next()
        val params = writer.getDefaultWriteParam
        params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT)
        params.setCompressionQuality(compression)
        if (progressive)
            params.setProgressiveMode(ImageWriteParam.MODE_DEFAULT)
        else
            params.setProgressiveMode(ImageWriteParam.MODE_DISABLED)

        val output = new MemoryCacheImageOutputStream(out)
        writer.setOutput(output)
        writer.write(null, new IIOImage(image.awt, null, null), params)
        writer.dispose()
        output.close()
        IOUtils.closeQuietly(out)
    }
}

object JpegWriter {
    def apply(image: Image) = new JpegWriter(image, 0.75f, false)
}