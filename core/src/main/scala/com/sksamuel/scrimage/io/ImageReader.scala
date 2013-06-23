package com.sksamuel.scrimage.io

import java.io.InputStream
import com.sksamuel.scrimage.Image

/** @author Stephen Samuel */
trait ImageReader {
    def read(in: InputStream): Image
}
