package com.sksamuel.scrimage.io

import com.sksamuel.scrimage.Image
import java.io.OutputStream
import javax.imageio.{ImageWriteParam, ImageIO}
import org.apache.commons.io.IOUtils

/** @author Stephen Samuel */
class JpegWriter(compression: Float, progressive: Boolean) extends ImageWriter {

    def withCompression(compression: Float): JpegWriter = new JpegWriter(compression, progressive)
    def withProgressive(progressive: Boolean): JpegWriter = new JpegWriter(compression, progressive)

    def write(image: Image, out: OutputStream) {
        val writer = ImageIO.getImageWritersByFormatName("jpg").next()
        val params = writer.getDefaultWriteParam
        params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT)
        params.setCompressionQuality(compression)
        if (progressive)
            params.setProgressiveMode(ImageWriteParam.MODE_DEFAULT)
        else
            params.setProgressiveMode(ImageWriteParam.MODE_DISABLED)

        writer.setOutput(out)
        writer.write(image.awt)
        writer.dispose()
        IOUtils.closeQuietly(out)
    }
}

object JpegWriter {
    def apply() = new JpegWriter(0.75f, false)
}