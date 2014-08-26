/*
   Copyright 2013 Stephen K Samuel

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.sksamuel.scrimage.io

import java.io.InputStream
import javax.imageio.ImageIO
import com.sksamuel.scrimage.{ Image, Raster }
import org.apache.sanselan.Sanselan
import ar.com.hjg.pngj.{ PngReader, ImageInfo, ImageLineInt, ImageLineByte, IImageLine }

/** @author Stephen Samuel */
trait JavaImageIOReader extends ImageReader {
  def read(in: InputStream): Image = Image(ImageIO.read(in))
}

trait SanselanReader extends ImageReader {
  def read(in: InputStream): Image = Image(Sanselan.getBufferedImage(in))
}

object PNGReader extends ImageReader with MimeTypeChecker {
  def read(in: InputStream): Image = {
    val pngr = new PngReader(in)
    println(pngr.toString)
    val channels = pngr.imgInfo.channels
    val bitDepth = pngr.imgInfo.bitDepth
    val width = pngr.imgInfo.cols
    val height = pngr.imgInfo.rows
    val raster = Raster(width, height, Raster.getType(channels, bitDepth))
    val rowSize = raster.n_channel * width

    if (bitDepth <= 8) {
      for (row <- 0 until height) { // also: while(pngr.hasMoreRows())
        val scanline: Array[Byte] = pngr.readRow().asInstanceOf[ImageLineInt].getScanline().map(_.toByte)
        System.arraycopy(scanline, 0, raster.model, row * rowSize, rowSize)
      }
    } else {
      for (row <- 0 until height) { // also: while(pngr.hasMoreRows())
        val scanline: Array[Int] = pngr.readRow().asInstanceOf[ImageLineInt].getScanline()
        System.arraycopy(scanline, 0, raster.model, row * rowSize, rowSize)
      }
    }

    pngr.end()
    new Image(raster)
  }

  def readMimeType(input: InputStream) = {
    try {
      val buff = Array.ofDim[Byte](8)
      input.read(buff)
      val expected = List(0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A) map (_.toByte)
      assert(buff.toList == expected)
      Some(PNGMimeType)
    } catch {
      case _: AssertionError => None
    }
  }
}
