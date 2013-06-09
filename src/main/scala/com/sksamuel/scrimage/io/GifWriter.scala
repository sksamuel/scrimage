package com.sksamuel.scrimage.io

import com.sksamuel.scrimage.Image
import java.io.OutputStream
import javax.imageio.{IIOImage, ImageWriteParam, ImageIO}
import org.apache.commons.io.IOUtils
import javax.imageio.stream.MemoryCacheImageOutputStream

/** @author Stephen Samuel */
class GifWriter(image: Image, progressive: Boolean) extends ImageWriter {

    def withProgressive(progressive: Boolean): GifWriter = new GifWriter(image: Image, progressive)

    def write(out: OutputStream) {
        val writer = ImageIO.getImageWritersByFormatName("gif").next()
        val params = writer.getDefaultWriteParam
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

object GifWriter {
    def apply(image: Image): GifWriter = new GifWriter(image, false)
}