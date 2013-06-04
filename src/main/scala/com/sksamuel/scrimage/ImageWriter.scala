package com.sksamuel.scrimage

import java.io.{ByteArrayOutputStream, OutputStream}
import javax.imageio.ImageIO

/** @author Stephen Samuel */
class ImageWriter(out: OutputStream) {
    def write(image: Image) {
        ImageIO.write(image.awt, "PNG", out)
    }
}

object ImageWriter {
    def apply(out: OutputStream) = new ImageWriter(out)
    def apply(bytes: Array[Byte]) = new ImageWriter(new ByteArrayOutputStream())
}