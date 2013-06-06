package com.sksamuel.scrimage.io

import com.sksamuel.scrimage.Image
import java.io.OutputStream
import javax.imageio.ImageIO
import org.apache.commons.io.IOUtils

/** @author Stephen Samuel */
class GifWriter extends GifWriter {
    def write(image: Image, out: OutputStream) {
        ImageIO.write(image.awt, "gif", out)
        IOUtils.closeQuietly(out)
    }
}