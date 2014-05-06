/*
   Copyright 2013 Jaap Taal

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

import java.io.{ByteArrayInputStream, InputStream, File, OutputStream}
import scala.concurrent.{Future, ExecutionContext}

class AsyncImageWriter[T <: ImageWriter](writer: T) {


  /**
   * Writes out this image to the given stream.
   *
   * @param out the stream to write out to
   */
  def write(out: OutputStream)(implicit executionContext: ExecutionContext): Future[Unit] = Future {
    writer.write(out)
  }

  /**
   * Writes out this image to the given filepath.
   *
   * @param path the path to write out to.
   */
  def write(path: String)(implicit executionContext: ExecutionContext): Future[Unit] = Future {
    writer.write(path)
  }

  /**
   * Writes out this image to the given file.
   *
   * @param file the file to write out to.
   */
  def write(file: File)(implicit executionContext: ExecutionContext):Future[Unit] = Future {
    writer.write(file)
  }

  /**
   * Writes out this image to a byte array.
   *
   * @return the byte array
   */
  def write()(implicit executionContext: ExecutionContext): Future[Array[Byte]] = Future {
    writer.write()
  }

  /**
   * Returns an input stream that reads from this image.
   */
  def toStream(implicit executionContext: ExecutionContext): Future[InputStream] = Future {
    val bytes = writer.write()
    new ByteArrayInputStream(bytes)
  }

}
