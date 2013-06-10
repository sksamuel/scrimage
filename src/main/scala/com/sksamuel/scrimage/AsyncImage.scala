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

package com.sksamuel.scrimage

import scala.concurrent._
import com.sksamuel.scrimage.Position.Center
import scala.concurrent.ExecutionContext.Implicits.global
import java.awt.Color
import com.sksamuel.scrimage.io.ImageWriter
import com.sksamuel.scrimage.ScaleMethod.Bicubic
import java.io.{InputStream, File}

/** @author Stephen Samuel */
class AsyncImage(image: Image) extends ImageLike[Future[Image]] {

    def fit(targetWidth: Int, targetHeight: Int, color: Color, scaleMethod: ScaleMethod, position: Position): Future[Image] = future {
        image.fit(targetWidth, targetHeight, color, scaleMethod, position)
    }

    def resizeTo(targetWidth: Int, targetHeight: Int, position: Position = Center): Future[Image] = future {
        image.resizeTo(targetWidth, targetHeight, position)
    }

    def scaleTo(targetWidth: Int, targetHeight: Int, scaleMethod: ScaleMethod = Bicubic): Future[Image] = future {
        image.scaleTo(targetWidth, targetHeight, scaleMethod)
    }

    /**
     * Creates a copy of this image with the given filter applied.
     * The original (this) image is unchanged.
     *
     * @param filter the filter to apply. See com.sksamuel.scrimage.Filter.
     *
     * @return A new image with the given filter applied.
     */
    def filter(filter: Filter): Future[Image] = future {
        image.filter(filter)
    }

    /**
     * Returns the underlying image.
     *
     * @return the image that was wrapped when creating this async.
     */
    def toImage: Image = image

    def writer[T <: ImageWriter](format: Format[T]): T = format.writer(image)
    def height: Int = image.height
    def width: Int = image.width
}

object AsyncImage {

    def apply(bytes: Array[Byte]): AsyncImage = Image(bytes).toAsync
    def apply(in: InputStream): AsyncImage = Image(in).toAsync
    def apply(file: File): AsyncImage = Image(file).toAsync
    def apply(image: Image) = new AsyncImage(image)
}
