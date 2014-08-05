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

import com.sksamuel.scrimage.io._
import com.sksamuel.scrimage.io.PngWriter

/** @author Stephen Samuel */
sealed trait Format[T <: ImageWriter] {
  /** Returns a new ImageWriter for writing the given image
    * configured with the default configuration settings
    * for this image format type.
    *
    */
  def writer(image: Image): T
}
object Format {
  case object PNG extends Format[PngWriter] {
    def writer(image: Image) = PngWriter(image)
  }
  case object JPEG extends Format[JpegWriter] {
    def writer(image: Image) = JpegWriter(image)
  }
  case object GIF extends Format[GifWriter] {
    def writer(image: Image) = GifWriter(image)
  }
}