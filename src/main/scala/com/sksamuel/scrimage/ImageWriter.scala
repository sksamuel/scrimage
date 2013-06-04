package com.sksamuel.scrimage

import java.io.{File, ByteArrayOutputStream, OutputStream}
import javax.imageio.ImageIO
import com.sksamuel.scrimage.Format.PNG
import org.apache.commons.io.{IOUtils, FileUtils}

/** @author Stephen Samuel */
class ImageWriter(out: OutputStream) {
    def write(image: Image, format: Format = PNG) {
        ImageIO.write(image.awt, "png", out)
        out.flush()
        IOUtils.closeQuietly(out)
    }
}

object ImageWriter {
    def apply(file: File) = new ImageWriter(FileUtils.openOutputStream(file))
    def apply(out: OutputStream) = new ImageWriter(out)
    def apply(bytes: Array[Byte]) = new ImageWriter(new ByteArrayOutputStream())
}