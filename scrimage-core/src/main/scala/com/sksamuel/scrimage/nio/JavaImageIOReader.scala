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

import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

import com.sksamuel.scrimage.Image

/** @author Stephen Samuel */
object JavaImageIOReader extends Reader {
  def read(bytes: Array[Byte]): Option[Image] = Option(Image(ImageIO.read(new ByteArrayInputStream(bytes))))
}

object JavaImageIO2Reader extends Reader {

  import scala.collection.JavaConverters._

  def read(bytes: Array[Byte]): Option[Image] = {
    ImageIO
      .getImageReaders(new ByteArrayInputStream(bytes))
      .asScala
      .foldLeft(None: Option[Image]) { (valueOpt, reader) =>
        // only bother to read if it hasn't already successfully been read
        valueOpt orElse {
          try {
            reader.setInput(new ByteArrayInputStream(bytes), true, true)
            val params = reader.getDefaultReadParam
            val imageTypes = reader.getImageTypes(0)
            while (imageTypes.hasNext) {
              val imageTypeSpecifier = imageTypes.next()
              val bufferedImageType = imageTypeSpecifier.getBufferedImageType
              if (bufferedImageType == BufferedImage.TYPE_BYTE_GRAY) {
                params.setDestinationType(imageTypeSpecifier)
              }
            }
            val bufferedImage = reader.read(0, params)
            Some(Image(bufferedImage))
          } catch {
            case e: Exception => None
          }
        }
      }
  }
}
