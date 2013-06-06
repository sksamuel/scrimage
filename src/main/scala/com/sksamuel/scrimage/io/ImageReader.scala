package com.sksamuel.scrimage.io

import java.io.{File, ByteArrayInputStream, InputStream}
import javax.imageio.ImageIO
import org.apache.commons.io.FileUtils
import com.sksamuel.scrimage.Image

/** @author Stephen Samuel */
trait ImageReader {
    def read(bytes: Array[Byte]): Image = read(new ByteArrayInputStream(bytes))
    def read(file: File): Image = read(FileUtils.openInputStream(file))
    def read(in: InputStream): Image
}

object GenericImageIOReader extends ImageReader {
    def read(in: InputStream): Image = Image(ImageIO.read(in))
}