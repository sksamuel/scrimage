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

package com.sksamuel.scrimage.nio

import java.awt.image.{BufferedImage, DataBufferInt, SinglePixelPackedSampleModel}
import java.io.OutputStream
import javax.imageio.ImageIO

import ar.com.hjg.pngj.{FilterType, ImageInfo, ImageLineInt}
import com.sksamuel.scrimage.Image

/** @author Stephen Samuel */
case class PngWriter(compressionLevel: Int) extends ImageWriter {

  import PngWriter._

  def withMaxCompression: PngWriter = MaxCompression
  def withMinCompression: PngWriter = MinCompression
  def withCompression(c: Int): PngWriter = copy(compressionLevel = c)

  override def write(image: Image, out: OutputStream): Unit = {

    if (image.awt.getType == BufferedImage.TYPE_INT_ARGB) {
      val imi = new ImageInfo(image.width, image.height, 8, true)

      val writer = new ar.com.hjg.pngj.PngWriter(out, imi)
      writer.setCompLevel(compressionLevel)
      writer.setFilterType(FilterType.FILTER_DEFAULT)

      val db = image.awt.getRaster.getDataBuffer.asInstanceOf[DataBufferInt]
      if (db.getNumBanks != 1) throw new RuntimeException("This method expects one bank")

      val samplemodel = image.awt.getSampleModel.asInstanceOf[SinglePixelPackedSampleModel]
      val line = new ImageLineInt(imi)
      val dbbuf = db.getData

      for ( row <- 0 until imi.rows ) {
        var elem = samplemodel.getOffset(0, row)
        var j = 0
        for ( col <- 0 until imi.cols ) {
          val sample = dbbuf(elem)
          elem = elem + 1
          line.getScanline()(j) = (sample & 0xFF0000) >> 16; // R
          j = j + 1
          line.getScanline()(j) = (sample & 0xFF00) >> 8; // G
          j = j + 1
          line.getScanline()(j) = sample & 0xFF; // B
          j = j + 1
          line.getScanline()(j) = ((sample & 0xFF000000) >> 24) & 0xFF; // A
          j = j + 1
        }
        writer.writeRow(line, row)
      }
      writer.end() // end calls close

    } else {
      ImageIO.write(image.awt, "png", out)
    }
  }
}

object PngWriter {

  val MaxCompression = PngWriter(9)
  val MinCompression = PngWriter(1)
  val NoCompression = PngWriter(0)

  def apply(): PngWriter = MaxCompression
}