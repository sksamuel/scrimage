package com.sksamuel.scrimage

import java.io.{ByteArrayInputStream, InputStream}
import javax.imageio.ImageIO

/** @author Stephen Samuel */
class ImageReader(in: InputStream) {
    def read = ImageIO.read(in)
}

object ImageReader {
    def apply(in: InputStream) = new ImageReader(in)
    def apply(bytes: Array[Byte]) = new ImageReader(new ByteArrayInputStream(bytes))
}
