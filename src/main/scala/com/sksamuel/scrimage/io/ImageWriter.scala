package com.sksamuel.scrimage.io

import java.io.{OutputStream, File, ByteArrayOutputStream}
import org.apache.commons.io.FileUtils

/** @author Stephen Samuel */
trait ImageWriter {

    def write(out: OutputStream)

    def write(path: String) {
        write(new File(path))
    }
    def write(bytes: Array[Byte]) {
        write(new ByteArrayOutputStream())
    }
    def write(file: File) {
        write(FileUtils.openOutputStream(file))
    }
}