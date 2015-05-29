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

import java.io._
import org.apache.commons.io.{ IOUtils, FileUtils }

/** @author Stephen Samuel */
trait ImageWriter {

  /** Writes out this image to the given stream.
    *
    * @param out the stream to write out to
    */
  def write(out: OutputStream)

  /** Writes out this image to the given filepath.
    *
    * @param path the path to write out to.
    */
  def write(path: String) {
    write(new File(path))
  }

  /** Writes out this image to the given file.
    *
    * @param file the file to write out to.
    */
  def write(file: File) {
    val out = FileUtils.openOutputStream(file)
    write(out)
    IOUtils.closeQuietly(out)
  }

  /** Writes out this image to a byte array.
    *
    * @return the byte array
    */
  def write(): Array[Byte] = {
    val baos = new ByteArrayOutputStream()
    write(baos)
    baos.toByteArray
  }

  /** Returns an input stream that reads from this image.
    */
  def toStream: InputStream = {
    val bytes = write()
    new ByteArrayInputStream(bytes)
  }
}