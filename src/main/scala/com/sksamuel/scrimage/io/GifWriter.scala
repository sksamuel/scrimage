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

import com.sksamuel.scrimage.Image
import java.io.OutputStream
import javax.imageio.{IIOImage, ImageWriteParam, ImageIO}
import org.apache.commons.io.IOUtils
import javax.imageio.stream.MemoryCacheImageOutputStream

/** @author Stephen Samuel */
class GifWriter(image: Image, progressive: Boolean) extends ImageWriter {

    def withProgressive(progressive: Boolean): GifWriter = new GifWriter(image: Image, progressive)

    def write(out: OutputStream) {
        val writer = ImageIO.getImageWritersByFormatName("gif").next()
        val params = writer.getDefaultWriteParam
        if (progressive)
            params.setProgressiveMode(ImageWriteParam.MODE_DEFAULT)
        else
            params.setProgressiveMode(ImageWriteParam.MODE_DISABLED)

        val output = new MemoryCacheImageOutputStream(out)
        writer.setOutput(output)
        writer.write(null, new IIOImage(image.awt, null, null), params)
        writer.dispose()
        output.close()
        IOUtils.closeQuietly(out)
    }
}

object GifWriter {
    def apply(image: Image): GifWriter = new GifWriter(image, false)
}