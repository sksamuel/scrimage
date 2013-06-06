package com.sksamuel.scrimage.io

import com.sksamuel.scrimage.Image
import java.io.OutputStream
import javax.imageio.ImageIO
import org.apache.commons.io.IOUtils

/** @author Stephen Samuel */
trait TiffWriter extends ImageWriter
object TiffWriter extends TiffWriter {
    def write(image: Image, out: OutputStream) {
        ImageIO.write(image.awt, "tiff", out)
        IOUtils.closeQuietly(out)
    }
}