package com.sksamuel.scrimage.nio

import java.awt.image.{BufferedImage, ColorModel, DataBufferInt, Raster}
import java.io.InputStream

import ar.com.hjg.pngj.{ImageLineInt, PngReader}
import com.sksamuel.scrimage.{ARGBPixel, Image}

object PngImageReader extends ImageReader  {

  override def read(in: InputStream): Image = {

    val pngr = new PngReader(in)

    val channels = pngr.imgInfo.channels
    val bitDepth = pngr.imgInfo.bitDepth
    val w = pngr.imgInfo.cols
    val h = pngr.imgInfo.rows

    val matrix = Array.ofDim[Int](w * h)

    for ( row <- 0 until h ) {
      val scanline: Array[Int] = pngr.readRow().asInstanceOf[ImageLineInt].getScanline
      val pixels = scanline.grouped(channels).map { group =>
        channels match {
          case 4 => ARGBPixel(group(3), group.head, group(1), group(2)) // note: the png reader is n RGBA
          case x => throw new UnsupportedOperationException(s"PNG Reader does not support $x channels")
        }
      }.map(_.toInt).toArray
      System.arraycopy(pixels, 0, matrix, row * w, w)
    }
    pngr.end()

    val buffer = new DataBufferInt(matrix, matrix.length)
    val bandMasks = Array(0xFF0000, 0xFF00, 0xFF, 0xFF000000)
    val raster = Raster.createPackedRaster(buffer, w, h, w, bandMasks, null)

    val cm = ColorModel.getRGBdefault()
    val image = new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), null)

    new Image(image)
  }

  def supports(input: InputStream): Boolean = {

    val buff = Array.ofDim[Byte](8)
    input.read(buff)
    val expected = List[Char](0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A).map(_.toByte)

    buff.toList == expected
  }
}
