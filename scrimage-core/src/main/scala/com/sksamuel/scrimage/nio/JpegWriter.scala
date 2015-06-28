package com.sksamuel.scrimage.nio

import java.awt.image.BufferedImage
import java.io.OutputStream
import javax.imageio.metadata.IIOMetadataNode
import javax.imageio.stream.MemoryCacheImageOutputStream
import javax.imageio.{IIOImage, ImageIO, ImageTypeSpecifier, ImageWriteParam}

import com.sksamuel.scrimage.Image
import org.apache.commons.io.IOUtils
import org.w3c.dom.Element

case class JpegWriter(compression: Int, progressive: Boolean, writeMetadata: Boolean = true) extends ImageWriter {

  def withCompression(compression: Int): JpegWriter = {
    require(compression >= 0)
    require(compression <= 100)
    copy(compression = compression)
  }

  def withProgressive(progressive: Boolean): JpegWriter = copy(progressive = progressive)
  def withMetadata: JpegWriter = copy(writeMetadata = true)
  def withoutMetadata: JpegWriter = copy(writeMetadata = false)

  override def write(image: Image, out: OutputStream): Unit = {

    val writer = ImageIO.getImageWritersByFormatName("jpeg").next()
    val params = writer.getDefaultWriteParam
    val typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB)

    val metadata = writer.getDefaultImageMetadata(typeSpecifier, params)

    if (writeMetadata) {

      val tree = metadata.getAsTree("javax_imageio_jpeg_image_1.0").asInstanceOf[Element]
      for ( directory <- image.metadata.directories ) {
        val node = new IIOMetadataNode(directory.name)
        tree.appendChild(node)
        for (tag <- directory.tags) {
          node.setAttribute(tag.name, tag.rawValue)
        }
      }
    }

    if (compression < 100) {
      params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT)
      params.setCompressionQuality(compression / 100f)
    }

    if (progressive) {
      params.setProgressiveMode(ImageWriteParam.MODE_DEFAULT)
    } else {
      params.setProgressiveMode(ImageWriteParam.MODE_DISABLED)
    }

    // in openjdk, awt cannot write out jpegs that have a transparency bit, even if that is set to 255.
    // see http://stackoverflow.com/questions/464825/converting-transparent-gif-png-to-jpeg-using-java
    // so have to convert to a non alpha type
    val noAlpha = if (image.awt.getColorModel.hasAlpha) {
      image.removeTransparency(java.awt.Color.WHITE).toNewBufferedImage(BufferedImage.TYPE_INT_RGB)
    } else {
      image.awt
    }

    val output = new MemoryCacheImageOutputStream(out)
    writer.setOutput(output)
    writer.write(null, new IIOImage(noAlpha, null, metadata), params)
    output.close()
    writer.dispose()
    IOUtils.closeQuietly(out)
  }
}

object JpegWriter {
  val NoCompression = JpegWriter(100, false)
  val Default = JpegWriter(80, false)
  def apply(): JpegWriter = Default
}