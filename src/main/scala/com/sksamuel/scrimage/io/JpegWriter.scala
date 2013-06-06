package com.sksamuel.scrimage.io

import com.sksamuel.scrimage.Image
import java.io.OutputStream
import javax.imageio.{ImageWriteParam, ImageIO}
import org.apache.commons.io.IOUtils

/** @author Stephen Samuel */
class JpegWriter(compression: Float) extends ImageWriter {

    def withCompression(compression: Float): JpegWriter = new JpegWriter(compression)

    def write(image: Image, out: OutputStream) {
        val writer = ImageIO.getImageWritersByFormatName("jpg").next()
        val params = writer.getDefaultWriteParam
        params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT)
        params.setCompressionQuality(compression)

        writer.setOutput(out)
        writer.write(image.awt)
        writer.dispose()
        IOUtils.closeQuietly(out)
    }
}