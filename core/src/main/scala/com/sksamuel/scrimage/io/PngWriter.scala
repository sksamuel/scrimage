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

import java.io.{ ByteArrayInputStream, OutputStream }
import javax.imageio.ImageIO
import com.sksamuel.scrimage.Image
import org.apache.commons.io.IOUtils
import org.apache.commons.io.output.ByteArrayOutputStream
import thirdparty.pngtastic.{ PngOptimizer, PngImage }

/** @author Stephen Samuel */
class PngWriter(image: Image, compressionLevel: Int) extends ImageWriter {

  def withMaxCompression = withCompression(9)
  def withCompression(compressionLevel: Int): PngWriter = new PngWriter(image, compressionLevel)

  def write(out: OutputStream) {

    // todo need to adapt pngtastic to accept raw image
    val ba = new ByteArrayOutputStream()
    ImageIO.write(image.awt, "png", ba)
    val png = new PngImage(new ByteArrayInputStream(ba.toByteArray))

    val optimizer = new PngOptimizer()
    val optimized = optimizer.optimize(png, false, compressionLevel)
    optimized.writeDataOutputStream(out)

    IOUtils.closeQuietly(out)
  }
}
object PngWriter {
  def apply(image: Image) = new PngWriter(image, 0)
}