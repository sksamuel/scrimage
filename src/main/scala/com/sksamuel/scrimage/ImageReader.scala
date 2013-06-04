package com.sksamuel.scrimage

import java.io.{File, ByteArrayInputStream, InputStream}
import javax.imageio.ImageIO
import org.apache.commons.io.FileUtils

/** @author Stephen Samuel */
class ImageReader(in: InputStream) {
    def read: Image = new Image(ImageIO.read(in))
}

object ImageReader {
    def apply(file: File) = new ImageReader(FileUtils.openInputStream(file))
    def apply(in: InputStream) = new ImageReader(in)
    def apply(bytes: Array[Byte]) = new ImageReader(new ByteArrayInputStream(bytes))
}
