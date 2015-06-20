package com.sksamuel.scrimage.nio

import java.awt.image.{BufferedImage, ColorModel, DataBufferInt, Raster}
import java.io.ByteArrayInputStream

import ar.com.hjg.pngj.{ImageLineHelper, ImageLineInt}
import com.sksamuel.scrimage.{Image, Pixel}

object PngReader extends Reader {

  override def fromBytes(bytes: Array[Byte], `type`: Int = Image.CANONICAL_DATA_TYPE): Option[Image] = {

    if (supports(bytes)) {

      val pngr = new ar.com.hjg.pngj.PngReader(new ByteArrayInputStream(bytes))

      val channels = pngr.imgInfo.channels
      val bitDepth = pngr.imgInfo.bitDepth
      val w = pngr.imgInfo.cols
      val h = pngr.imgInfo.rows

      val matrix = Array.ofDim[Int](w * h)

      pngr.imgInfo.indexed match {
        case true =>
          for ( row <- 0 until h ) {
            val scanline = pngr.readRow().asInstanceOf[ImageLineInt]
            if (bitDepth < 8)
              ImageLineHelper.scaleUp(scanline)
            val pixels = ImageLineHelper.palette2rgb(scanline, pngr.getMetadata.getPLTE, null, null)
            val mapped = pixels.grouped(3).map { group =>
              Pixel(group(0), group(1), group(2), 255).toInt
            }.toArray
            System.arraycopy(mapped, 0, matrix, row * w, w)
          }
        case false =>
          for ( row <- 0 until h ) {
            val scanline: Array[Int] = pngr.readRow().asInstanceOf[ImageLineInt].getScanline
            val pixels = scanline.grouped(channels).map { group =>
              channels match {
                case 4 => Pixel(group.head, group(1), group(2), group(3)) // note: the png reader is n RGBA
                case 3 => Pixel(group.head, group(1), group(2), 255) // if no alpha then 255 is full opacity
                case 2 => Pixel(group.head, group.head, group.head, group(1))
                case 1 => Pixel(group.head, group.head, group.head, 255) // greyscale no alpha
              }
            }.map(_.toInt).toArray
            System.arraycopy(pixels, 0, matrix, row * w, w)
          }
      }
      pngr.end()

      val buffer = new DataBufferInt(matrix, matrix.length)
      val bandMasks = Array(0xFF0000, 0xFF00, 0xFF, 0xFF000000)
      val raster = Raster.createPackedRaster(buffer, w, h, w, bandMasks, null)

      val cm = ColorModel.getRGBdefault
      val image = new BufferedImage(cm, raster, cm.isAlphaPremultiplied, null)

      Option(new Image(image))

    } else {
      None
    }
  }

  def supports(bytes: Array[Byte]): Boolean = {
    val expected = List[Char](0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A).map(_.toByte)
    bytes.take(8).toList == expected
  }
}
