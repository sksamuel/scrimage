package com.sksamuel.scrimage

object Using {

  def resource[A, B <: AutoCloseable](closeable: B)(f: B => A): A =
    try {
      f(closeable)
    } finally {
      closeable.close()
    }
}
