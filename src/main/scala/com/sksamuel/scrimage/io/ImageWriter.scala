package com.sksamuel.scrimage.io

import java.io.{OutputStream, File, ByteArrayOutputStream}
import org.apache.commons.io.FileUtils
import com.sksamuel.scrimage.Image

/** @author Stephen Samuel */
trait ImageWriter {

    def write(image: Image, out: OutputStream)
    def write(image: Image, bytes: Array[Byte]) {
        write(image, new ByteArrayOutputStream())
    }
    def write(image: Image, file: File) {
        write(image, FileUtils.openOutputStream(file))
    }
}