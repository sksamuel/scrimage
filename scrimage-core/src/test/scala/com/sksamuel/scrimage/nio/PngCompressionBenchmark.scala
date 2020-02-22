package com.sksamuel.scrimage.nio

import com.sksamuel.scrimage.ImmutableImage
import java.io.File
import scala.concurrent.duration._

object PngCompressionBenchmark extends App {

  val image = ImmutableImage.fromResource("/com/sksamuel/scrimage/jazz.jpg")

  for (c <- 0 to 9) {
    benchmarkN(1, string => println(s"Compression level $c took " + string)) {
      image.output(File.createTempFile(s"compressiontest$c", ".png"))(new PngWriter(c))
    }
  }

  def benchmarkN[A](n: Int, pr: String => Unit)(f: => A): Unit = {
    val stopwatch = new Stopwatch
    stopwatch.start()
    for (_ <- 0 until n) f
    stopwatch.stop()
    pr(s"${stopwatch.elapsed.toMillis / n}ms")
  }
}

class Stopwatch {
  var _start = 0L
  var _end = 0L
  def start(): Unit = {
    _start = System.nanoTime()
  }
  def stop(): Unit = {
    _end = System.nanoTime()
  }
  def elapsed: FiniteDuration = (_end - _start).nanos
}
