package com.sksamuel.scrimage

import java.io.{File, ByteArrayInputStream, InputStream}
import javax.imageio.ImageIO
import org.apache.commons.io.FileUtils

/** @author Stephen Samuel */
class ImageReader(in: InputStream) {
    require(in != null, "Input stream to reader must not be null")
    def read: Image = {
        require(in.available > 0)
        Image(ImageIO.read(in))
    }
}

object ImageReader {
    def apply(file: File) = new ImageReader(FileUtils.openInputStream(file))
    def apply(in: InputStream) = new ImageReader(in)
    def apply(bytes: Array[Byte]) = new ImageReader(new ByteArrayInputStream(bytes))
}
