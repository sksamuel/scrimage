package com.sksamuel.scrimage.io

import java.awt.image.{BufferedImage, Raster, ColorModel, DataBufferInt}
import java.io.InputStream

import ar.com.hjg.pngj.{ImageLineInt, PngReader}
import com.sksamuel.scrimage.Format.PNG
import com.sksamuel.scrimage.{Image, Format, MimeTypeChecker}

object PNGReader extends ImageReader with MimeTypeChecker {

  def read(in: InputStream): Image = {

    val pngr = new PngReader(in)

    val channels = pngr.imgInfo.channels
    val bitDepth = pngr.imgInfo.bitDepth
    val w = pngr.imgInfo.cols
    val h = pngr.imgInfo.rows

    val rowSize = w * channels
    val matrix = Array.ofDim[Int](w * h)

    if (bitDepth <= 8) {
      for ( row <- 0 until h ) {
        // also: while(pngr.hasMoreRows())
        val scanline: Array[Byte] = pngr.readRow().asInstanceOf[ImageLineInt].getScanline.map(_.toByte)
        System.arraycopy(scanline, 0, matrix, row * rowSize, rowSize)
      }
    } else {
      for ( row <- 0 until h ) {
        // also: while(pngr.hasMoreRows())
        val scanline: Array[Int] = pngr.readRow().asInstanceOf[ImageLineInt].getScanline
        System.arraycopy(scanline, 0, matrix, row * rowSize, rowSize)
      }
    }
    pngr.end()

    val buffer = new DataBufferInt(matrix, matrix.length)
    val bandMasks = Array(0xFF0000, 0xFF00, 0xFF, 0xFF000000)
    val raster = Raster.createPackedRaster(buffer, w, h, w, bandMasks, null)

    val cm = ColorModel.getRGBdefault()
    val image = new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), null)

    new Image(image)
  }

  def readMimeType(input: InputStream): Option[PNG.type] = {

    val buff = Array.ofDim[Byte](8)
    input.read(buff)
    val expected = List[Char](0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A).map(_.toByte)

    if (buff.toList == expected) Some(Format.PNG)
    else None
  }
}
