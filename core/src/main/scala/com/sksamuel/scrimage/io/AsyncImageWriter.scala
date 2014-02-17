package com.sksamuel.scrimage.io

import java.io.{ByteArrayInputStream, InputStream, File, OutputStream}
import scala.concurrent.{Future, ExecutionContext}
import org.apache.commons.io.{IOUtils, FileUtils}

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
