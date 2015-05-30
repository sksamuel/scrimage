package com.sksamuel.scrimage.nio

import java.io.OutputStream
import javax.imageio.{ IIOImage, ImageIO }

import com.sksamuel.scrimage.Image
import org.apache.commons.io.IOUtils

trait TwelveMonkeysWriter extends ImageWriter {

  def format: String

  override def write(image: Image, out: OutputStream): Unit = {

    import scala.collection.JavaConverters._

    val writers = ImageIO.getImageWritersByFormatName(format).asScala.toList
    writers match {
      case writer :: tail =>
        val ios = ImageIO.createImageOutputStream(out)
        val params = writer.getDefaultWriteParam()
        writer.setOutput(ios)
        writer.write(null, new IIOImage(image.awt, null, null), params)
        ios.close()
        writer.dispose()
        IOUtils.closeQuietly(out)
      case _ => throw new UnsupportedOperationException(s"$format format not registered")
    }
  }
}
