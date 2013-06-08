package com.sksamuel.scrimage.io

import java.io.{ByteArrayInputStream, OutputStream}
import javax.imageio.ImageIO
import com.sksamuel.scrimage.Image
import thirdparty.pngtastic.{PngImage, PngOptimizer}
import org.apache.commons.io.output.ByteArrayOutputStream

/** @author Stephen Samuel */
class PngWriter extends ImageWriter {
    def write(image: Image, out: OutputStream) {

        // todo need to adapt pngtatsic to accept raw image
        val ba = new ByteArrayOutputStream()
        ImageIO.write(image.awt, "png", ba)
        val png = new PngImage(new ByteArrayInputStream(ba.toByteArray))

        val optimizer = new PngOptimizer()
        val optimized = optimizer.optimize(png)

        optimized.writeDataOutputStream(out)
    }
}