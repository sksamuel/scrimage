package com.sksamuel.scrimage.io

import java.io.OutputStream
import javax.imageio.ImageIO
import com.sksamuel.scrimage.Image
import org.apache.commons.io.IOUtils

/** @author Stephen Samuel */
class PngWriter extends ImageWriter {
    def write(image: Image, out: OutputStream) {
        ImageIO.write(image.awt, "png", out)
        IOUtils.closeQuietly(out)
    }
}