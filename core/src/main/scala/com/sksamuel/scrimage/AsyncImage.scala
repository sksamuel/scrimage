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
import com.sksamuel.scrimage.ScaleMethod.Bicubic
import java.io.{ InputStream, File }
import com.sksamuel.scrimage.io.{ ImageWriter, AsyncImageWriter }

/** @author Stephen Samuel */
class AsyncImage(image: Image)(implicit executionContext: ExecutionContext) extends ImageLike[Future[AsyncImage]] {

  override def clear(color: Color = X11Colorlist.White) = image.clear(color)
  override def empty = image.empty
  override def copy = image.copy
  override def pixels: Array[Int] = image.pixels

  override def map(f: (Int, Int, Int) => Int): Future[AsyncImage] = Future {
    AsyncImage(image.map(f))
  }

  override def foreach(f: (Int, Int, Int) => Unit) {
    image.foreach(f)
  }

  def fit(targetWidth: Int,
          targetHeight: Int,
          color: Color = X11Colorlist.White,
          scaleMethod: ScaleMethod = Bicubic,
          position: Position = Position.Center): Future[AsyncImage] = Future {
    AsyncImage(image.fit(targetWidth, targetHeight, color, scaleMethod, position))
  }

  def pixel(x: Int, y: Int): Int = image.pixel(x, y)

  def padTo(targetWidth: Int,
            targetHeight: Int,
            color: Color = X11Colorlist.White): Future[AsyncImage] = Future {
    AsyncImage(image.padTo(targetWidth, targetHeight, color))
  }

  def resizeTo(targetWidth: Int,
               targetHeight: Int,
               position: Position = Center,
               background: Color = X11Colorlist.White): Future[AsyncImage] = Future {
    AsyncImage(image.resizeTo(targetWidth, targetHeight, position))
  }

  def scaleTo(targetWidth: Int, targetHeight: Int, scaleMethod: ScaleMethod = Bicubic): Future[AsyncImage] = Future {
    AsyncImage(image.scaleTo(targetWidth, targetHeight, scaleMethod))
  }

  /** Creates a copy of this image with the given filter applied.
    * The original (this) image is unchanged.
    *
    * @param filter the filter to apply. See com.sksamuel.scrimage.Filter.
    *
    * @return A new image with the given filter applied.
    */
  def filter(filter: Filter): Future[AsyncImage] = Future {
    AsyncImage(image.filter(filter))
  }

  /** Returns the underlying image.
    *
    * @return the image that was wrapped when creating this async.
    */
  def toImage: Image = image

  def writer[T <: ImageWriter](format: Format[T]): AsyncImageWriter[T] = new AsyncImageWriter[T](format.writer(image))

  def height: Int = image.height
  def width: Int = image.width
}

object AsyncImage {

  def apply(bytes: Array[Byte])(implicit executionContext: ExecutionContext): Future[AsyncImage] = Future {
    Image(bytes).toAsync
  }
  def apply(in: InputStream)(implicit executionContext: ExecutionContext): Future[AsyncImage] = Future {
    Image(in).toAsync
  }
  def apply(file: File)(implicit executionContext: ExecutionContext): Future[AsyncImage] = Future {
    Image(file).toAsync
  }
  def apply(image: Image)(implicit executionContext: ExecutionContext) = new AsyncImage(image)
}
