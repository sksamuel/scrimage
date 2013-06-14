package com.sksamuel.scrimage.io

import java.io.InputStream
import javax.imageio.ImageIO
import com.sksamuel.scrimage.Image
import org.apache.sanselan.Sanselan

/** @author Stephen Samuel */
trait JavaImageIOReader extends ImageReader {
    def read(in: InputStream): Image = Image(ImageIO.read(in))
}

trait SanselanReader extends ImageReader {
    def read(in: InputStream): Image = Image(Sanselan.getBufferedImage(in))
}