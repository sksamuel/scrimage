package com.sksamuel.scrimage

import java.io.{ByteArrayOutputStream, OutputStream}
import javax.imageio.ImageIO
import com.sksamuel.scrimage.Format.PNG

/** @author Stephen Samuel */
class ImageWriter(out: OutputStream) {
    def write(image: Image, format: Format = PNG) {
        ImageIO.write(image.awt, format.getClass.getSimpleName, out)
    }
}

object ImageWriter {
    def apply(out: OutputStream) = new ImageWriter(out)
    def apply(bytes: Array[Byte]) = new ImageWriter(new ByteArrayOutputStream())
}