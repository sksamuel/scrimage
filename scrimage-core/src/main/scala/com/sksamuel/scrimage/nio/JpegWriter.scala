package com.sksamuel.scrimage.nio

import java.io.OutputStream
import javax.imageio.stream.MemoryCacheImageOutputStream
import javax.imageio.{IIOImage, ImageIO, ImageWriteParam}

import com.sksamuel.scrimage.Image
import org.apache.commons.io.IOUtils

case class JpegWriter(compression: Int, progressive: Boolean) extends ImageWriter {

  def withCompression(compression: Int): JpegWriter = {
    require(compression >= 0)
    require(compression <= 100)
    copy(compression = compression)
  }

  def withProgressive(progressive: Boolean): JpegWriter = copy(progressive = progressive)

  override def write(image: Image, out: OutputStream): Unit = {

    val writer = ImageIO.getImageWritersByFormatName("jpeg").next()
    val params = writer.getDefaultWriteParam
    if (compression < 100) {
      params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT)
      params.setCompressionQuality(compression / 100f)
    }
    if (progressive) {
      params.setProgressiveMode(ImageWriteParam.MODE_DEFAULT)
    } else {
      params.setProgressiveMode(ImageWriteParam.MODE_DISABLED)
    }

    // jpegs cannot write out transparency. The java version will break
    // see http://stackoverflow.com/questions/464825/converting-transparent-gif-png-to-jpeg-using-java
    // so have to remove alpha
    val noAlpha = image.removeTransparency(java.awt.Color.WHITE)

    val output = new MemoryCacheImageOutputStream(out)
    writer.setOutput(output)
    writer.write(null, new IIOImage(noAlpha.toNewBufferedImage, null, null), params)
    writer.dispose()
    output.close()
    IOUtils.closeQuietly(out)
  }
}

object JpegWriter {
  val NoCompression = JpegWriter(100, false)
  def apply(): JpegWriter = JpegWriter(80, false)
}