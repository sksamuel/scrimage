package com.sksamuel.scrimage.io

import com.sksamuel.scrimage.Image
import java.io.OutputStream
import javax.imageio.{ImageWriteParam, ImageIO}
import org.apache.commons.io.IOUtils

/** @author Stephen Samuel */
class GifWriter(progressive: Boolean) extends ImageWriter {

    def withProgressive(progressive: Boolean): GifWriter = new GifWriter(progressive)

    def write(image: Image, out: OutputStream) {
        val writer = ImageIO.getImageWritersByFormatName("gif").next()
        val params = writer.getDefaultWriteParam
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

object GifWriter {
    def apply(): GifWriter = new GifWriter(false)
}